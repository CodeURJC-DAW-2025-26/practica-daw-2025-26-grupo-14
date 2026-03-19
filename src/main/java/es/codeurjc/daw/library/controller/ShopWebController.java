package es.codeurjc.daw.library.controller;
 
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.daw.library.dto.DtoMapper;
import es.codeurjc.daw.library.dto.ProductDto;
import es.codeurjc.daw.library.model.Product;
import es.codeurjc.daw.library.model.Rating;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.repository.RatingRepository;
import es.codeurjc.daw.library.service.ProductService;
import es.codeurjc.daw.library.service.UserService;



@RestController
public class ShopWebController {
	
	@Autowired
	private ProductService productService;

	@Autowired
	private UserService userService;

	@Autowired
	private RatingRepository ratingRepository;

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
	public Map<String, Object> main(Principal principal) {
		Map<String, Object> response = new HashMap<>();
		response.put("products", DtoMapper.toProductDtoList(productService.getAllProducts()));

		if (principal != null) {
			User user = userService.findByName(principal.getName()).orElse(null);
			String city = user != null ? user.getCity() : null;

			List<ProductDto> localProducts = city != null ? DtoMapper.toProductDtoList(productService.searchProductsBySellerCity(city)) : List.of();
			response.put("localProducts", localProducts);
			response.put("hasLocalProducts", !localProducts.isEmpty());
			response.put("currentUser", user != null ? DtoMapper.toDto(user) : null);
			response.put("isAuthenticated", true);
		} else {
			response.put("isAuthenticated", false);
		}

		return response;
	}

	@GetMapping("/product/{id}")
	public ResponseEntity<Map<String, Object>> product(@PathVariable Long id, Principal principal) {
		Optional<Product> product = productService.getProductById(id);
		if (product.isEmpty() || product.get().getSeller() == null) {
			return ResponseEntity.notFound().build();
		}

		Product prod = product.get();
		Map<String, Object> response = new HashMap<>();
		response.put("product", DtoMapper.toDto(prod));
		response.put("sellerProducts", DtoMapper.toProductDtoList(productService.getProductsBySeller(prod.getSeller())));
		response.put("products", DtoMapper.toProductDtoList(productService.getAllProducts()));

		boolean isOwner = principal != null && principal.getName().equals(prod.getSeller().getName());
		response.put("isOwner", isOwner);

		if (prod.getNumberImages() != 0) {
			response.put("mainImageId", prod.getImage(0).getId());
		}

		Long sellerId = prod.getSeller().getId();
		double avg = ratingRepository.findByRatedId(sellerId).stream()
			.mapToInt(Rating::getRating)
			.average()
			.orElse(0.0);

		int avgPercent = (int) Math.round((avg / 5.0) * 100);

		response.put("avgRating", avg);
		response.put("avgRatingPercent", avgPercent);

		return ResponseEntity.ok(response);
	}
    
	@GetMapping("/shop/search")
	public List<ProductDto> searchPage() {
		return DtoMapper.toProductDtoList(productService.getAllProducts());
	}

	//Para AJAX
	@GetMapping("/products/page")
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
