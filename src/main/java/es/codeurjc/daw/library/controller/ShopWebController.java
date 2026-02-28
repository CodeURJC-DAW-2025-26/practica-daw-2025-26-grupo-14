package es.codeurjc.daw.library.controller;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import es.codeurjc.daw.library.service.ProductService;
import es.codeurjc.daw.library.model.Product;


@Controller
public class ShopWebController {
	
	@Autowired
	private ProductService productService;

	@GetMapping("/")
	public String main(Model model) {
		model.addAttribute("products", productService.getAllProducts());
		return "main";
	}

	@GetMapping("/product/{id}")
	public String product(@PathVariable Long id, Model model) {
		Optional<Product> product = productService.getProductById(id);
		if (product.isPresent()) {
			Product prod = product.get();
			model.addAttribute("product", prod);             
			model.addAttribute("seller_products", productService.getProductsBySeller(prod.getSeller()));
		
			model.addAttribute("products", productService.getAllProducts());
		return "product";}
		else {
			return "main";
		}
	}

	@GetMapping("/normal_search")
	public String searchPage() {
		return "normal_search";
	}
	
	@GetMapping("/category_search")
	public String categoryhPage() {
		return "category_search";
	}

}
