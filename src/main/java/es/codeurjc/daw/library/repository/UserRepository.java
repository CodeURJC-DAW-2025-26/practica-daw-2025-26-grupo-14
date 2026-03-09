package es.codeurjc.daw.library.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.codeurjc.daw.library.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
     Optional<User> findByName(String name);
     Optional<User> findByEmail(String email);
     Optional<User> findBydni(String dni);
     @Query("SELECT u.createdAt, COUNT(u) FROM User u GROUP BY u.createdAt")
     List<Object[]> countUsersByCreatedAt();
}
