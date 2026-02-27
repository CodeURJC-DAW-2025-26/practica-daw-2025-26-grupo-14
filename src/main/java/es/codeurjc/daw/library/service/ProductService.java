package es.codeurjc.daw.library.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.daw.library.model.Product;
import es.codeurjc.daw.library.repository.ProductRepository;

@Service
public class ProductService {


    @Autowired
    private ProductRepository productRepository;
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public void save(Product product) {
        //product.setShortDescription(shortDescription);
		productRepository.save(product);
	}

	public void delete(long id) {
		productRepository.deleteById(id);
	}

    	public boolean exist(long id) {
		return productRepository.existsById(id);
	}
}

