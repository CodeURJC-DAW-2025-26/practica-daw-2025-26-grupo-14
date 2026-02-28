package es.codeurjc.daw.library.controller;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import es.codeurjc.daw.library.model.Product;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.service.UserService;
import es.codeurjc.daw.library.service.ProductService;


@Controller
public class UserController {

    @Autowired
    private UserService userService;
	
	@Autowired
	private ProductService productService;

	@GetMapping("/login")
	public String login() {
		return "login";
    }

    @GetMapping("/loginerror")
	public String loginError() {
		return "loginerror";
	}

    @GetMapping("/logout")
	public String logout() {
		return "main";
	}

    @GetMapping("/register")
	public String register() {
		return "register";
	}


    @GetMapping("/user_account/{id}")
	public String profile(@PathVariable Long id, Model model) {
		Optional<User> user = userService.getUserById(id);
		if (user.isPresent()) {

           List<Product> products = productService.getProductsBySeller(user.get());
            model.addAttribute("user", user.get());
            model.addAttribute("products", products);
		    return "user_account";}
        else{
                return "pageerror";
            }
    }



    

    @GetMapping("/publish")
    public String publishForm() {
        return "publish";
    }

    @GetMapping("/my_listings")
    public String my_listings(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "my-listings";
    }

    @GetMapping("/my_deals")
    public String my_deals() {
        return "my_deals";
    }
    
    

}

