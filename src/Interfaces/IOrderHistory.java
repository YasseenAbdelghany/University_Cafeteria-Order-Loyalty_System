package Interfaces;

import Core.OrderHistory;
import java.util.List;

public interface IOrderHistory {
    public boolean saveOrderHistory(OrderHistory history);
    List<OrderHistory> getOrderHistoryByStudent(String studentCode);
    boolean updateOrderStatus(String orderCode, String newStatus);
    OrderHistory getOrderHistoryByOrderCode(String orderCode);
    List<OrderHistory> getAllOrderHistory();
}
