package terminal;

import Services.*;
import DataBase.*;
import Interfaces.*;

import java.util.logging.Logger;

/**
 * Service Locator pattern implementation following Dependency Inversion Principle
 * This class handles all service initialization and dependency injection
 * Now uses only database storage - no more in-memory fallbacks
 */
public class ServiceContainer {
    private static final Logger logger = Logger.getLogger(ServiceContainer.class.getName());

    // Repositories (use interfaces for dependency inversion)
    private final IStudentRepository studentsRepo;
    private final IMenuProvider menuRepo;
    private final IOrderRepository orderRepo;
    private final ILoyaltyProgram loyaltyRepo;

    // Services
    private final StudentManager studentManager;
    private final MenuManager menuManager;
    private final OrderProcessor orderProcessor;
    private final LoyaltyProgramService loyaltyService;
    private final OrderHistoryService orderHistoryService;
    private final NotificationHistoryService notificationHistoryService;
    private final AdminManager adminManager;
    private final AdminLIN_Out adminAuthService;
    private final NotificationService notificationService;
    private final ReportService reportService;
    private final RoleAuthService roleAuthService;
    private final PaymentRegistry paymentRegistry;
    private final AdminManagement_Services adminManagementServices;

    public ServiceContainer() {
        logger.info("Initializing service container with database storage only...");

        // Initialize repositories with database storage only - no more in-memory fallbacks
        try {
            this.studentsRepo = new StudentDAO();
            this.menuRepo = new MenuDAO();
            this.orderRepo = new OrderDAO();
            this.loyaltyRepo = new LoyaltyDAO();
            logger.info("All repositories initialized with database storage.");
        } catch (Exception e) {
            logger.severe("Failed to initialize database repositories: " + e.getMessage());
            throw new RuntimeException("Database initialization failed. Cannot start application without database connection.", e);
        }

        // Initialize core services (Business Layer)
        this.studentManager = new StudentManager(studentsRepo, loyaltyRepo);
        this.menuManager = new MenuManager(menuRepo);
        this.loyaltyService = new LoyaltyProgramService(studentsRepo, loyaltyRepo);
        this.orderHistoryService = new OrderHistoryService();
        this.notificationHistoryService = new NotificationHistoryService();
        this.adminManager = new AdminManager();
        this.adminAuthService = new AdminLIN_Out(adminManager);
        this.notificationService = new NotificationService();
        this.reportService = new ReportService(orderRepo, menuRepo, studentsRepo);
        this.roleAuthService = new RoleAuthService();
        this.paymentRegistry = new PaymentRegistry();
        this.adminManagementServices = new AdminManagement_Services();

        // Create default service managers (idempotent)
        try {
            this.adminManagementServices.createDefaultManagers();
            logger.info("Default service managers ensured.");
        } catch (Exception e) {
            logger.warning("Default service manager creation skipped: " + e.getMessage());
        }

        // Initialize order processor with all dependencies including order and notification history
        this.orderProcessor = new OrderProcessor(
                orderRepo,
                menuRepo,
                loyaltyService,
                orderHistoryService,
                notificationHistoryService
        );

        logger.info("Service container initialization completed successfully.");
    }

    // Getters following Interface Segregation Principle
    public StudentManager getStudentManager() { return studentManager; }
    public MenuManager getMenuManager() { return menuManager; }
    public OrderProcessor getOrderProcessor() { return orderProcessor; }
    public LoyaltyProgramService getLoyaltyService() { return loyaltyService; }
    public OrderHistoryService getOrderHistoryService() { return orderHistoryService; }
    public NotificationHistoryService getNotificationHistoryService() { return notificationHistoryService; }
    public AdminManager getAdminManager() { return adminManager; }
    public AdminLIN_Out getAdminAuthService() { return adminAuthService; }
    public NotificationService getNotificationService() { return notificationService; }
    public ReportService getReportService() { return reportService; }
    public RoleAuthService getRoleAuthService() { return roleAuthService; }
    public PaymentRegistry getPaymentRegistry() { return paymentRegistry; }
    public AdminManagement_Services getAdminManagementServices() { return adminManagementServices; }
    public IStudentRepository getStudentDAO() { return studentsRepo; }
}
