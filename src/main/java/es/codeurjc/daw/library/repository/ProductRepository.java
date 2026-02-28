package es.codeurjc.daw.library.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.daw.library.model.Product;
import es.codeurjc.daw.library.model.User;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findBySeller(User seller);
    List<Product> findByNameContainingIgnoreCase(String name);
    
}
