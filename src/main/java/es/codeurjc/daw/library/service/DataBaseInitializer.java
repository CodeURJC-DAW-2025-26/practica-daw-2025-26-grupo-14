package es.codeurjc.daw.library.service;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import es.codeurjc.daw.library.model.Image;
import es.codeurjc.daw.library.model.Product;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.repository.ProductRepository;
import es.codeurjc.daw.library.repository.UserRepository;
import jakarta.annotation.PostConstruct;
//import es.codeurjc.daw.library.repository.UserRepository;


@Service
public class DataBaseInitializer {

	@Autowired
	private ProductService productService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private ImageService imageService;

	@PostConstruct
	public void init() throws IOException, URISyntaxException {
        if (userRepository.count() > 0 || productRepository.count() > 0) {
            return; // Database already initialized, skip seeding
        }
        // Sample Users
		User seller1 = new User("Mario Martin", "Mario", "mario@martin.com", "Madrid", passwordEncoder.encode("pass"), "12345678A", "USER");
        User seller2 =  new User("Lucia Garcia", "LuGar", "lucia@garcia.com", "Barcelona", passwordEncoder.encode("pass"), "12345678B", "USER");
        User seller3 =  new User("Carlos Lopez", "Carlos", "carlos@lopez.com", "Valencia", passwordEncoder.encode("pass"), "12345678C", "USER");
        User admin =  new User("Admin User", "Admin", "admin@user.com", "Sevilla", passwordEncoder.encode("adminpass"), "12345678D", "USER", "ADMIN");

        seller1.setImage(setImage("/static/my_images/Mario_profile.jpeg"));
        seller2.setImage(setImage("/static/my_images/LuGar_profile.webp"));
        seller3.setImage(setImage("/static/my_images/Carlos_profile.webp"));
        admin.setImage(setImage("/static/my_images/admin_profile.webp"));

        userRepository.save(seller1);
		userRepository.save(seller2);
		userRepository.save(seller3);
        userRepository.save(admin);

        // Sample Products
		Product prod1 = new Product(
            "Camisa de manga larga", seller1, 15, "Ropa",
            "New", "Camisa de manga larga en buen estado, talla M.",  "Phone"
        );
        prod1.setImage(setImage("/static/my_images/camisa.webp"));
        productService.save(prod1);

        Product prod2 = new Product(
            "Bicicleta de montanna", seller2, 120, "Deportes", "Acceptable",
            "Bicicleta de montanna en buen estado, con cambios y frenos funcionales.",  "Chat"
        );
        prod2.setImage(setImage("/static/my_images/bicicleta.webp"));
        productService.save(prod2);
        Product prod3 = new Product(
            "Sofa de dos plazas", seller3, 250, "Hogar", "Like new",
            "Sofa de dos plazas en buen estado, color gris.",  "Both"
        );
        prod3.setImage(setImage("/static/my_images/sofa.webp"));
        productService.save(prod3);


		// Sample users

		
	}

	public Image setImage(String classpathResource) throws IOException {
		Resource image = new ClassPathResource(classpathResource);

		Image createdImage = imageService.createImage(image.getInputStream());
		return createdImage;
	}
}
