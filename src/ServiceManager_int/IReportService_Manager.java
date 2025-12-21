package ServiceManager_int;

import ServiceManagers.ReportService_Manager;


import java.util.List;

public interface IReportService_Manager {
    boolean add(ReportService_Manager manager);
    boolean delete(String username);
    boolean update(String username, ReportService_Manager newManager);
    ReportService_Manager findByUsername(String username);
    List<ReportService_Manager> findAll();
    boolean exists(String username);
    void createDefaultManager();
    int count();
}
