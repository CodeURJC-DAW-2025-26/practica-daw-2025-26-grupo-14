package es.codeurjc.daw.library.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class AdminController {
	
	@GetMapping("/administrator")
	public String administrator() {
		return "administrator";
	}

    @GetMapping("/adminlistings")
	public String listings() {
		return "admin_listings";
	}

    @GetMapping("/users")
	public String users() {
		return "admin_users";
	}

    @GetMapping("/adminstats")
	public String stats() {
		return "admin_stats";
	}

}
