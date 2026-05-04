package es.codeurjc.daw.library.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import es.codeurjc.daw.library.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalControllerAdvice {

	@Autowired
	UserRepository userRepository;

    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();

		if (principal != null) {
			String username = principal.getName();

			// Only mark as logged if we can find the user in the database
			userRepository.findByName(username).ifPresentOrElse(user -> {
				model.addAttribute("logged", true);
				model.addAttribute("userName", username);
				model.addAttribute("myid", user.getId());
				model.addAttribute("admin", request.isUserInRole("ADMIN"));
			}, () -> {
				model.addAttribute("logged", false);
			});

		} else {
			model.addAttribute("logged", false);
		}
    }
    
}
