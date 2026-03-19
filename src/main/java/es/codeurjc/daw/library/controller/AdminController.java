package es.codeurjc.daw.library.controller;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.daw.library.dto.DtoMapper;
import es.codeurjc.daw.library.model.Product;
import es.codeurjc.daw.library.repository.ProductRepository;
import es.codeurjc.daw.library.repository.RatingRepository;
import es.codeurjc.daw.library.repository.UserRepository;
import es.codeurjc.daw.library.service.ProductService;
import es.codeurjc.daw.library.service.UserService;

import java.util.Optional;


@RestController
public class AdminController {

	@Autowired
	private UserService userService;

	@Autowired
	private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private RatingRepository ratingRepository;
	
	@GetMapping("/administrator")
	public Map<String, Object> administrator() {
		return Map.of("status", "ok");
	}

    @GetMapping("/admin_listings")
	public Map<String, Object> listings() {
        List<Product> reportedProducts = new ArrayList<>();
        for (Product product : productRepository.findAll()) {
            if (product.getReported()){
                reportedProducts.add(product);
            }
        }
        Map<String, Object> response = new HashMap<>();
        response.put("reportedProducts", DtoMapper.toProductDtoList(reportedProducts));
        return response;
	}

    @GetMapping("/admin_users")
	public Map<String, Object> users() {
		return Map.of("users", DtoMapper.toUserDtoList(userService.getAllUsers()));
	}

	@GetMapping("/ban_user/{id}")
	public Map<String, Object> banUser(@PathVariable Long id) {
        userService.banUser(id);
        return Map.of("bannedUserId", id);
    }

    @GetMapping("/ignore_report/{id}")
	public ResponseEntity<Map<String, Object>> ignoreReport(@PathVariable Long id) {
    Optional<Product> p = productService.getProductById(id);
    if (p.isEmpty()){
        return ResponseEntity.notFound().build();
    }
    Product product = p.get();
    product.setReported(false);
    product.setReportedMessage("");
    productService.save(product);
    return ResponseEntity.ok(Map.of("ignoredReportFor", id));
    }


    @GetMapping("/admin_stats")
    public ResponseEntity<Map<String, Object>> adminStats() {
        long total_u = userRepository.count();
        long total_p = productRepository.count();
        long total_r = ratingRepository.count();

        Map<String, Object> response = new HashMap<>();
        response.put("totalUsers", total_u);
        response.put("totalProducts", total_p);
        response.put("totalRatings", total_r);

        if (total_p > 0) {
            List<Object[]> rows = productRepository.countProductsByCategory(); // category, count
            List<Map<String, Object>> categoryStats = rows.stream().map(r -> {
                String category = (String) r[0];
                long count = (Long) r[1];
                long percent = total_p == 0 ? 0 : Math.round((count * 100.0) / total_p);
                return Map.<String, Object>of(
                    "category", category,
                    "count", count,
                    "percent", percent
                );
            }).toList();
            response.put("categoryStats", categoryStats);

            List<Map<String, Object>> productsByDate = productRepository.countProductsByCreatedAt().stream().map(r -> {
                String createdAt = (String) r[0];
                Long count = (Long) r[1];
                return Map.<String, Object>of(
                    "date", createdAt,
                    "count", count
                );
            }).toList();
            response.put("productsByCreatedAt", productsByDate);
        }

        if (total_u > 0) {
            List<Map<String, Object>> usersByDate = userRepository.countUsersByCreatedAt().stream().map(r -> {
                String createdAt = (String) r[0];
                Long count = (Long) r[1];
                return Map.<String, Object>of(
                    "date", createdAt,
                    "count", count
                );
            }).toList();
            response.put("usersByCreatedAt", usersByDate);

            if (total_r > 0) {
                List<Map<String, Object>> ratingDistribution = ratingRepository.countRatingsByValue().stream().map(r -> {
                    Number valueNum = (Number) r[0];
                    Long count = (Long) r[1];
                    return Map.<String, Object>of(
                        "rating", valueNum.intValue(),
                        "count", count
                    );
                }).toList();
                response.put("ratingDistribution", ratingDistribution);
            }
        }

        return ResponseEntity.ok(response);
    }

}
