package es.codeurjc.daw.library.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.codeurjc.daw.library.model.Rating;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    @Query("SELECT r.rating, COUNT(r) FROM Rating r GROUP BY r.rating ORDER BY r.rating DESC")
    List<Object[]> countRatingsByValue();

    @Query("SELECT r FROM Rating r WHERE r.rated.id = :ratedId")
    List<Rating> findByRatedId(@Param("ratedId") Long ratedId);


}
