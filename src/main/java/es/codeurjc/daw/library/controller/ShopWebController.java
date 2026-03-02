package es.codeurjc.daw.library.controller;
 
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import es.codeurjc.daw.library.model.Product;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.service.ProductService;
import es.codeurjc.daw.library.service.UserService;



@Controller
public class ShopWebController {
	
	@Autowired
	private ProductService productService;

	@Autowired
	private UserService userService;

	/*@ModelAttribute
	public void addAttributes(Model model, HttpServletRequest request) {

		Principal principal = request.getUserPrincipal();

		if (principal != null) {

			model.addAttribute("logged", true);
			model.addAttribute("userName", principal.getName());
			model.addAttribute("admin", request.isUserInRole("ADMIN"));

		} else {
			model.addAttribute("logged", false);
		}
	}*/


	@GetMapping("/")
	public String main(Model model, Principal principal) {
		
		model.addAttribute("products", productService.getAllProducts());

		if (principal != null) {
			User user = userService.findByName(principal.getName()).orElse(null);
			
			String city = user != null ? user.getCity() : null;

			List <Product> localProducts = city != null ? productService.searchProductsBySellerCity(city) : null;
			model.addAttribute("localProducts", localProducts);
			boolean hasLocalProducts = localProducts != null && !localProducts.isEmpty();
			model.addAttribute("hasLocalProducts", hasLocalProducts);

		}
		return "main";
	}

	@GetMapping("/product/{id}")
	public String product(@PathVariable Long id, Model model) {
		Optional<Product> product = productService.getProductById(id);
		if (product.isPresent() && product.get().getSeller() != null) {
			Product prod = product.get();
			model.addAttribute("product", prod);
			// list other products from the same seller; seller is guaranteed non‑null above
			model.addAttribute("seller_products", productService.getProductsBySeller(prod.getSeller()));
			model.addAttribute("products", productService.getAllProducts());
			if (model.getAttribute("userName") != null && model.getAttribute("userName").equals(prod.getSeller().getName())) {
				model.addAttribute("isOwner", true);
			}
			return "product";
		} else {
			// either the product doesn’t exist or it has no associated seller
			// return an error page (template: pageerror.html)
			return "pageerror";
		}

	}
    
	@GetMapping("/shop/search")
	public String searchPage() {
		return "search";
	}
}
