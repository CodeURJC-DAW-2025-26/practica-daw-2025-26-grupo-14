package es.codeurjc.daw.library.controller;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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


    @GetMapping("/register")
	public String register() {
		return "register";
	}

    @PostMapping("/newproduct")
	public String postNewProduct(Model model, Product product, String sellerName) {
		Optional<User> seller = userService.findByName(sellerName);
		if (!seller.isPresent()) {
			model.addAttribute("error", "You should be logged in to publish a product.");
			return "publish";
		}
		product.setSeller(seller.get());

		productService.save(product);
		model.addAttribute("products", productService.getAllProducts());
		return "my-listings";
	}

    @GetMapping("/editproduct/{id}")
	public String editProduct(Model model, @PathVariable long id) {

		Optional<Product> product = productService.getProductById(id);
		if (product.isPresent()) {
			model.addAttribute("product", product.get());
			return "publish";
		} else {
			return "pageerror";
		}
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

	@PostMapping("/deleteproduct/{id}")
	public String deleteProduct(@PathVariable Long id, Model model) {
		Optional<Product> product = productService.getProductById(id);
		if (product.isPresent()) {
			productService.delete(id);
			model.addAttribute("product", product.get());
		} else {
			model.addAttribute("error", "The product with id " + id + " does not exist.");
		}
		
		return "my-listings";
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

