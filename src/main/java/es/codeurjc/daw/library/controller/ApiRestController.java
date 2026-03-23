package es.codeurjc.daw.library.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import es.codeurjc.daw.library.dto.DtoMapper;
import es.codeurjc.daw.library.dto.OrderDto;
import es.codeurjc.daw.library.dto.ProductDto;
import es.codeurjc.daw.library.dto.RatingDto;
import es.codeurjc.daw.library.dto.UserDto;
import es.codeurjc.daw.library.model.Order;
import es.codeurjc.daw.library.model.Product;
import es.codeurjc.daw.library.model.Rating;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.service.OrderService;
import es.codeurjc.daw.library.service.ProductService;
import es.codeurjc.daw.library.service.RatingService;
import es.codeurjc.daw.library.service.UserService;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
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
    private PasswordEncoder passwordEncoder;

    // --------- Products ---------

    @GetMapping("/products")
    public List<ProductDto> getProducts(@RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) {
        if (keyword != null && !keyword.isBlank()) {
            return DtoMapper.toProductDtoList(productService.searchProductsByName(keyword));
        }
        if (category != null && !category.isBlank()) {
            return DtoMapper.toProductDtoList(productService.searchProductsByCategory(category));
        }
        return DtoMapper.toProductDtoList(productService.getAllProducts());
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long id) {
        return productService.getProductById(id).map(p -> ResponseEntity.ok(DtoMapper.toDto(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/products")
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDto) {
        if (productDto == null || productDto.getSellerId() == null) {
            return ResponseEntity.badRequest().build();
        }
        return userService.getUserById(productDto.getSellerId()).map(seller -> {
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
            return ResponseEntity.status(HttpStatus.CREATED).body(DtoMapper.toDto(p));
        }).orElse(ResponseEntity.badRequest().build());
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @RequestBody ProductDto productDto) {
        return productService.getProductById(id).map(existing -> {
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
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (!productService.exist(id)) {
            return ResponseEntity.notFound().build();
        }
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // --------- Users ---------

    @GetMapping("/users")
    public List<UserDto> getUsers() {
        return DtoMapper.toUserDtoList(userService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        return userService.getUserById(id).map(u -> ResponseEntity.ok(DtoMapper.toDto(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/users")
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        if (userDto == null || userDto.getName() == null || userDto.getPassword() == null) {
            return ResponseEntity.badRequest().build();
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
        return ResponseEntity.status(HttpStatus.CREATED).body(DtoMapper.toDto(user));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        return userService.getUserById(id).map(existing -> {
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
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (!userService.getUserById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // --------- Orders ---------

    @GetMapping("/orders")
    public List<OrderDto> getOrders() {
        return DtoMapper.toOrderDtoList(orderService.getAllOrders());
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable Long id) {
        return orderService.getOrderById(id).map(o -> ResponseEntity.ok(DtoMapper.toDto(o)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/orders")
    public ResponseEntity<OrderDto> createOrder(@RequestBody OrderDto orderDto) {
        if (orderDto == null || orderDto.getBuyerId() == null || orderDto.getProductId() == null) {
            return ResponseEntity.badRequest().build();
        }
        return userService.getUserById(orderDto.getBuyerId()).flatMap(buyer ->
                productService.getProductById(orderDto.getProductId()).map(product -> {
                    Order order = new Order();
                    order.setBuyer(buyer);
                    order.setProduct(product);
                    order.setState(orderDto.getState() != null ? orderDto.getState() : "Offer sent");
                    orderService.save(order);
                    return ResponseEntity.status(HttpStatus.CREATED).body(DtoMapper.toDto(order));
                })
        ).orElse(ResponseEntity.badRequest().build());
    }

    @PutMapping("/orders/{id}")
    public ResponseEntity<OrderDto> updateOrder(@PathVariable Long id, @RequestBody OrderDto orderDto) {
        return orderService.getOrderById(id).map(existing -> {
            if (orderDto.getState() != null) {
                existing.setState(orderDto.getState());
            }
            orderService.save(existing);
            return ResponseEntity.ok(DtoMapper.toDto(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        if (!orderService.getOrderById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // --------- Ratings ---------

    @GetMapping("/ratings")
    public List<RatingDto> getRatings() {
        return DtoMapper.toRatingDtoList(ratingService.getAllRatings());
    }

    @GetMapping("/ratings/{id}")
    public ResponseEntity<RatingDto> getRating(@PathVariable Long id) {
        return ratingService.getRatingById(id).map(r -> ResponseEntity.ok(DtoMapper.toDto(r)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/ratings")
    public ResponseEntity<RatingDto> createRating(@RequestBody RatingDto ratingDto) {
        if (ratingDto == null || ratingDto.getRaterId() == null || ratingDto.getRatedId() == null) {
            return ResponseEntity.badRequest().build();
        }
        return userService.getUserById(ratingDto.getRaterId()).flatMap(rater ->
                userService.getUserById(ratingDto.getRatedId()).map(rated -> {
                    Rating rating = new Rating();
                    rating.setRater(rater);
                    rating.setRated(rated);
                    rating.setSummery(ratingDto.getSummery());
                    rating.setRating(ratingDto.getRating());
                    rating.setDescription(ratingDto.getDescription());
                    ratingService.save(rating);
                    return ResponseEntity.status(HttpStatus.CREATED).body(DtoMapper.toDto(rating));
                })
        ).orElse(ResponseEntity.badRequest().build());
    }

    @PutMapping("/ratings/{id}")
    public ResponseEntity<RatingDto> updateRating(@PathVariable Long id, @RequestBody RatingDto ratingDto) {
        return ratingService.getRatingById(id).map(existing -> {
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
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/ratings/{id}")
    public ResponseEntity<Void> deleteRating(@PathVariable Long id) {
        if (!ratingService.getRatingById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        ratingService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
}
