package es.codeurjc.daw.library.controller.auth;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.security.jwt.AuthResponse;
import es.codeurjc.daw.library.security.jwt.AuthResponse.Status;
import es.codeurjc.daw.library.security.jwt.LoginRequest;
import es.codeurjc.daw.library.security.jwt.UserLoginService;
import es.codeurjc.daw.library.service.UserService;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1/auth")
public class LoginController {
	
	@Autowired
	private UserLoginService userService;
	
	@Autowired
	private UserService userDbService;

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(
			@RequestBody LoginRequest loginRequest,
			HttpServletResponse response) {
		
		return userService.login(response, loginRequest);
	}

	@PostMapping("/refresh")
	public ResponseEntity<AuthResponse> refreshToken(
			@CookieValue(name = "RefreshToken", required = false) String refreshToken, HttpServletResponse response) {

		return userService.refresh(response, refreshToken);
	}

	@PostMapping("/logout")
	public ResponseEntity<AuthResponse> logOut(HttpServletResponse response) {
		return ResponseEntity.ok(new AuthResponse(Status.SUCCESS, userService.logout(response)));
	}

	@GetMapping("/me")
	public ResponseEntity<?> me(@AuthenticationPrincipal UserDetails userDetails) {
		if (userDetails == null) {
			return ResponseEntity.status(401).build();
		}

		User user = userDbService.findByName(userDetails.getUsername())
			.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
		return ResponseEntity.ok(Map.of(
			"name", userDetails.getUsername(), 
			"roles", userDetails.getAuthorities().stream().map(a -> a.getAuthority().replace("ROLE_", "")).toList(), 
			"id", user.getId()
			));
	}
}
