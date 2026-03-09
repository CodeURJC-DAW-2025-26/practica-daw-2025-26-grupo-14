package es.codeurjc.daw.library.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.codeurjc.daw.library.model.Product;
import es.codeurjc.daw.library.model.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findBySeller(User seller);
    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByCategoryIgnoreCase(String category);

    List<Product> findBySellerCityIgnoreCase(String city);

    Page<Product> findByCategoryIgnoreCase(String category, Pageable pageable);
    Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

    @Query("SELECT p.category, COUNT(p) FROM Product p GROUP BY p.category ORDER BY COUNT(p) DESC")
    List<Object[]> countProductsByCategory();

    @Query("SELECT p.createdAt, COUNT(p) FROM Product p GROUP BY p.createdAt")
    List<Object[]> countProductsByCreatedAt();


}
