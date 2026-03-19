package es.codeurjc.daw.library.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.daw.library.dto.DtoMapper;
import es.codeurjc.daw.library.dto.ProductDto;
import es.codeurjc.daw.library.service.ProductService;

@RestController
public class SearchController {

    @Autowired
    private ProductService productService;

    @GetMapping("/search")
    public List<ProductDto> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) {

        List<ProductDto> results = List.of();

        if (category != null && !category.trim().isEmpty()) {
            results = DtoMapper.toProductDtoList(productService.searchProductsByCategory(category));
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            results = DtoMapper.toProductDtoList(productService.searchProductsByName(keyword));
        }

        return results;
    }
}
