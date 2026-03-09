package es.codeurjc.daw.library.controller;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.codeurjc.daw.library.model.Product;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.repository.ProductRepository;
import es.codeurjc.daw.library.service.UserService;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;


@Controller
public class AdminController {

	@Autowired
	private UserService userService;

	@Autowired
	private ProductRepository productRepository;
	
	@GetMapping("/administrator")
	public String administrator() {
		return "administrator";
	}

    @GetMapping("/admin_listings")
	public String listings() {
		return "admin_listings";
	}

    @GetMapping("/admin_users")
	public String users(Model model) {
		model.addAttribute("users", userService.getAllUsers());
		return "admin_users";
	}

	@GetMapping("/ban_user/{id}")
	public String banUser(@PathVariable Long id, Model model) {
    userService.banUser(id);
    return "redirect:/admin_users";
}


    @GetMapping("/admin_stats")
public String adminStats(Model model) {
    List<Object[]> rows = productRepository.countProductsByCategory(); // category, count
    long total = productRepository.count();

    List<Map<String,Object>> categoryStats = rows.stream().map(r -> {
        String category = (String) r[0];
        long count = (Long) r[1];
        long percent = total == 0 ? 0 : Math.round((count * 100.0) / total);
        return Map.<String, Object>of(
            "category", category,
            "count", count,
            "percent", percent,
			"widthStyle", "width: " + percent + "%;"
        );
    }).toList();
	

    model.addAttribute("categoryStats", categoryStats);
    return "admin_stats";
}



}
