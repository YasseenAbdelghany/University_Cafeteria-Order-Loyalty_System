package app.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import app.gui.shared.AlertHelper;
import app.gui.NavigationService;
import terminal.ServiceContainer;
import Enums.ManagerType;
import Services.RoleAuthService;
import ServiceManagers.*;
import ServiceManagersDAO.*;

/**
 * Controller for the Service Manager Login screen.
 * Handles authentication for different types of service managers.
 */
public class ServiceLoginController {
    
    @FXML
    private ComboBox<ManagerType> managerTypeComboBox;
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    private ServiceContainer services;
    private RoleAuthService roleAuthService;
    
    @FXML
    public void initialize() {
        services = NavigationService.getServiceContainer();
        roleAuthService = services.getRoleAuthService();
        
        // Populate manager type combo box
        managerTypeComboBox.getItems().addAll(ManagerType.values());
    }
    
    @FXML
    private void handleLogin() {
        // Validate inputs
        ManagerType managerType = managerTypeComboBox.getValue();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        
        if (managerType == null) {
            AlertHelper.showError("Validation Error", "Please select a manager type");
            return;
        }
        
        if (username.isEmpty()) {
            AlertHelper.showError("Validation Error", "Username cannot be empty");
            return;
        }
        
        if (password.isEmpty()) {
            AlertHelper.showError("Validation Error", "Password cannot be empty");
            return;
        }
        
        try {
            // Authenticate via RoleAuthService
            boolean authenticated = roleAuthService.login(managerType.toString(), username, password);
            
            if (!authenticated) {
                AlertHelper.showError("Login Failed", "Invalid username or password");
                return;
            }
            
            // Retrieve the manager object based on type
            ServicesManager manager = retrieveManager(managerType, username);
            
            if (manager == null) {
                AlertHelper.showError("Login Failed", "Could not retrieve manager information");
                return;
            }
            
            // Navigate to service dashboard with manager data
            navigateToServiceDashboard(manager, managerType);
            
        } catch (Exception e) {
            AlertHelper.showError("Error", "Login failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Retrieve the manager object from the database based on type and username
     */
    private ServicesManager retrieveManager(ManagerType type, String username) {
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
    }
    
    /**
     * Navigate to service dashboard with manager data
     */
    private void navigateToServiceDashboard(ServicesManager manager, ManagerType type) {
        // Create a data object to pass both manager and type
        ServiceManagerData data = new ServiceManagerData(manager, type);
        NavigationService.navigateToWithData("service-dashboard", data);
    }
    
    @FXML
    private void handleBack() {
        NavigationService.navigateTo("main-menu");
    }
    
    /**
     * Data class to pass both manager and type to the dashboard
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
