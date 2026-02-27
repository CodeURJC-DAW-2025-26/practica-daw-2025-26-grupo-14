package es.codeurjc.daw.library.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import es.codeurjc.daw.library.service.ProductService;


@Controller
public class ShopWebController {
	
	@Autowired
	private ProductService productService;

	@GetMapping("/")
	public String main(Model model) {
		model.addAttribute("products", productService.getAllProducts());
		return "main";
	}

	@GetMapping("/product")
	public String product() {
		return "product";
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
