package es.codeurjc.daw.library.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import es.codeurjc.daw.library.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.method.AuthorizeReturnObject;
import org.springframework.ui.Model;

@ControllerAdvice
public class GlobalControllerAdvice {

	@Autowired
	UserRepository userRepository;

    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();

		if (principal != null) {
			String username = principal.getName();

			model.addAttribute("logged", true);
			model.addAttribute("userName", username);
			model.addAttribute("myid", userRepository.findByName(username).get().getId());
			model.addAttribute("admin", request.isUserInRole("ADMIN"));

		} else {
			model.addAttribute("logged", false);
		}
    }
    
}
