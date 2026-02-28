package es.codeurjc.daw.library.service;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.repository.UserRepository;

@Service
public class UserService {


    @Autowired
    private UserRepository userRepository;
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByName(String name) {
        return userRepository.findByName(name);
    }

    public void save(User user) {
		userRepository.save(user);
	}

	public void delete(long id) {
		userRepository.deleteById(id);
	}

    	public boolean exist(long id) {
		return userRepository.existsById(id);
	}

        public void banUser(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setBanned(!user.getIsBanned()); // alterna ban/unban
            userRepository.save(user);       // guarda el cambio
        });
    }
        
}


