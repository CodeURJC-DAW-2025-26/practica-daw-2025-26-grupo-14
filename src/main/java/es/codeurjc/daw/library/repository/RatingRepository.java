package es.codeurjc.daw.library.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.daw.library.model.Rating;

public interface RatingRepository extends JpaRepository<Rating, Long> {

}
