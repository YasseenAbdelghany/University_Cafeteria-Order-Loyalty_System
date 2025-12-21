package app.gui;

import javafx.application.Application;
import javafx.stage.Stage;
import terminal.ServiceContainer;
import app.gui.shared.AlertHelper;

import java.util.logging.Logger;

/**
 * Main JavaFX application entry point for the Cafeteria Management System GUI.
 * This class initializes the application, sets up the primary stage, and launches the main menu.
 */
public class CafeteriaGUIApp extends Application {
    private static final Logger logger = Logger.getLogger(CafeteriaGUIApp.class.getName());
    private static ServiceContainer serviceContainer;
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        try {
            logger.info("Starting Cafeteria GUI Application...");
            
            primaryStage = stage;
            
            // Initialize the service container with all business services
            logger.info("Initializing service container...");
            serviceContainer = new ServiceContainer();
            
            // Initialize the navigation service with stage and services
            NavigationService.initialize(stage, serviceContainer);
            
            // Configure the primary stage
            stage.setTitle("Cafeteria Management System");
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            
            // Navigate to the main menu
            logger.info("Navigating to main menu...");
            NavigationService.navigateTo("main-menu");
            
            // Show the stage
            stage.show();
            
            logger.info("Cafeteria GUI Application started successfully.");
            
        } catch (Exception e) {
            logger.severe("Failed to start application: " + e.getMessage());
            e.printStackTrace();
            AlertHelper.showError("Startup Error", 
                "Failed to start the application: " + e.getMessage());
        }
    }

    @Override
    public void stop() {
        logger.info("Shutting down Cafeteria GUI Application...");
        // Cleanup resources if needed
    }

    /**
     * Get the service container instance
     */
    public static ServiceContainer getServiceContainer() {
        return serviceContainer;
    }

    /**
     * Get the primary stage
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Main entry point for the GUI application
     */
    public static void main(String[] args) {
        launch(args);
    }
}
