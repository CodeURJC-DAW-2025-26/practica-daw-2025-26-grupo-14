package es.codeurjc.daw.library.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class ShopWebController {
	
	@GetMapping("/")
	public String main() {
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
