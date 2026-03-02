package es.codeurjc.daw.library.controller;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.codeurjc.daw.library.model.Product;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.service.UserService;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;


@Controller
public class AdminController {

	@Autowired
	private UserService userService;
	
	@GetMapping("/administrator")
	public String administrator() {
		return "administrator";
	}

    @GetMapping("/admin_listings")
	public String listings() {
		return "admin_listings";
	}

    @GetMapping("/admin_users")
	public String users(Model model) {
		model.addAttribute("users", userService.getAllUsers());
		return "admin_users";
	}

	@GetMapping("/ban_user/{id}")
	public String banUser(@PathVariable Long id, Model model) {
    userService.banUser(id);
    return "redirect:/admin_users";
}


    @GetMapping("/admin_stats")
	public String stats() {
		return "admin_stats";
	}

	//Para AJAX
	@GetMapping("/users/page")
	@ResponseBody
	public Map<String, Object> usersPage(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		Page<User> result = userService.getUsersPage(page, size);

		List<Map<String, Object>> products = result.getContent().stream().map(u -> {
			Map<String, Object> dto = new HashMap<>();
			dto.put("id", u.getId());
			dto.put("name", u.getName());
			dto.put("email", u.getEmail());
			dto.put("isBanned", u.getIsBanned());
			return dto;
		}).toList();

		return Map.of("products", products, "hasNext", result.hasNext());
	}

}
