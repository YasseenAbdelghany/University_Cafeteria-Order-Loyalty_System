package ServiceManager_int;

import ServiceManagers.MenuManagement;


import java.util.List;

public interface IMenuManagement {
    boolean add(MenuManagement manager);
    boolean delete(String username);
    boolean update(String username, MenuManagement newManager);
    MenuManagement findByUsername(String username);
    List<MenuManagement> findAll();
    boolean exists(String username);
    void createDefaultManager();
    int count();
}
