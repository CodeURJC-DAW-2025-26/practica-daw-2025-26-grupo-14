package es.codeurjc.daw.library.service;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.daw.library.model.Order;
import es.codeurjc.daw.library.repository.OrderRepository;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    /*public List<Order> getOrdersByBuyer(User buyer) {
        return orderRepository.findByBuyer(buyer);
    }*/

    public void save(Order order) {
        orderRepository.save(order);
    }

    public void delete(long id) {
        orderRepository.deleteById(id);
    }

    	public boolean exist(long id) {
        return orderRepository.existsById(id);
    }
    
}
