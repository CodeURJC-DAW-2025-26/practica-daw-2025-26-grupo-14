package es.codeurjc.daw.library.service;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import es.codeurjc.daw.library.model.Product;
import es.codeurjc.daw.library.model.User;
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
    PasswordEncoder passwordEncoder;

	@PostConstruct
	public void init() throws IOException, URISyntaxException {

        // Sample Users
		User seller1 = new User("Mario Martin", "Mario", "mario@martin.com", "Madrid", passwordEncoder.encode("pass"), "USER");
        User seller2 =  new User("Lucia Garcia", "LuGar", "lucia@garcia.com", "Barcelona", passwordEncoder.encode("pass"), "USER");
        User seller3 =  new User("Carlos Lopez", "Carlos", "carlos@lopez.com", "Valencia", passwordEncoder.encode("pass"), "USER");


        userRepository.save(seller1);
		userRepository.save(seller2);
		userRepository.save(seller3);

        // Sample Products
		Product prod1 = new Product(
            "Camisa de manga larga", seller1, 15, "Ropa",
            "Camisa de manga larga en buen estado, talla M.", null, "Clothing"
        );
        productService.save(prod1);

        Product prod2 = new Product(
            "Bicicleta de montanna", seller2, 120, "Deportes",
            "Bicicleta de montanna en buen estado, con cambios y frenos funcionales.", null, "Electronics"
        );
        productService.save(prod2);
        Product prod3 = new Product(
            "Sofa de dos plazas", seller3, 250, "Hogar",
            "Sofa de dos plazas en buen estado, color gris.", null, "Furniture"
        );
        productService.save(prod3);


		// Sample users

		/*userRepository.save(new User("user", passwordEncoder.encode("pass"), "USER"));
		userRepository.save(new User("admin", passwordEncoder.encode("adminpass"), "USER", "ADMIN"));
	}

	public void setBookImage(Book book, String classpathResource) throws IOException {
		Resource image = new ClassPathResource(classpathResource);

		Image createdImage = imageService.createImage(image.getInputStream());
		book.setImage(createdImage);*/
	}
}
