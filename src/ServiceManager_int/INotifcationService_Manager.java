package ServiceManager_int;

import ServiceManagers.NotifcationService_Manager;


import java.util.List;

public interface INotifcationService_Manager {
    boolean add(NotifcationService_Manager manager);
    boolean delete(String username);
    boolean update(String username, NotifcationService_Manager newManager);
    NotifcationService_Manager findByUsername(String username);
    List<NotifcationService_Manager> findAll();
    boolean exists(String username);
    void createDefaultManager();
    int count();
}
