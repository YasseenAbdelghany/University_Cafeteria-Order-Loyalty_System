package app.gui.controllers;

import Core.Admin;
import Services.AdminLIN_Out;
import app.gui.shared.AlertHelper;
import app.gui.NavigationService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import terminal.ServiceContainer;

import java.util.logging.Logger;

/**
 * Controller for the admin dashboard screen.
 * Provides access to admin functions: managing service managers and viewing reports.
 */
public class AdminDashboardController {
    private static final Logger logger = Logger.getLogger(AdminDashboardController.class.getName());
    
    @FXML
    private Label welcomeLabel;
    
    @FXML
    private Button manageManagersButton;
    
    @FXML
    private Button viewReportsButton;
    
    @FXML
    private Button logoutButton;
    
    private ServiceContainer services;
    private AdminLIN_Out adminService;
    private Admin currentAdmin;
    
    /**
     * Initialize the controller.
     * Called automatically by JavaFX after FXML loading.
     */
    @FXML
    public void initialize() {
        services = NavigationService.getServiceContainer();
        adminService = services.getAdminAuthService();
        logger.info("AdminDashboardController initialized.");
    }
    
    /**
     * Set the current admin and update the UI.
     * Called by NavigationService when navigating to this screen with admin data.
     * 
     * @param admin The logged-in admin
     */
    public void setAdmin(Admin admin) {
        this.currentAdmin = admin;
        updateUI();
    }
    
    /**
     * Update the UI with admin information.
     */
    private void updateUI() {
        if (currentAdmin != null) {
            welcomeLabel.setText("Welcome, " + currentAdmin.getName() + "!");
            logger.info("Admin dashboard loaded for: " + currentAdmin.getName());
        }
    }
    
    /**
     * Handle Manage Service Managers button click.
     * Navigates to the manager management screen.
     */
    @FXML
    private void handleManageManagers() {
        logger.info("Navigating to manager management screen...");
        NavigationService.navigateToWithData("manager-management", currentAdmin);
    }
    
    /**
     * Handle View System Reports button click.
     * Navigates to the reports screen.
     */
    @FXML
    private void handleViewReports() {
        logger.info("Navigating to system reports screen...");
        NavigationService.navigateToWithData("reports", currentAdmin);
    }
    
    /**
     * Handle Logout button click.
     * Logs out the admin and returns to the main menu.
     */
    @FXML
    private void handleLogout() {
        if (currentAdmin != null) {
            logger.info("Admin logout: " + currentAdmin.getName());
            adminService.performLogout(currentAdmin);
            AlertHelper.showInfo("Logged Out", "You have been logged out successfully.");
        }
        NavigationService.navigateTo("main-menu");
    }
}
