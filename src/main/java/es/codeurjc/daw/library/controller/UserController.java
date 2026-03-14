package es.codeurjc.daw.library.controller;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.daw.library.model.Image;
import es.codeurjc.daw.library.model.Order;
import es.codeurjc.daw.library.model.Product;
import es.codeurjc.daw.library.model.Rating;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.repository.UserRepository;
import es.codeurjc.daw.library.service.UserService;
import es.codeurjc.daw.library.service.ImageService;
import es.codeurjc.daw.library.service.OrderService;
import es.codeurjc.daw.library.service.ProductService;
import es.codeurjc.daw.library.service.RatingService;


@Controller
public class UserController {

    private final UserRepository userRepository;

    @Autowired
    private UserService userService;
	
	@Autowired
	private ProductService productService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private RatingService ratingService;

	@Autowired
	private ImageService imageService;
	
	@Autowired
    PasswordEncoder passwordEncoder;

    UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

	@GetMapping("/login")
	public String login() {
		return "login";
    }

    @GetMapping("/loginerror")
	public String loginError() {
		return "loginerror";
	}


    @GetMapping("/register")
	public String register() {
		return "register";
	}

	@PostMapping("/register")
	public String register(Model model, User user, String confirmPassword, Boolean user_new) {
		if (user_new && !user.getPassword().equals(confirmPassword)) {
			model.addAttribute("error", "Passwords do not match");
			return "registererror";
		} else if (user_new || confirmPassword != null){
			user.setPassword(passwordEncoder.encode(confirmPassword));
		}
		user.setRoles("USER");
		
		if(!user.getDni().matches("\\d{8}[A-Za-z]")){
			model.addAttribute("error", "DNI must have 8 numbers and 1 letter.");
			return "registererror";
		}
		
		if(userRepository.findBydni(user.getDni()).isPresent()){
			model.addAttribute("error", "DNI must unique.");
			return "registererror";
		}

		if (user_new) {
			user.setDate();
			userService.save(user);
			model.addAttribute("message", "Account created successfully. Please log in.");
			return "login";
		} else {
			Optional<User> u = userService.getUserById(user.getId());
			if (!u.isPresent()) {
				model.addAttribute("error", "Couldn't find user.");
				return "error";
			}
			user.setImage(u.get().getImage());
			userService.save(user);
			model.addAttribute("message", "Account updated successfully.");
			model.addAttribute("user", user);
			model.addAttribute("isOwner", true);
			if(user.getMyRatings() != null){
				int[] count_ratings = {0,0,0,0,0};
				int total = 0;
				int sum = 0;
				for (Rating rating : user.getMyRatings()) {
					count_ratings[rating.getRating()] += 1;
					total +=1;
					sum += rating.getRating();
				}

				Map<Integer, Integer> ratingMap = new HashMap<>();
				if (total > 0){
					for (int i = 0; i < count_ratings.length; i++) {
						ratingMap.put(i, (count_ratings[i]*100)/total);
					}
				}
				model.addAttribute("ammount", ratingMap.entrySet());

				double avg = 0.0;
				int avgPercent = 0;
				if (total > 0){
					avg = (double) sum/total;
					avgPercent = (int)Math.round((avg / 5.0) * 100);
				} 
				model.addAttribute("totalRating", total);
				model.addAttribute("avgRating", avg);
				model.addAttribute("avgRatingPercent", avgPercent);
			}
			return "user_account";
		}
		
	}

	

    @PostMapping("/newproduct")
	public String postNewProduct(Model model, Product product, String sellerName,  List<MultipartFile> imageFields, @RequestParam(required = false) List<Long> deleteImageIds) throws IOException {
		Optional<User> seller = userService.findByName(sellerName);
		if (!seller.isPresent()) {
			model.addAttribute("error", "You should be logged in to publish a product.");
			return "publish";
		}
		product.setSeller(seller.get());
		product.setDate();

		if (product.getId() != null) {
			Optional<Product> p = productService.getProductById(product.getId());
			if (!p.isPresent()) {
				model.addAttribute("error", "Product couldn´t be found.");
				return "publish";
			}
			if (deleteImageIds == null){
				product.setImages(p.get().getImages());
			}else{
				for (Image image : p.get().getImages()) {
					if (!deleteImageIds.contains(image.getId())){
						product.setImage(image);
					}
				}
			}
			
		}
		if (imageFields != null && !imageFields.isEmpty()) {
			
			for (MultipartFile imageField : imageFields) {
				Image image = imageService.createImage(imageField.getInputStream());
				product.setImage(image);
			}
			
		}

		productService.save(product);
		model.addAttribute("products", productService.getAllProducts());
		return "my-listings";
	}

    @GetMapping("/editproduct/{id}")
	public String editProduct(Model model, @PathVariable long id) {

		Optional<Product> product = productService.getProductById(id);
		if (product.isPresent()) {
			Product p = product.get();
			model.addAttribute("product", p);
			model.addAttribute("contactOptions", List.of(
				Map.of("value", "Chat", "selected", "Chat".equals(p.getContactPreference())),
				Map.of("value", "Phone", "selected", "Phone".equals(p.getContactPreference())),
				Map.of("value", "Both", "selected", "Both".equals(p.getContactPreference()))
			));

			model.addAttribute("categoryOptions", List.of(
				Map.of("value", "Electronics", "selected", "Electronics".equals(p.getCategory())),
				Map.of("value", "Clothing", "selected", "Clothing".equals(p.getCategory())),
				Map.of("value", "Home", "selected", "Home".equals(p.getCategory())),
				Map.of("value", "Sports", "selected", "Sports".equals(p.getCategory())),
				Map.of("value", "Books", "selected", "Books".equals(p.getCategory())),
				Map.of("value", "Other", "selected", "Other".equals(p.getCategory()))
			));

			model.addAttribute("conditionOptions", List.of(
				Map.of("value", "New", "selected", "New".equals(p.getCondition())),
				Map.of("value", "Like new", "selected", "Like new".equals(p.getCondition())),
				Map.of("value", "Used", "selected", "Used".equals(p.getCondition())),
				Map.of("value", "For parts", "selected", "For parts".equals(p.getCondition()))
			));

			return "publish";
		} else {
			return "pageerror";
		}
	}

    @GetMapping("/user_account/{id}")
	public String profile(@PathVariable Long id, Model model) {
		Optional<User> user = userService.getUserById(id);
		if (user.isPresent()) {

           List<Product> products = productService.getProductsBySeller(user.get());
            model.addAttribute("user", user.get());
			if (model.getAttribute("userName") != null && model.getAttribute("userName").equals(user.get().getName())) {
				model.addAttribute("isOwner", true);
			}
            model.addAttribute("products", products);
			if(user.get().getMyRatings() != null){
				int[] count_ratings = {0,0,0,0,0};
				int total = 0;
				int sum = 0;
				for (Rating rating : user.get().getMyRatings()) {
					count_ratings[rating.getRating()] += 1;
					sum += rating.getRating();
					total += 1;
				}

				Map<Integer, Integer> ratingMap = new HashMap<>();
				if (total > 0){
					for (int i = 0; i < count_ratings.length; i++) {
						ratingMap.put(i, (count_ratings[i]*100)/total);
					}
				}

				double avg = 0.0;
				int avgPercent = 0;
				if (total > 0){
					avg = (double) sum/total;
					avgPercent = (int)Math.round((avg / 5.0) * 100);
				} 
				model.addAttribute("totalRating", total);
				model.addAttribute("avgRating", avg);
				model.addAttribute("avgRatingPercent", avgPercent);
				model.addAttribute("ammount", ratingMap.entrySet());

			}
		    return "user_account";}
        else{
                return "pageerror";
            }
    }

	@GetMapping("/edituser/{id}")
	public String editUser(@PathVariable Long id, Model model) {
		Optional<User> user = userService.getUserById(id);
		if (user.isPresent()) {
			model.addAttribute("user", user.get());
			model.addAttribute("cityOptions", List.of(
				Map.of("value", "Madrid", "selected", "Madrid".equals(user.get().getCity())),
				Map.of("value", "Barcelona", "selected", "Barcelona".equals(user.get().getCity())),
				Map.of("value", "Valencia", "selected", "Valencia".equals(user.get().getCity())),
				Map.of("value", "Sevilla", "selected", "Sevilla".equals(user.get().getCity())),
				Map.of("value", "Zaragoza", "selected", "Zaragoza".equals(user.get().getCity()))
			));
			return "register";
		} else {
			return "pageerror";
		}
	}

	@PostMapping("/report_product/{id}")
	public String postReportProduct(@PathVariable Long id, Model model, String message) {
		Optional<Product> product = productService.getProductById(id);
		if (!product.isPresent()) {
			model.addAttribute("errorMessage", "Product not found.");
			return "error";
		}

		if (model.getAttribute("userName") == null) {
			model.addAttribute("message", "You should be logged in to report a product.");
			return "redirect:/product/" + id;
		}
		Product p = product.get();
		p.setReported(true);
		p.setReportedMessage(message);
		productService.save(p);
		model.addAttribute("message", "Product reported succesfully");
		return "redirect:/product/" + id;
	}

	@PostMapping("/create_deal/{id}")
	public String postCreateDeal(@PathVariable Long id, Model model) {
		Optional<Product> product = productService.getProductById(id);
		if (!product.isPresent()) {
			model.addAttribute("errorMessage", "Product not found.");
			return "error";
		}

		if (model.getAttribute("userName") == null) {
			model.addAttribute("message", "You should be logged in to buy a product.");
			return "redirect:/product/" + id;
		}
		Optional<User> buyer = userService.findByName((String) model.getAttribute("userName"));
		if (!buyer.isPresent()) {
			model.addAttribute("errorMessage", "Your user is not found.");
			return "error";
		}
		
		if(buyer.get().getMyOrders().stream().anyMatch(o -> 
			o.getProduct() != null && o.getProduct().getId().equals(product.get().getId()))){
				model.addAttribute("message", "You has already bought this product.");
				return "redirect:/product/" + id;
			}

		Order order = new Order("Offer sent", product.get(), buyer.get());
		
		orderService.save(order);
		return "redirect:/";
	}

	@PostMapping("/updateOrder/{id}")
	public String updateOrder(@PathVariable long id, @RequestParam String action, Model model) {
		Optional<Order> order = orderService.getOrderById(id);
		if (!order.isPresent()) {
			model.addAttribute("errorMessage", "Order not found.");
			return "error";
		}
		Order o = order.get();
		switch (action) {
			case "accepted":
				o.setState("Accepted");
				break;
			case "rejected":
				o.setState("Rejected");
				break;
			case "cancelled":
				o.setState("Cancelled");
				break;
			case "offer_sent":
				o.setState("Offer sent");
				break;
			default:
				model.addAttribute("errorMessage", "Invalid action.");
				return "error";
		}
		orderService.save(o);
		return "redirect:/my_deals";
	}

	@PostMapping("/deleteproduct/{id}")
	public String deleteProduct(@PathVariable Long id, Model model) {
			productService.delete(id);
			return "redirect:/my_listings";

	}

	@GetMapping("/chat/{id}")
	public String openChat(@PathVariable Long id, Model model) {

		Optional<Order> deal = orderService.getOrderById(id);

		if(!deal.isPresent()){
			model.addAttribute("errorMessage", "Your deal is not found");
			return "error";
		}

		if (model.getAttribute("userName") != null && model.getAttribute("userName").equals(deal.get().getProduct().getSeller().getName())) {
			model.addAttribute("isSeller", true);
		} else {
			model.addAttribute("isSeller", false);
		}

		model.addAttribute("deal", deal.get());

		return "deals_chat";
	}

	@PostMapping("/deleteuser/{id}")
	public String deleteUser(@PathVariable Long id, Model model) {	
		for (Product p : productService.getProductsBySeller(userService.getUserById(id).get())) {
			productService.delete(p.getId());
		}
		userService.delete(id);

		return "redirect:/admin_users";
	}

	@PostMapping("/delete_deal/{id}")
	public String deleteDeal(@PathVariable Long id) {
		String appId = "D718DE9B-58D6-449A-80A7-7AF34C6ABD1E";
		String channelUrl = "deal-" + id;
		String apiToken = "6c1470b9cb7b4b6f6d719eb56064103c6f21c2a9";

		RestTemplate rt = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Api-Token", apiToken);
		HttpEntity<Void> request = new HttpEntity<>(headers);

  
        rt.exchange(
            "https://api-" + appId + ".sendbird.com/v3/group_channels/" + channelUrl,
            HttpMethod.DELETE,
            request,
            String.class
        );


		Optional<Order> order = orderService.getOrderById(id);
		if (order.isPresent() && order.get().getRating() != null) {
			Rating rating = order.get().getRating();
			order.get().setRating(null);
			orderService.save(order.get());
			ratingService.delete(rating.getId());
		}
		orderService.delete(id);
		return "redirect:/my_deals";
	}

	@PostMapping("/new_rating")
	public String postNewRating(Model model, Rating rating, Long order_id) {
		Order order = orderService.getOrderById(order_id).orElse(null);
		if(order == null) {
			model.addAttribute("errorMessage", "Order not found");
			return "error";
		}
		String raterName = (String) model.getAttribute("userName");
		Optional<User> raterUser = userService.findByName(raterName);
		if (!raterUser.isPresent()) {
			model.addAttribute("errorMessage", "Your user is not found");
			return "error";
		}
		rating.setRater(raterUser.get());
		rating.setRated(order.getSellUser());
		 ratingService.save(rating);
		 order.setRating(rating);
		 orderService.save(order);
		return "redirect:/my_deals";
	}

	@PostMapping("/delete_rating/{id}")
	public String deleteRating(@PathVariable Long id, Model model) {
		Optional<Rating> rating = ratingService.getRatingById(id);
		if (!rating.isPresent()) {
			model.addAttribute("errorMessage", "Rating not found.");
			return "error";
		}
		Order order = rating.get().getOrder();
		order.setRating(null);
		orderService.save(order);
		ratingService.delete(id);
		return "redirect:/my_deals";
	}

	@PostMapping("/uploadProfilePicture/{id}")
	public String postChangeProfile(@PathVariable Long id, @RequestParam("imageField") MultipartFile imageField, Model model) throws IOException {
		
		Optional<User> user = userService.getUserById(id);
		if (!user.isPresent()) {
			model.addAttribute("errorMessage", "User not found.");
			return "error";
		}
		User u = user.get();
		
		if (!imageField.isEmpty()) {
			Image image = imageService.createImage(imageField.getInputStream());
			u.setImage(image);
		}

		userService.save(u);

		
		return "redirect:/user_account/" + id;
	}
	
	@PostMapping("/uploadImage/{id}")
	public String postChangeImage(@PathVariable Long id, @RequestParam("imageField") MultipartFile imageField, Model model) throws IOException {
		
		Optional<Product> product = productService.getProductById(id);
		if (!product.isPresent()) {
			model.addAttribute("errorMessage", "Product not found.");
			return "error";
		}
		Product p = product.get();
		
		if (!imageField.isEmpty()) {
			Image image = imageService.createImage(imageField.getInputStream());
			p.setImage(image);
		}

		productService.save(p);

		
		return "redirect:/product/" + id;
	}

    @GetMapping("/publish")
    public String publishForm() {
        return "publish";
    }

    @GetMapping("/my_listings")
    public String my_listings(Model model) {
		if (model.getAttribute("userName") == null) {
			model.addAttribute("errorMessage", "You should be logged in to view your deals.");
			return "error";
		}
		Optional<User> user = userService.findByName((String) model.getAttribute("userName"));
		if (!user.isPresent()) {
			model.addAttribute("errorMessage", "Your user is not found.");
			return "error";
		}
        model.addAttribute("user", user.get());
        return "my-listings";
    }

    @GetMapping("/my_deals")
    public String my_deals(Model model) {
		if (model.getAttribute("userName") == null) {
			model.addAttribute("errorMessage", "You should be logged in to view your deals.");
			return "error";
		}
		Optional<User> user = userService.findByName((String) model.getAttribute("userName"));
		if (!user.isPresent()) {
			model.addAttribute("errorMessage", "Your user is not found.");
			return "error";
		}
		List<Order> deals = user.get().getMyOrders();
        model.addAttribute("deals_buyer", deals);
		List<Order> deals_seller = orderService.getAllOrders().stream().filter(order -> order.getSellUser().equals(user.get())).toList();
		model.addAttribute("deals_seller", deals_seller);
		model.addAttribute("deals", !deals.isEmpty() || !deals_seller.isEmpty());
        return "my_deals";
    }

	@GetMapping("/create_rating/{id}")
	public String createRating(@PathVariable Long id, Model model) {
		Optional<Order> order = orderService.getOrderById(id);
		if (!order.isPresent()) {
			model.addAttribute("errorMessage", "Order not found.");
			return "error";
		}
		model.addAttribute("order", order.get());
		return "rating_form";
	}

	@GetMapping("/edit_rating/{id}")
	public String editRating(@PathVariable Long id, Model model) {
		Optional<Rating> rating = ratingService.getRatingById(id);
		if (!rating.isPresent()) {
			model.addAttribute("errorMessage", "Rating not found.");
			return "error";
		}
		model.addAttribute("order", rating.get().getOrder());
		model.addAttribute("rating", rating.get());
		return "rating_form";
	}

}

