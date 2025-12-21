package Services;

import Core.OrderHistory;
import Core.Order;
import Core.Student;
import DataBase.OrderHistoryDAO;
import Interfaces.IOrderHistory;
import java.time.LocalDateTime;
import java.util.List;

public class OrderHistoryService {
    private final IOrderHistory orderHistoryDAO;

    public OrderHistoryService() {
        this.orderHistoryDAO = new OrderHistoryDAO();
    }

    public OrderHistoryService(IOrderHistory orderHistoryDAO) {
        this.orderHistoryDAO = orderHistoryDAO;
    }

    /**
     * Record order in history when order is completed (explicit total)
     */
    public boolean recordOrderInHistory(Order order, Student student, String paymentMethod, double totalAmount) {
        try {
            OrderHistory history = new OrderHistory(
                student.getName(),
                student.getStudentCode(),
                order.getCode(),
                paymentMethod,
                totalAmount,
                LocalDateTime.now(),
                order.getStatus().toString()
            );
            return orderHistoryDAO.saveOrderHistory(history);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Convenience overload: compute total from order
     */
    public boolean recordOrderInHistory(Order order, Student student, String paymentMethod) {
        double total = order.total().getAmount().doubleValue();
        return recordOrderInHistory(order, student, paymentMethod, total);
    }

    /**
     * Get order history for a specific student
     */
    public List<OrderHistory> getOrderHistoryForStudent(String studentCode) {
        return orderHistoryDAO.getOrderHistoryByStudent(studentCode);
    }

    /**
     * Update order status in history
     */
    public boolean updateOrderStatus(String orderCode, String newStatus) {
        return orderHistoryDAO.updateOrderStatus(orderCode, newStatus);
    }

    /**
     * Get specific order history by order code
     */
    public OrderHistory getOrderHistory(String orderCode) {
        return orderHistoryDAO.getOrderHistoryByOrderCode(orderCode);
    }

    /**
     * Get all order history (for admin use)
     */
    public List<OrderHistory> getAllOrderHistory() {
        return orderHistoryDAO.getAllOrderHistory();
    }
}
