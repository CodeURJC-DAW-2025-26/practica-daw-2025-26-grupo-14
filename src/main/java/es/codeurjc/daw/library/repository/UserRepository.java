package es.codeurjc.daw.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.daw.library.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
