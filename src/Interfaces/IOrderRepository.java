package Interfaces;

import Core.Order;
import Core.TopItem;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IOrderRepository {
    void save(Order order);
    Order findById(int id);
    List<Order> findPending();
    List<Order> findByStudentId(int studentId);
    void update(Order order);
    List<Order> findAll(); // Existing
}
