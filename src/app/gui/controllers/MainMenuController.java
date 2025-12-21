package app.gui.controllers;

import app.gui.NavigationService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import terminal.ServiceContainer;

import java.util.logging.Logger;

/**
 * Controller for the main menu screen.
 * Handles navigation to different portals (Student, Admin, Services, IT Admin) and application exit.
 */
public class MainMenuController {
    private static final Logger logger = Logger.getLogger(MainMenuController.class.getName());
    
    @FXML
    private Button studentButton;
    
    @FXML
    private Button adminButton;
    
    @FXML
    private Button servicesButton;
    
    @FXML
    private Button itAdminButton;
    
    @FXML
    private Button exitButton;
    
    private ServiceContainer services;
    
    /**
     * Initialize the controller.
     * Called automatically by JavaFX after FXML loading.
     */
    @FXML
    public void initialize() {
        services = NavigationService.getServiceContainer();
        logger.info("MainMenuController initialized.");
    }
    
    /**
     * Handle Student Portal button click.
     * Navigates to the student login screen.
     */
    @FXML
    private void handleStudentPortal() {
        logger.info("Navigating to Student Portal...");
        NavigationService.navigateTo("student-login");
    }
    
    /**
     * Handle Admin Dashboard button click.
     * Navigates to the admin login screen.
     */
    @FXML
    private void handleAdminPortal() {
        logger.info("Navigating to Admin Dashboard...");
        NavigationService.navigateTo("admin-login");
    }
    
    /**
     * Handle Service Manager Portal button click.
     * Navigates to the service manager login screen.
     */
    @FXML
    private void handleServicesPortal() {
        logger.info("Navigating to Service Manager Portal...");
        NavigationService.navigateTo("service-login");
    }
    
    /**
     * Handle IT Admin Portal button click.
     * Navigates to the IT admin login screen.
     */
    @FXML
    private void handleITAdminPortal() {
        logger.info("Navigating to IT Admin Portal...");
        NavigationService.navigateTo("itadmin-login");
    }
    
    /**
     * Handle Exit button click.
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        logger.info("Exiting application...");
        Platform.exit();
    }
}
