package Services;

import Enums.ManagerType;
import ServiceManager_int.*;
import ServiceManagersDAO.*;
import ServiceManagers.*;
import java.util.List;


/**
 * AdminManagement_Services follows SOLID principles:
 * S - Single Responsibility: Manages all service manager operations
 * O - Open/Closed: Can be extended with new manager types without modification
 * L - Liskov Substitution: All DAOs implement their specific interfaces
 * I - Interface Segregation: Uses specific interfaces for different operations
 * D - Dependency Inversion: Depends on abstractions (interfaces), not concrete classes
 */
public class AdminManagement_Services {

    private final IStudentManagement studentDAO;
    private final IMenuManagement menuDAO;
    private final IOrderManagement orderDAO;
    private final IPaymentService_Manager paymentDAO;
    private final IReportService_Manager reportDAO;
    private final INotifcationService_Manager notificationDAO;

    public AdminManagement_Services() {

        // Dependency injection through constructor - depending on abstractions, not concretions
        this.studentDAO = new StudentManagementDAO();
        this.menuDAO = new MenuManagementDAO();
        this.orderDAO = new OrderManagementDAO();
        this.paymentDAO = new PaymentService_ManagerDAO();
        this.reportDAO = new ReportService_ManagerDAO();
        this.notificationDAO = new NotifcationService_ManagerDAO();
    }

    public AdminManagement_Services(IStudentManagement studentDAO,
                                   IMenuManagement menuDAO,
                                   IOrderManagement orderDAO,
                                   IPaymentService_Manager paymentDAO,
                                   IReportService_Manager reportDAO,
                                   INotifcationService_Manager notificationDAO) {
        this.studentDAO = studentDAO;
        this.menuDAO = menuDAO;
        this.orderDAO = orderDAO;
        this.paymentDAO = paymentDAO;
        this.reportDAO = reportDAO;
        this.notificationDAO = notificationDAO;
    }


    public boolean addManager(ManagerType type, ServicesManager manager) {
        if (type == null || manager == null) {
            throw new IllegalArgumentException("Manager type and manager cannot be null");
        }

        switch (type) {
            case STUDENT:
                if (manager instanceof StudentManagement) {
                    return studentDAO.add((StudentManagement) manager);
                }
                break;
            case MENU:
                if (manager instanceof MenuManagement) {
                    return menuDAO.add((MenuManagement) manager);
                }
                break;
            case ORDER:
                if (manager instanceof OrderManagement) {
                    return orderDAO.add((OrderManagement) manager);
                }
                break;
            case PAYMENT:
                if (manager instanceof PaymentService_Manager) {
                    return paymentDAO.add((PaymentService_Manager) manager);
                }
                break;
            case REPORT:
                if (manager instanceof ReportService_Manager) {
                    return reportDAO.add((ReportService_Manager) manager);
                }
                break;
            case NOTIFICATION:
                if (manager instanceof NotifcationService_Manager) {
                    return notificationDAO.add((NotifcationService_Manager) manager);
                }
                break;
        }
        throw new IllegalArgumentException("Invalid manager type or manager class mismatch: " + type);
    }


    public boolean deleteManager(ManagerType type, String username) {
        if (type == null || username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Manager type and username cannot be null or empty");
        }

        switch (type) {
            case STUDENT:
                return studentDAO.delete(username);
            case MENU:
                return menuDAO.delete(username);
            case ORDER:
                return orderDAO.delete(username);
            case PAYMENT:
                return paymentDAO.delete(username);
            case REPORT:
                return reportDAO.delete(username);
            case NOTIFICATION:
                return notificationDAO.delete(username);
            default:
                throw new IllegalArgumentException("Unsupported manager type: " + type);
        }
    }


    public boolean updateManager(ManagerType type, String username, ServicesManager newManager) {
        if (type == null || username == null || newManager == null) {
            throw new IllegalArgumentException("Parameters cannot be null");
        }

        switch (type) {
            case STUDENT:
                if (newManager instanceof StudentManagement) {
                    return studentDAO.update(username, (StudentManagement) newManager);
                }
                break;
            case MENU:
                if (newManager instanceof MenuManagement) {
                    return menuDAO.update(username, (MenuManagement) newManager);
                }
                break;
            case ORDER:
                if (newManager instanceof OrderManagement) {
                    return orderDAO.update(username, (OrderManagement) newManager);
                }
                break;
            case PAYMENT:
                if (newManager instanceof PaymentService_Manager) {
                    return paymentDAO.update(username, (PaymentService_Manager) newManager);
                }
                break;
            case REPORT:
                if (newManager instanceof ReportService_Manager) {
                    return reportDAO.update(username, (ReportService_Manager) newManager);
                }
                break;
            case NOTIFICATION:
                if (newManager instanceof NotifcationService_Manager) {
                    return notificationDAO.update(username, (NotifcationService_Manager) newManager);
                }
                break;
        }
        throw new IllegalArgumentException("Invalid manager type or manager class mismatch: " + type);
    }


    public ServicesManager findManager(ManagerType type, String username) {
        if (type == null || username == null) {
            return null;
        }

        switch (type) {
            case STUDENT:
                return studentDAO.findByUsername(username);
            case MENU:
                return menuDAO.findByUsername(username);
            case ORDER:
                return orderDAO.findByUsername(username);
            case PAYMENT:
                return paymentDAO.findByUsername(username);
            case REPORT:
                return reportDAO.findByUsername(username);
            case NOTIFICATION:
                return notificationDAO.findByUsername(username);
            default:
                return null;
        }
    }

    /**
     * Get all managers of a specific type
     */
    public List<? extends ServicesManager> getAllManagers(ManagerType type) {
        if (type == null) {
            throw new IllegalArgumentException("Manager type cannot be null");
        }

        switch (type) {
            case STUDENT:
                return studentDAO.findAll();
            case MENU:
                return menuDAO.findAll();
            case ORDER:
                return orderDAO.findAll();
            case PAYMENT:
                return paymentDAO.findAll();
            case REPORT:
                return reportDAO.findAll();
            case NOTIFICATION:
                return notificationDAO.findAll();
            default:
                throw new IllegalArgumentException("Unsupported manager type: " + type);
        }
    }

    /**
     * Check if a manager exists
     */
    public boolean managerExists(ManagerType type, String username) {
        if (type == null || username == null) {
            return false;
        }

        switch (type) {
            case STUDENT:
                return studentDAO.exists(username);
            case MENU:
                return menuDAO.exists(username);
            case ORDER:
                return orderDAO.exists(username);
            case PAYMENT:
                return paymentDAO.exists(username);
            case REPORT:
                return reportDAO.exists(username);
            case NOTIFICATION:
                return notificationDAO.exists(username);
            default:
                return false;
        }
    }

    /**
     * Create default managers for all types
     */
    public void createDefaultManagers() {
        studentDAO.createDefaultManager();
        menuDAO.createDefaultManager();
        orderDAO.createDefaultManager();
        paymentDAO.createDefaultManager();
        reportDAO.createDefaultManager();
        notificationDAO.createDefaultManager();
    }

    /**
     * Get manager count for a specific type
     */
    public int getManagerCount(ManagerType type) {
        if (type == null) {
            return 0;
        }

        switch (type) {
            case STUDENT:
                return studentDAO.count();
            case MENU:
                return menuDAO.count();
            case ORDER:
                return orderDAO.count();
            case PAYMENT:
                return paymentDAO.count();
            case REPORT:
                return reportDAO.count();
            case NOTIFICATION:
                return notificationDAO.count();
            default:
                return 0;
        }
    }

    /**
     * Get total manager count across all types
     */
    public int getTotalManagerCount() {
        return studentDAO.count() + menuDAO.count() + orderDAO.count() +
               paymentDAO.count() + reportDAO.count() + notificationDAO.count();
    }

    /**
     * Display all managers of a specific type
     */
    public void displayManagers(ManagerType type) {
        if (type == null) {
            System.out.println("Invalid manager type");
            return;
        }

        // Wild Card to handle different manager types
        List<? extends ServicesManager> managers = getAllManagers(type);
        if (managers.isEmpty()) {
            System.out.println("No " + type.name().toLowerCase() + " managers found");
            return;
        }

        System.out.println("\n=== " + type.name() + " MANAGERS ===");
        System.out.printf("%-5s %-20s %-15s %-15s%n", "ID", "Name", "Phone Number", "Username");
        System.out.println("--------------------------------------------------------");
        for (ServicesManager manager : managers) {
            System.out.printf("%-5d %-20s %-15s %-15s%n",
                manager.getId(),
                manager.getName(),
                manager.getPhoneNumber(),
                manager.getUsername());
        }
    }
}

