package es.codeurjc.daw.library.service;

import java.io.IOException;
import java.net.URISyntaxException;
//import java.util.ArrayList;
//import java.util.Arrays;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.core.io.Resource;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import es.codeurjc.daw.library.model.Product;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.repository.UserRepository;
//import es.codeurjc.daw.library.repository.UserRepository;

@Service
public class DataBaseInitializer {

	@Autowired
	private ProductService productService;

    @Autowired
    private UserRepository userRepository;

	@PostConstruct
	public void init() throws IOException, URISyntaxException {

		// Sample Users
		User seller1 = new User("Mario Martin", "Mario", "mario@martin.com", "Madrid", "psswd");
        User seller2 =  new User("Lucia Garcia", "LuGar", "lucia@garcia.com", "Barcelona", "psswd");
        User seller3 =  new User("Carlos Lopez", "Carlos", "carlos@lopez.com", "Valencia", "psswd");

        userRepository.save(seller1);
		userRepository.save(seller2);
		userRepository.save(seller3);

        // Sample Products
		Product prod1 = new Product(
            "Camisa de manga larga", seller1, 15, "Ropa",
            "Camisa de manga larga en buen estado, talla M."
        );
        productService.save(prod1);

        Product prod2 = new Product(
            "Bicicleta de montanna", seller2, 120, "Deportes",
            "Bicicleta de montanna en buen estado, con cambios y frenos funcionales."
        );
        productService.save(prod2);
        Product prod3 = new Product(
            "Sofa de dos plazas", seller3, 250, "Hogar",
            "Sofa de dos plazas en buen estado, color gris."
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
