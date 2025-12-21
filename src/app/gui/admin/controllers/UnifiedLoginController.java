package app.gui.admin.controllers;

import Core.ITAdmin;
import Core.Admin;
import Services.AdminLIN_Out;
import Services.RoleAuthService;
import app.gui.shared.AlertHelper;
import app.gui.admin.AdminNavigationService;
import app.gui.admin.UserRole;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import terminal.ServiceContainer;
import Enums.ManagerType;
import ServiceManagers.*;
import ServiceManagersDAO.*;

import java.util.logging.Logger;

/**
 * Controller for the unified login screen in the Administrative Application.
 * Handles authentication for all administrative user types (Admin, Service Manager, IT Admin)
 * and routes users to the appropriate dashboard based on their role.
 */
public class UnifiedLoginController {
    private static final Logger logger = Logger.getLogger(UnifiedLoginController.class.getName());
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Button loginButton;
    
    private ServiceContainer services;
    private AdminLIN_Out adminAuthService;
    private RoleAuthService roleAuthService;
    
    /**
     * Data class to hold authentication result
     */
    private static class AuthResult {
        UserRole role;
        Object userData;
        ManagerType managerType;
        
        AuthResult(UserRole role, Object userData) {
            this.role = role;
            this.userData = userData;
            this.managerType = null;
        }
        
        AuthResult(UserRole role, Object userData, ManagerType managerType) {
            this.role = role;
            this.userData = userData;
            this.managerType = managerType;
        }
    }
    
    /**
     * Initialize the controller.
     * Called automatically by JavaFX after FXML loading.
     */
    @FXML
    public void initialize() {
        services = AdminNavigationService.getServiceContainer();
        adminAuthService = services.getAdminAuthService();
        roleAuthService = services.getRoleAuthService();
        
        // Clear login fields on initialization (for logout)
        if (usernameField != null) {
            usernameField.clear();
        }
        if (passwordField != null) {
            passwordField.clear();
        }
        
        logger.info("UnifiedLoginController initialized.");
    }
    
    /**
     * Handle Login button click.
     * Validates inputs, detects user role, and navigates to appropriate dashboard.
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        
        // Validate inputs
        if (username.isEmpty()) {
            AlertHelper.showError("Validation Error", "Username cannot be empty.");
            usernameField.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            AlertHelper.showError("Validation Error", "Password cannot be empty.");
            passwordField.requestFocus();
            return;
        }
        
        try {
            // Detect user role and authenticate
            logger.info("Attempting unified login for username: " + username);
            AuthResult authResult = detectUserRole(username, password);
            
            if (authResult.role == UserRole.UNKNOWN) {
                AlertHelper.showError("Login Failed", "Invalid username or password.");
                passwordField.clear();
                usernameField.requestFocus();
                return;
            }
            
            // Login successful - navigate to appropriate dashboard
            logger.info("Login successful. Role: " + authResult.role);
            navigateToRoleDashboard(authResult);
            
        } catch (Exception e) {
            logger.severe("Error during unified login: " + e.getMessage());
            e.printStackTrace();
            AlertHelper.showError("Login Error", "An error occurred during login: " + e.getMessage());
        }
    }
    
    /**
     * Detect user role by attempting authentication against all user types.
     * Checks in order: IT Admin root account, IT Admin, Admin, Service Managers.
     * 
     * @param username The username entered
     * @param password The password entered
     * @return AuthResult containing role and user data
     */
    private AuthResult detectUserRole(String username, String password) {
        // Check IT Admin root account first (special case)
        if ("root".equals(username) && "root".equals(password)) {
            logger.info("Root account detected");
            // Create a special ITAdmin object for root
            ITAdmin rootITAdmin = new ITAdmin();
            rootITAdmin.setName("IT Administrator");
            rootITAdmin.setUserName("root");
            rootITAdmin.setPassword("root");
            return new AuthResult(UserRole.IT_ADMIN, rootITAdmin);
        }
        
        // Try Admin authentication (all admin table users except root)
        try {
            Admin admin = adminAuthService.login(username, password);
            if (admin != null) {
                logger.info("Admin authentication successful");
                return new AuthResult(UserRole.ADMIN, admin);
            }
        } catch (Exception e) {
            logger.fine("Admin authentication failed: " + e.getMessage());
        }
        
        // Try Service Manager authentication for each manager type
        for (ManagerType type : ManagerType.values()) {
            try {
                boolean authenticated = roleAuthService.login(type.toString(), username, password);
                if (authenticated) {
                    logger.info("Service Manager authentication successful. Type: " + type);
                    ServicesManager manager = retrieveManager(type, username);
                    if (manager != null) {
                        return new AuthResult(UserRole.SERVICE_MANAGER, manager, type);
                    }
                }
            } catch (Exception e) {
                logger.fine("Service Manager authentication failed for type " + type + ": " + e.getMessage());
            }
        }
        
        // No authentication succeeded
        logger.info("All authentication attempts failed for username: " + username);
        return new AuthResult(UserRole.UNKNOWN, null);
    }
    
    /**
     * Retrieve the service manager object from the database based on type and username.
     * 
     * @param type The manager type
     * @param username The username
     * @return The ServicesManager object or null if not found
     */
    private ServicesManager retrieveManager(ManagerType type, String username) {
        try {
            switch (type) {
                case STUDENT:
                    StudentManagementDAO studentDAO = new StudentManagementDAO();
                    return studentDAO.findByUsername(username);
                    
                case MENU:
                    MenuManagementDAO menuDAO = new MenuManagementDAO();
                    return menuDAO.findByUsername(username);
                    
                case ORDER:
                    OrderManagementDAO orderDAO = new OrderManagementDAO();
                    return orderDAO.findByUsername(username);
                    
                case PAYMENT:
                    PaymentService_ManagerDAO paymentDAO = new PaymentService_ManagerDAO();
                    return paymentDAO.findByUsername(username);
                    
                case REPORT:
                    ReportService_ManagerDAO reportDAO = new ReportService_ManagerDAO();
                    return reportDAO.findByUsername(username);
                    
                case NOTIFICATION:
                    NotifcationService_ManagerDAO notificationDAO = new NotifcationService_ManagerDAO();
                    return notificationDAO.findByUsername(username);
                    
                default:
                    return null;
            }
        } catch (Exception e) {
            logger.warning("Failed to retrieve manager: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Navigate to the appropriate dashboard based on user role.
     * 
     * @param authResult The authentication result containing role and user data
     */
    private void navigateToRoleDashboard(AuthResult authResult) {
        switch (authResult.role) {
            case ADMIN:
                Admin admin = (Admin) authResult.userData;
                logger.info("Navigating to admin dashboard for: " + admin.getName());
                AlertHelper.showSuccess("Login Successful", "Welcome, " + admin.getName() + "!");
                AdminNavigationService.navigateToWithData("admin-dashboard", admin);
                break;
                
            case SERVICE_MANAGER:
                ServicesManager manager = (ServicesManager) authResult.userData;
                logger.info("Navigating to manager dashboard for: " + manager.getName() + " (" + authResult.managerType + ")");
                AlertHelper.showSuccess("Login Successful", "Welcome, " + manager.getName() + "!");
                navigateToManagerDashboard(manager, authResult.managerType);
                break;
                
            case IT_ADMIN:
                ITAdmin itAdmin = (ITAdmin) authResult.userData;
                logger.info("Navigating to IT admin dashboard for: " + itAdmin.getName());
                AlertHelper.showSuccess("Login Successful", "Welcome, " + itAdmin.getName() + "!");
                AdminNavigationService.navigateToWithData("itadmin-dashboard", itAdmin);
                break;
                
            case UNKNOWN:
            default:
                logger.warning("Cannot navigate - unknown role");
                AlertHelper.showError("Navigation Error", "Unable to determine user role.");
                break;
        }
    }
    
    /**
     * Navigate to the specific manager dashboard based on manager type.
     * 
     * @param manager The service manager object
     * @param type The manager type
     */
    private void navigateToManagerDashboard(ServicesManager manager, ManagerType type) {
        switch (type) {
            case STUDENT:
                AdminNavigationService.navigateToWithData("student-manager-dashboard", manager);
                break;
                
            case MENU:
                AdminNavigationService.navigateToWithData("menu-manager-dashboard", manager);
                break;
                
            case ORDER:
                AdminNavigationService.navigateToWithData("order-manager-dashboard", manager);
                break;
                
            case PAYMENT:
                AdminNavigationService.navigateToWithData("payment-manager-dashboard", manager);
                break;
                
            case REPORT:
                AdminNavigationService.navigateToWithData("report-manager-dashboard", manager);
                break;
                
            case NOTIFICATION:
                AdminNavigationService.navigateToWithData("notification-manager-dashboard", manager);
                break;
                
            default:
                logger.warning("Unknown manager type: " + type);
                AlertHelper.showError("Navigation Error", "Unknown manager type");
                break;
        }
    }
    
    /**
     * Data class to pass both manager and type to the service dashboard.
     * This matches the structure expected by ServiceDashboardController.
     */
    public static class ServiceManagerData {
        private final ServicesManager manager;
        private final ManagerType type;
        
        public ServiceManagerData(ServicesManager manager, ManagerType type) {
            this.manager = manager;
            this.type = type;
        }
        
        public ServicesManager getManager() {
            return manager;
        }
        
        public ManagerType getType() {
            return type;
        }
    }
}
