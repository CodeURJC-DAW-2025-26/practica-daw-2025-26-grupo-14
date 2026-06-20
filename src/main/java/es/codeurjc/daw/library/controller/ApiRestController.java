package es.codeurjc.daw.library.controller;

import java.net.URI;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import es.codeurjc.daw.library.dto.ChartDto;
import es.codeurjc.daw.library.dto.DtoMapper;
import es.codeurjc.daw.library.dto.ImageDto;
import es.codeurjc.daw.library.dto.OrderDto;
import es.codeurjc.daw.library.dto.ProductDto;
import es.codeurjc.daw.library.dto.RatingDto;
import es.codeurjc.daw.library.dto.UserDto;
import es.codeurjc.daw.library.model.Image;
import es.codeurjc.daw.library.model.Order;
import es.codeurjc.daw.library.model.Product;
import es.codeurjc.daw.library.model.Rating;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.service.ImageService;
import es.codeurjc.daw.library.service.OrderService;
import es.codeurjc.daw.library.service.ProductService;
import es.codeurjc.daw.library.service.RatingService;
import es.codeurjc.daw.library.service.UserService;
import es.codeurjc.daw.library.util.SecurityUtil;



@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1")
public class ApiRestController {
     @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RatingService ratingService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SecurityUtil securityUtil;

    // --------- Products ---------

     @GetMapping("")
    public String test() {
        return "API funcionando";
    }
    
    @GetMapping("/products")
    public List<ProductDto> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Long sellerId) {

        Page<Product> productPage;
        
        if (sellerId != null) {
            // Get products by seller
            User seller = userService.getUserById(sellerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
            List<Product> products = productService.getProductsBySeller(seller);
            return DtoMapper.toProductDtoList(products);
        }
        
        productPage = productService.getProductsPage(page, 10, keyword, category);

        return DtoMapper.toProductDtoList(productPage.getContent());
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long id) {
        Product product = productService.getProductById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Product not found with id " + id)
            );

        return ResponseEntity.ok(DtoMapper.toDto(product));
    }

    @PostMapping("/products")
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDto, @AuthenticationPrincipal UserDetails currentUser) {

        if (productDto == null || productDto.getName() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product name is required");
        }

        User seller = userService.findByName(currentUser.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        if (!securityUtil.isProductOwnerOrAdmin(seller.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You don't have permission to create a product for this seller");
        }

        Product p = new Product();
        p.setSeller(seller);
        p.setName(productDto.getName());
        p.setPrice(productDto.getPrice());
        p.setCategory(productDto.getCategory());
        p.setCondition(productDto.getCondition());
        p.setFullDescription(productDto.getFullDescription());
        p.setShortDescription(productDto.getShortDescription());
        p.setContactPreference(productDto.getContactPreference());
        p.setReported(Boolean.TRUE.equals(productDto.getReported()));
        p.setReportedMessage(productDto.getReportedMessage());
        p.setDate();

        productService.save(p);

        URI location = URI.create(String.format("/api/v1/products/%d", p.getId()));
        return ResponseEntity.created(location).body(DtoMapper.toDto(p));

    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @RequestBody ProductDto productDto) {
        Product existing = productService.getProductById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Product not found with id " + id));

        if (!securityUtil.isProductOwnerOrAdmin(existing.getSeller().getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "You don't have permission to modify this product");
        }

        if (productDto.getName() != null) {
            existing.setName(productDto.getName());
        }
        if (productDto.getPrice() != 0.0f) {
            existing.setPrice(productDto.getPrice());
        }
        if (productDto.getCategory() != null) {
            existing.setCategory(productDto.getCategory());
        }
        if (productDto.getCondition() != null) {
            existing.setCondition(productDto.getCondition());
        }
        if (productDto.getFullDescription() != null) {
            existing.setFullDescription(productDto.getFullDescription());
        }
        if (productDto.getShortDescription() != null) {
            existing.setShortDescription(productDto.getShortDescription());
        }
        if (productDto.getContactPreference() != null) {
            existing.setContactPreference(productDto.getContactPreference());
        }
        if (productDto.getReported() != null) {
            existing.setReported(productDto.getReported());
        }
        if (productDto.getReportedMessage() != null) {
            existing.setReportedMessage(productDto.getReportedMessage());
        }

        productService.save(existing);
        return ResponseEntity.ok(DtoMapper.toDto(existing));

    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Product not found with id " + id));

        // Check access: user must be the seller or admin
        if (!securityUtil.isProductOwnerOrAdmin(product.getSeller().getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "You don't have permission to delete this product");
        }

        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/products/{id}/images")
    public ResponseEntity<?> uploadProductImages(
            @PathVariable Long id,
            @RequestParam("imageFiles") List<MultipartFile> imageFiles) throws IOException, SQLException {

        Product product = productService.getProductById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        if (!securityUtil.isProductOwnerOrAdmin(product.getSeller().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission");
        }

        for (MultipartFile file : imageFiles) {
            Image image = imageService.createImage(file.getInputStream());
            product.setImage(image);
        }

        productService.save(product);
        return ResponseEntity.ok().build();
    }

    // --------- Users ---------

    @GetMapping("/users")
    public List<UserDto> getUsers(@RequestParam(defaultValue = "0") int page) {
        Page<User> userPage = userService.getUsersPage(page, 10);
        return DtoMapper.toUserDtoList(userPage.getContent());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        User user = userService.getUserById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id " + id));

        return ResponseEntity.ok(DtoMapper.toDto(user));
    }

    @PostMapping("/users") //Shouldn´t we use register?
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        if (userDto == null || userDto.getName() == null || userDto.getPassword() == null || userDto.getDni() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name and password and DNI are required");
        }
        User user = new User();
        user.setFullName(userDto.getFullName());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setCity(userDto.getCity());
        user.setDni(userDto.getDni());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        if (userDto.getRoles() != null && !userDto.getRoles().isEmpty()) {
            user.setRoles(userDto.getRoles());
        } else {
            user.setRoles("USER");
        }
        user.setDate();
        userService.save(user);

        URI location = URI.create(String.format("/api/v1/users/%d", user.getId()));
        return ResponseEntity.created(location).body(DtoMapper.toDto(user));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        User existing = userService.getUserById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found with id " + id));

        // Check access: user can only update their own profile or admin can update any
        if (!securityUtil.isUserOwnerOrAdmin(id)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "You don't have permission to update this user profile");
        }

        if (userDto.getFullName() != null) {
            existing.setFullName(userDto.getFullName());
        }
        if (userDto.getName() != null) {
            existing.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            existing.setEmail(userDto.getEmail());
        }
        if (userDto.getCity() != null) {
            existing.setCity(userDto.getCity());
        }
        if (userDto.getDni() != null) {
            existing.setDni(userDto.getDni());
        }
        if (userDto.getIsBanned() != null) {
            existing.setBanned(userDto.getIsBanned());
        }
        if (userDto.getPassword() != null && !userDto.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        if (userDto.getRoles() != null && !userDto.getRoles().isEmpty()) {
            existing.setRoles(userDto.getRoles());
        }

        userService.save(existing);
        return ResponseEntity.ok(DtoMapper.toDto(existing));

    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (!userService.getUserById(id).isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id " + id);
        }
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/{id}/profilePicture")
    public ResponseEntity<Void> uploadProfilePicture(
            @PathVariable Long id,
            @RequestParam("imageField") MultipartFile file
    ) {
        userService.saveProfilePicture(id, file);
        return ResponseEntity.ok().build();
    }

    // --------- Orders ---------

    @GetMapping("/orders")
    public org.springframework.data.domain.Page<OrderDto> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return orderService.getOrdersPage(page, size)
                .map(DtoMapper::toDto);
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable Long id) {
        Order order = orderService.getOrderById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found with id " + id));

        return ResponseEntity.ok(DtoMapper.toDto(order));
    }

    @PostMapping("/orders")
    public ResponseEntity<OrderDto> createOrder(@RequestBody OrderDto orderDto, @AuthenticationPrincipal UserDetails currentUser) {
        if (orderDto == null || orderDto.getProductId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "productId is required");
        }

        User buyer = userService.findByName(currentUser.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        Product product = productService.getProductById(orderDto.getProductId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Product not found with id " + orderDto.getProductId()));

        Order order = new Order();
        order.setBuyer(buyer);
        order.setProduct(product);
        order.setState(orderDto.getState() != null ? orderDto.getState() : "Offer sent");

        orderService.save(order);

        URI location = URI.create(String.format("/api/v1/orders/%d", order.getId()));
        return ResponseEntity.created(location).body(DtoMapper.toDto(order));

    }

    @PutMapping("/orders/{id}")
    public ResponseEntity<OrderDto> updateOrder(@PathVariable Long id, @RequestBody OrderDto orderDto) {
        Order existing = orderService.getOrderById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Order not found with id " + id));

        // Check access: user must be the buyer or admin
        if (!securityUtil.isOrderOwnerOrAdmin(existing.getBuyer().getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "You don't have permission to modify this order");
        }

        if (orderDto.getState() != null) {
            existing.setState(orderDto.getState());
        }

        orderService.save(existing);
        return ResponseEntity.ok(DtoMapper.toDto(existing));

    }

    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        Order order = orderService.getOrderById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Order not found with id " + id));

        // Check access: user must be the buyer or admin
        if (!securityUtil.isOrderOwnerOrAdmin(order.getBuyer().getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "You don't have permission to delete this order");
        }

        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // --------- Ratings ---------

    @GetMapping("/ratings")
    public org.springframework.data.domain.Page<RatingDto> getRatings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ratingService.getRatingsPage(page, size)
                .map(DtoMapper::toDto);
    }

    @GetMapping("/ratings/{id}")
    public ResponseEntity<RatingDto> getRating(@PathVariable Long id) {
        Rating rating = ratingService.getRatingById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rating not found with id " + id));

        return ResponseEntity.ok(DtoMapper.toDto(rating));
    }

    @PostMapping("/ratings")
    public ResponseEntity<RatingDto> createRating(@RequestBody RatingDto ratingDto, @AuthenticationPrincipal UserDetails currentUser) {
        if (ratingDto == null || ratingDto.getRatedId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ratedId is required");
        }

        User rater = userService.findByName(currentUser.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        User rated = userService.getUserById(ratingDto.getRatedId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Rated user not found with id " + ratingDto.getRatedId()));

        Rating rating = new Rating();
        rating.setRater(rater);
        rating.setRated(rated);
        rating.setSummery(ratingDto.getSummery());
        rating.setRating(ratingDto.getRating());
        rating.setDescription(ratingDto.getDescription());

        Order order = orderService.getOrderById(ratingDto.getOrderId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Order not found with id " + ratingDto.getOrderId()));

        order.setRating(rating);
        ratingService.save(rating);
        orderService.save(order);

        URI location = URI.create(String.format("/api/v1/ratings/%d", rating.getId()));
        return ResponseEntity.created(location).body(DtoMapper.toDto(rating));

    }

    @PutMapping("/ratings/{id}")
    public ResponseEntity<RatingDto> updateRating(@PathVariable Long id, @RequestBody RatingDto ratingDto) {
        Rating existing = ratingService.getRatingById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Rating not found with id " + id));

        // Check access: user must be the rater or admin
        if (!securityUtil.isRatingOwnerOrAdmin(existing.getRater().getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "You don't have permission to modify this rating");
        }

        if (ratingDto.getSummery() != null) {
            existing.setSummery(ratingDto.getSummery());
        }
        if (ratingDto.getRating() != 0) {
            existing.setRating(ratingDto.getRating());
        }
        if (ratingDto.getDescription() != null) {
            existing.setDescription(ratingDto.getDescription());
        }

        ratingService.save(existing);
        return ResponseEntity.ok(DtoMapper.toDto(existing));
    }

    @DeleteMapping("/ratings/{id}")
    public ResponseEntity<Void> deleteRating(@PathVariable Long id) {
        Rating rating = ratingService.getRatingById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Rating not found with id " + id));

        // Check access: user must be the rater or admin
        if (!securityUtil.isRatingOwnerOrAdmin(rating.getRater().getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "You don't have permission to delete this rating");
        }

        ratingService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // --------- Images ---------
    @GetMapping("/images")
    public List<ImageDto> getImages() {
        return DtoMapper.toImageDtoList(imageService.getAllImages());
    }

    @GetMapping("/images/{id}")
    public ResponseEntity<ImageDto> getImage(@PathVariable Long id) {
        Image image = imageService.getImage(id);
        
        if (image == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found with id " + id);
        }

        return ResponseEntity.ok(DtoMapper.toDto(image));
    }

    // --------- Charts ---------
     @GetMapping("/charts")
    public ResponseEntity<ChartDto> getCharts() {
        ChartDto dto = new ChartDto();
        dto.setName("charts");
        
        Map<String, Long> data = new LinkedHashMap<>();
        long products = productService.getAllProducts().size();
        if (products != 0){
            data.put("categories", products);
            data.put("productHistorical", products);
        }

        long users = userService.getAllUsers().size();
        if (users != 0){
            data.put("userHistorical", users);
        }

        long ratings = ratingService.getAllRatings().size();
        if (ratings != 0){
            data.put("ratings", ratings);
        }

        for (User user : userService.getAllUsers()) {
            long ratingCount = user.getMyRatings().size();

            if (ratingCount > 0) {
                data.put("users/" + user.getId().toString() + "/ratings",  ratingCount);
            }
        }

        dto.setData(data);

        return ResponseEntity.ok(dto);
    }
    
    @GetMapping("/charts/categories")
    public ResponseEntity<ChartDto> getChartCategories() {
        ChartDto dto = new ChartDto();
        dto.setName("categories");
        Map<String, Long> data = new LinkedHashMap<>();
        
        data.put("Clothing",(long) productService.searchProductsByCategory("Clothing").size());
        data.put("Electronics", (long) productService.searchProductsByCategory("Electronics").size());
        data.put("Home", (long) productService.searchProductsByCategory("Home").size());
        data.put("Sports", (long) productService.searchProductsByCategory("Sports").size());
        data.put("Books", (long) productService.searchProductsByCategory("Books").size());
        data.put("Others", (long) productService.searchProductsByCategory("Others").size());
        data.put("Total", (long) productService.getAllProducts().size());

        dto.setData(data);

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/charts/productHistorical")
    public ResponseEntity<ChartDto> getChartPHistorical() {
        ChartDto dto = new ChartDto();
        dto.setName("productHistorical");
        
        Map<String, Long> data = productService.getAllProducts().stream()
            .collect(Collectors.groupingBy(
                    p -> p.getCreatedAt().toString(),
                    TreeMap::new,
                    Collectors.counting()
            ));
        data.put("Total", (long) productService.getAllProducts().size());

        dto.setData(data);

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/charts/userHistorical")
    public ResponseEntity<ChartDto> getUsersHistorical() {
        ChartDto dto = new ChartDto();
        dto.setName("userHistorical");
        
        Map<String, Long> data = userService.getAllUsers().stream()
            .collect(Collectors.groupingBy(
                    p -> p.getCreatedAt().toString(),
                    TreeMap::new,
                    Collectors.counting()
            ));
        data.put("Total", (long) userService.getAllUsers().size());

        dto.setData(data);

        return ResponseEntity.ok(dto);
    }
    
    @GetMapping("/charts/ratings")
    public ResponseEntity<ChartDto> getRatingsChart() {
        ChartDto dto = new ChartDto();
        dto.setName("ratings");
        
        Map<String, Long> data = ratingService.getAllRatings().stream()
                .collect(Collectors.groupingBy(
                        p -> String.valueOf(p.getRating()),
                        TreeMap::new,
                        Collectors.counting()
                ));

        data.put("Total", (long) ratingService.getAllRatings().size());

        dto.setData(data);

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/charts/ratings/{id}")
    public ResponseEntity<ChartDto> getRatingsIdChart() {
        ChartDto dto = new ChartDto();
        dto.setName("ratings");
        
        Map<String, Long> data = ratingService.getAllRatings().stream()
                .collect(Collectors.groupingBy(
                        p -> String.valueOf(p.getRating()),
                        TreeMap::new,
                        Collectors.counting()
                ));

        data.put("Total", (long) ratingService.getAllRatings().size());

        dto.setData(data);

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/charts/users/{id}/ratings")
    public ResponseEntity<ChartDto> getUserRatingsChart(@PathVariable Long id) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found with id " + id));

        Map<String, Long> data = user.getMyRatings().stream()
                .collect(Collectors.groupingBy(
                        rating -> String.valueOf(rating.getRating()),
                        TreeMap::new,
                        Collectors.counting()
                ));

        data.put("Total", (long) user.getMyRatings().size());

        ChartDto dto = new ChartDto();
        dto.setName("users/" + user.getId().toString() + "/ratings");
        dto.setData(data);

        return ResponseEntity.ok(dto);
    }
    
    @GetMapping("/users/{id}/ratings")
    public ResponseEntity<Map<String, Object>> getUserRatings(@PathVariable Long id) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found with id " + id));

        List<Rating> ratings = user.getMyRatings();
        double avgRating = ratings.stream()
                .mapToInt(Rating::getRating)
                .average()
                .orElse(0.0);
        
        Map<String, Object> result = new HashMap<>();
        result.put("avgRating", avgRating);
        result.put("totalRatings", ratings.size());
        
        return ResponseEntity.ok(result);
    }

    // --------- SendBird ---------
    @GetMapping("/users/{id}/conversations")
    public ResponseEntity<Map<String, Object>> getUserConversations(@PathVariable Long id) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found with id " + id));

        if(!securityUtil.getAuthenticatedUser().equals(user)){
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "You don't have permission to see this conversations");
        }

        String appId = "D718DE9B-58D6-449A-80A7-7AF34C6ABD1E";
        String apiToken = "6c1470b9cb7b4b6f6d719eb56064103c6f21c2a9";
        String sendbirdUserId = user.getDni();

        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Api-Token", apiToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map<String, Object>> response = rt.exchange(
                "https://api-" + appId + ".sendbird.com/v3/users/" + sendbirdUserId
                        + "/my_group_channels?show_member=true&show_read_receipt=true&show_delivery_receipt=true",
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        return ResponseEntity.ok(response.getBody());
    }
    
}
