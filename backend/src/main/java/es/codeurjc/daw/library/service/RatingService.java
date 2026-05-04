package es.codeurjc.daw.library.service;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.daw.library.model.Rating;
import es.codeurjc.daw.library.repository.RatingRepository;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    public List<Rating> getAllRatings() {
        return ratingRepository.findAll();
    }

    public Optional<Rating> getRatingById(Long id) {
        return ratingRepository.findById(id);
    }

    public void save(Rating rating) {
        ratingRepository.save(rating);
    }

    public void delete(long id) {
        ratingRepository.deleteById(id);
    }

    	public boolean exist(long id) {
        return ratingRepository.existsById(id);
    }
}
