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
    public String search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            Model model) {

        List<Product> results = List.of();

        if (category != null && !category.trim().isEmpty()) {

            // Cuando haces click en la categoría
            results = productService.searchProductsByCategory(category);

            if (results.isEmpty()) {
                model.addAttribute("message",
                        "No products found in category: " + category);
            }

        } else if (keyword != null && !keyword.trim().isEmpty()) {

            // Cuando usas la barra de búsqueda
            results = productService.searchProductsByName(keyword);

            if (results.isEmpty()) {
                model.addAttribute("message",
                        "No products found for: " + keyword);
            }

        } else {
            model.addAttribute("message", "Please enter a search term.");
        }

        model.addAttribute("results", results);
        return "search";
    }
    
    
}
