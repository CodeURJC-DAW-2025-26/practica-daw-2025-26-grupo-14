package es.codeurjc.daw.library.controller;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import es.codeurjc.daw.library.model.Product;
import es.codeurjc.daw.library.repository.ProductRepository;
import es.codeurjc.daw.library.repository.RatingRepository;
import es.codeurjc.daw.library.repository.UserRepository;
import es.codeurjc.daw.library.service.ProductService;
import es.codeurjc.daw.library.service.UserService;
import es.codeurjc.daw.library.repository.OrderRepository;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;


@Controller
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

    @Autowired
    private OrderRepository orderRepository;
	
	@GetMapping("/administrator")
	public String administrator(Model model) {
            long total_u = userRepository.count();
            long total_p = productRepository.count();
            long total_r = ratingRepository.count();
            long total_d = orderRepository.count();
            model.addAttribute("t_users", total_u);
            model.addAttribute("t_listings", total_p);
            model.addAttribute("t_ratings", total_r);
            model.addAttribute("t_deals", total_d);
		return "administrator";
	}

    @GetMapping("/admin_listings")
	public String listings(Model model) {
        List<Product> reportedProducts = new ArrayList<>();
        for (Product product : productRepository.findAll()) {
            if (product.getReported()){
                reportedProducts.add(product);
            }
        }
        if (!reportedProducts.isEmpty()){
            model.addAttribute("Products", reportedProducts);
        }
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

    @GetMapping("/ignore_report/{id}")
	public String ignoreReport(@PathVariable Long id, Model model) {
    Optional<Product> p = productService.getProductById(id);
    if (!p.isPresent()){
        model.addAttribute("error", "Product not found.");
        return "error";
    }
    Product product = p.get();
    product.setReported(false);
    product.setReportedMessage("");
    productService.save(product);
    return "redirect:/admin_listings";
    }


    @GetMapping("/admin_stats")
public String adminStats(Model model) {
    long total_u = userRepository.count();
    long total_p = productRepository.count();
    long total_r = ratingRepository.count();
    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yy");

    if (total_p > 0){
        List<Object[]> rows = productRepository.countProductsByCategory(); // category, count
        List<Map<String,Object>> categoryStats = rows.stream().map(r -> {
            String category = (String) r[0];
            long count = (Long) r[1];
            long percent = total_p == 0 ? 0 : Math.round((count * 100.0) / total_p);
            return Map.<String, Object>of(
                "category", category,
                "count", count,
                "percent", percent,
                "widthStyle", "width: " + percent + "%;"
            );
        }).toList();
        
        TreeMap<LocalDate, Long> byDate = new TreeMap<>();

        for (Object[] r : productRepository.countProductsByCreatedAt()) {
            String createdAt = (String) r[0];
            Long count = (Long) r[1];
            byDate.put(LocalDate.parse(createdAt, fmt), count);
        }

        String labelsJs = byDate.keySet().stream()
            .map(d -> "\"" + d.format(fmt) + "\"")
            .collect(Collectors.joining(","));

        String dataJs = byDate.values().stream()
            .map(String::valueOf)
            .collect(Collectors.joining(","));

        model.addAttribute("productChartLabels", labelsJs);
        model.addAttribute("productChartData", dataJs);
        model.addAttribute("categoryStats", categoryStats);
        model.addAttribute("Products", true);
    } else {
        model.addAttribute("Products", false);
    }

    if (total_u > 0){
        TreeMap<LocalDate, Long> usersByDate = new TreeMap<>();

        for (Object[] r : userRepository.countUsersByCreatedAt()) {
            String createdAt = (String) r[0];
            Long count = (Long) r[1];
            usersByDate.put(LocalDate.parse(createdAt, fmt), count);
        }

        String userLabelsJs = usersByDate.keySet().stream()
            .map(d -> "\"" + d.format(fmt) + "\"")
            .collect(Collectors.joining(","));

        String userDataJs = usersByDate.values().stream()
            .map(String::valueOf)
            .collect(Collectors.joining(","));
        model.addAttribute("userChartLabels", userLabelsJs);
        model.addAttribute("userChartData", userDataJs);
        model.addAttribute("Users", true);
        List<Object[]> rows_r = ratingRepository.countRatingsByValue();
        if (total_r > 0){
            TreeMap<Integer, Long> ratings = new TreeMap<>();


            for (Object[] r : ratingRepository.countRatingsByValue()) {
                Number valueNum = (Number) r[0];
                Long count = (Long) r[1];
                ratings.put(valueNum.intValue(), count);
            }

            String ratingLabelsJs = ratings.keySet().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

            String ratingDataJs = ratings.values().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

            model.addAttribute("label", ratingLabelsJs);
            model.addAttribute("count", ratingDataJs);
            model.addAttribute("Ratings", true);
        }else{
            model.addAttribute("Ratings", false);
        }
    }else{
        model.addAttribute("Users", false);
    }

    model.addAttribute("total_p", total_p);
    model.addAttribute("total_u", total_u);
    model.addAttribute("total_r", total_r);
    return "admin_stats";
}



}
