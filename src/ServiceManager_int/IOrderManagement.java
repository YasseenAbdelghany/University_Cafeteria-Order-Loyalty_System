package ServiceManager_int;

import ServiceManagers.OrderManagement;


import java.util.List;

public interface IOrderManagement {
    boolean add(OrderManagement manager);
    boolean delete(String username);
    boolean update(String username, OrderManagement newManager);
    OrderManagement findByUsername(String username);
    List<OrderManagement> findAll();
    boolean exists(String username);
    void createDefaultManager();
    int count();
}
