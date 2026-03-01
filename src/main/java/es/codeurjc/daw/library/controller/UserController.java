package es.codeurjc.daw.library.controller;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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

	
	@Autowired
    PasswordEncoder passwordEncoder;

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

	@PostMapping("/register")
	public String register(Model model, User user, String myPassword, String confirmPassword) {
		if (!user.getPassword().equals(confirmPassword)) {
			model.addAttribute("error", "Passwords do not match");
			return "register";
		}
		user.setPassword(passwordEncoder.encode(confirmPassword));
		user.setRoles("USER");
		userService.save(user);
		return "redirect:/login";
	}

	@PostMapping("/edituser/{id}")
	public String editUser(Model model, @PathVariable Long id, User user, String confirmPassword) {
		if (!user.getPassword().equals(confirmPassword)) {
			model.addAttribute("error", "Passwords do not match");
			return "register";
		}
		user.setPassword(passwordEncoder.encode(confirmPassword));
		user.setRoles("USER");
		userService.save(user);
		return "redirect:/user_account/" + id;
	}
	

    @PostMapping("/newproduct")
	public String postNewProduct(Model model, Product product, String sellerName) {
		Optional<User> seller = userService.findByName(sellerName);
		if (!seller.isPresent()) {
			model.addAttribute("error", "You should be logged in to publish a product.");
			return "publish";
		}
		product.setSeller(seller.get());
		product.setDate();

		productService.save(product);
		model.addAttribute("products", productService.getAllProducts());
		return "my-listings";
	}

    @GetMapping("/editproduct/{id}")
	public String editProduct(Model model, @PathVariable long id) {

		Optional<Product> product = productService.getProductById(id);
		if (product.isPresent()) {
			Product p = product.get();
			model.addAttribute("product", p);
			model.addAttribute("contactOptions", List.of(
				Map.of("value", "Chat", "selected", "Chat".equals(p.getContactPreference())),
				Map.of("value", "Phone", "selected", "Phone".equals(p.getContactPreference())),
				Map.of("value", "Both", "selected", "Both".equals(p.getContactPreference()))
			));

			model.addAttribute("categoryOptions", List.of(
				Map.of("value", "Electronics", "selected", "Electronics".equals(p.getCategory())),
				Map.of("value", "Clothing", "selected", "Clothing".equals(p.getCategory())),
				Map.of("value", "Home", "selected", "Home".equals(p.getCategory())),
				Map.of("value", "Sports", "selected", "Sports".equals(p.getCategory())),
				Map.of("value", "Books", "selected", "Books".equals(p.getCategory())),
				Map.of("value", "Other", "selected", "Other".equals(p.getCategory()))
			));

			model.addAttribute("conditionOptions", List.of(
				Map.of("value", "New", "selected", "New".equals(p.getCondition())),
				Map.of("value", "Like new", "selected", "Like new".equals(p.getCondition())),
				Map.of("value", "Used", "selected", "Used".equals(p.getCondition())),
				Map.of("value", "For parts", "selected", "For parts".equals(p.getCondition()))
			));

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
			if (model.getAttribute("userName") != null && model.getAttribute("userName").equals(user.get().getName())) {
				model.addAttribute("isOwner", true);
			}
            model.addAttribute("products", products);
		    return "user_account";}
        else{
                return "pageerror";
            }
    }

	@GetMapping("/edituser/{id}")
	public String editUser(@PathVariable Long id, Model model) {
		Optional<User> user = userService.getUserById(id);
		if (user.isPresent()) {
			model.addAttribute("user", user.get());
			return "register";
		} else {
			return "pageerror";
		}
	}



	@PostMapping("/deleteproduct/{id}")
	public String deleteProduct(@PathVariable Long id, Model model) {
			productService.delete(id);
			return "redirect:/my_listings";

	}

	@PostMapping("/deleteuser/{id}")
	public String deleteUser(@PathVariable Long id, Model model) {	
		for (Product p : productService.getProductsBySeller(userService.getUserById(id).get())) {
			productService.delete(p.getId());
		}
		userService.delete(id);

		return "redirect:/login";
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

