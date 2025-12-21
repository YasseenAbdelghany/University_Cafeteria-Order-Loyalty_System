package app.gui.controllers;

import Core.Admin;
import Services.AdminLIN_Out;
import app.gui.shared.AlertHelper;
import app.gui.NavigationService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import terminal.ServiceContainer;

import java.util.logging.Logger;

/**
 * Controller for the admin login screen.
 * Handles admin authentication and navigation to the admin dashboard.
 */
public class AdminLoginController {
    private static final Logger logger = Logger.getLogger(AdminLoginController.class.getName());
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private Button backButton;
    
    private ServiceContainer services;
    private AdminLIN_Out adminService;
    
    /**
     * Initialize the controller.
     * Called automatically by JavaFX after FXML loading.
     */
    @FXML
    public void initialize() {
        services = NavigationService.getServiceContainer();
        adminService = services.getAdminAuthService();
        logger.info("AdminLoginController initialized.");
    }
    
    /**
     * Handle Login button click.
     * Validates inputs and authenticates the admin.
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
            // Attempt login
            logger.info("Attempting admin login for username: " + username);
            Admin admin = adminService.login(username, password);
            
            if (admin == null) {
                AlertHelper.showError("Login Failed", "Invalid username or password.");
                passwordField.clear();
                usernameField.requestFocus();
                return;
            }
            
            // Login successful
            logger.info("Admin login successful: " + admin.getName());
            AlertHelper.showSuccess("Login Successful", "Welcome, " + admin.getName() + "!");
            
            // Navigate to admin dashboard with admin data
            NavigationService.navigateToWithData("admin-dashboard", admin);
            
        } catch (Exception e) {
            logger.severe("Error during admin login: " + e.getMessage());
            e.printStackTrace();
            AlertHelper.showError("Login Error", "An error occurred during login: " + e.getMessage());
        }
    }
    
    /**
     * Handle Back button click.
     * Returns to the main menu.
     */
    @FXML
    private void handleBack() {
        logger.info("Returning to main menu from admin login.");
        NavigationService.navigateTo("main-menu");
    }
}
