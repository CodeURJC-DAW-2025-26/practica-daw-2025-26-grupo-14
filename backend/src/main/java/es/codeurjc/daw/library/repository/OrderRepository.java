package es.codeurjc.daw.library.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.daw.library.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
