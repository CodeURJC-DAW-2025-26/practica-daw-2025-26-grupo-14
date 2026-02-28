package es.codeurjc.daw.library.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import es.codeurjc.daw.library.service.UserService;


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

}
