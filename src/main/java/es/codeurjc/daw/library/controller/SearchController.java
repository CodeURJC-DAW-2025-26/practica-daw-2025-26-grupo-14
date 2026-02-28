package es.codeurjc.daw.library.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.daw.library.model.Product;
import es.codeurjc.daw.library.service.ProductService;

@Controller
public class SearchController {

    @Autowired
    private ProductService productService;

    @GetMapping("/search")
    public String search( @RequestParam(required = false) String keyword, Model model) {

        List<Product> results;

        if (keyword == null || keyword.trim().isEmpty()) {
            model.addAttribute("message", "Please enter a search term.");
            results = productService.getAllProducts(); // o lista vacía
        } else {
            results = productService.searchProductsByName(keyword);

            if (results.isEmpty()) {
                model.addAttribute("message", "No products found for: " + keyword);
            }
        }

        model.addAttribute("results", results);

        return "search";
    }
    
    
}
