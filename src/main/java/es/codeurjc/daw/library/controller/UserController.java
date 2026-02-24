package es.codeurjc.daw.library.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class UserController {
	
	@GetMapping("/login")
	public String login() {
		return "login";
	}

    @GetMapping("/register")
	public String register() {
		return "register";
	}

    @GetMapping("/profile")
	public String profile() {
		return "user_account";
	}

    @GetMapping("/publish")
    public String publishForm() {
        return "publish";
    }

    @GetMapping("/my_listings")
    public String my_listings() {
        return "my-listings";
    }

    @GetMapping("/my_deals")
    public String my_deals() {
        return "my_deals";
    }
    
    

}

