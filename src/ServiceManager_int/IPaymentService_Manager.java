package ServiceManager_int;


import ServiceManagers.PaymentService_Manager;


import java.util.List;

public interface IPaymentService_Manager {
    boolean add(PaymentService_Manager manager);
    boolean delete(String username);
    boolean update(String username, PaymentService_Manager newManager);
    PaymentService_Manager findByUsername(String username);
    List<PaymentService_Manager> findAll();
    boolean exists(String username);
    void createDefaultManager();
    int count();
}
