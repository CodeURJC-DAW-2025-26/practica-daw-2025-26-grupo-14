package es.codeurjc.daw.library.controller;
 
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.codeurjc.daw.library.model.Product;
import es.codeurjc.daw.library.model.Rating;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.repository.RatingRepository;
import es.codeurjc.daw.library.service.ProductService;
import es.codeurjc.daw.library.service.UserService;
import jakarta.servlet.http.HttpServletRequest;



@Controller
public class ShopWebController {
	
	@Autowired
	private ProductService productService;

	@Autowired
	private UserService userService;

	@Autowired
	private RatingRepository ratingRepository;

	@ModelAttribute
	public void addAttributes(Model model, HttpServletRequest request) {

		Principal principal = request.getUserPrincipal();

		if (principal != null) {

			model.addAttribute("logged", true);
			model.addAttribute("userName", principal.getName());
			model.addAttribute("admin", request.isUserInRole("ADMIN"));

		} else {
			model.addAttribute("logged", false);
		}
	}


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
			if(product.get().getNumberImages() != 0){
				model.addAttribute("main_image", product.get().getImage(0));
			}

			Long sellerId = product.get().getSeller().getId();
			double avg = ratingRepository.findByRatedId(sellerId).stream()
				.mapToInt(Rating::getRating)
				.average()
				.orElse(0.0);
			
			int avgPercent = (int)Math.round((avg / 5.0) * 100);
			
			model.addAttribute("avgRating", avg);
			model.addAttribute("avgRatingPercent", avgPercent);
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

	//Para AJAX
	@GetMapping("/products/page")
	@ResponseBody
	public Map<String, Object> productsPage(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false) String category,
			@RequestParam(required = false) Double minPrice,
			@RequestParam(required = false) Double maxPrice,
			@RequestParam(required = false) Integer minSellerRate,
    		@RequestParam(required = false) Long sellerId) {

		Page<Product> result = productService.getProductsPage(page, size, keyword, category, minPrice, maxPrice, minSellerRate, sellerId);

		List<Map<String, Object>> products = result.getContent().stream().map(p -> {
			Map<String, Object> dto = new HashMap<>();
			dto.put("id", p.getId());
			dto.put("name", p.getName());
			dto.put("price", p.getPrice());
			dto.put("shortDescription", p.getShortDescription());
			dto.put("createdAt", p.getCreatedAt());//createdAt


			dto.put("imageId", p.getNumberImages() != 0 ? p.getImage(0).getId() : null);
			return dto;
		}).toList();

		return Map.of("products", products, "hasNext", result.hasNext());
	}

	    

}
