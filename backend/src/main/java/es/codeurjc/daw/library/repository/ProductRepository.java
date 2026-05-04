package es.codeurjc.daw.library.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.codeurjc.daw.library.model.Product;
import es.codeurjc.daw.library.model.User;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findBySeller(User seller);
    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByCategoryIgnoreCase(String category);

    List<Product> findBySellerCityIgnoreCase(String city);

    Page<Product> findByCategoryIgnoreCase(String category, Pageable pageable);
    Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p LEFT JOIN p.seller s LEFT JOIN s.myRatings r "
        + "WHERE (:keyword IS NULL OR :keyword = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) "
        + "AND (:category IS NULL OR :category = '' OR LOWER(p.category) = LOWER(:category)) "
        + "AND (:minPrice IS NULL OR p.price >= :minPrice) "
        + "AND (:maxPrice IS NULL OR p.price <= :maxPrice)"
        + "AND (:sellerId IS NULL OR p.seller.id = :sellerId)"
        + "AND (:reported IS NULL OR p.reported)"
        + "GROUP BY p HAVING (:minSellerRate IS NULL OR COALESCE(AVG(r.rating),0) >= :minSellerRate)")
    Page<Product> search(String keyword, String category, Double minPrice, Double maxPrice, Integer minSellerRate, Long sellerId, Boolean reported, Pageable pageable);

    @Query("SELECT p.category, COUNT(p) FROM Product p GROUP BY p.category ORDER BY COUNT(p) DESC")
    List<Object[]> countProductsByCategory();

    @Query("SELECT p.createdAt, COUNT(p) FROM Product p GROUP BY p.createdAt")
    List<Object[]> countProductsByCreatedAt();


}
