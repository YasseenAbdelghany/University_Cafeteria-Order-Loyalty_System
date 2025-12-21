package app.gui.admin;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;
import terminal.ServiceContainer;
import app.gui.shared.AlertHelper;

import java.util.logging.Logger;

/**
 * Main JavaFX application entry point for the Administrative Portal.
 * This class initializes the administrative application, sets up the primary stage, 
 * and launches the unified login screen for all administrative users.
 */
public class AdminGUIApp extends Application {
    private static final Logger logger = Logger.getLogger(AdminGUIApp.class.getName());
    private static ServiceContainer serviceContainer;
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        try {
            logger.info("Starting Administrative Portal Application...");
            
            primaryStage = stage;
            
            // Initialize the service container with all business services
            logger.info("Initializing service container...");
            serviceContainer = new ServiceContainer();
            
            // Initialize the navigation service with stage and services
            AdminNavigationService.initialize(stage, serviceContainer);
            
            // Get screen bounds for optimal sizing
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

            // Configure the primary stage
            stage.setTitle("Cafeteria - Administration");

            // Set stage to match screen dimensions
            stage.setX(screenBounds.getMinX());
            stage.setY(screenBounds.getMinY());
            stage.setWidth(screenBounds.getWidth());
            stage.setHeight(screenBounds.getHeight());

            // Maximize the window
            stage.setMaximized(true);

            // Enable fullscreen without hint message
            stage.setFullScreen(true);
            stage.setFullScreenExitHint(""); // Empty hint = no message shown

            // Navigate to the unified login screen
            logger.info("Navigating to unified login...");
            AdminNavigationService.navigateTo("unified-login");
            
            // Show the stage
            stage.show();
            
            logger.info("Administrative Portal Application started successfully in fullscreen mode.");

            //
            Image icon = new Image("file:D:\\inteljii  ULTIMATE\\IdeaProjects\\CafeteriaSystem1\\icon2.png"); // file:CopyPathOfImage
            stage.getIcons().add(icon);
        } catch (Exception e) {
            logger.severe("Failed to start application: " + e.getMessage());
            e.printStackTrace();
            AlertHelper.showError("Startup Error", 
                "Failed to start the Administrative Portal: " + e.getMessage());
        }
    }

    @Override
    public void stop() {
        logger.info("Shutting down Administrative Portal Application...");
        // Cleanup resources if needed
    }

    /**
     * Get the service container instance
     * 
     * @return The service container instance
     */
    public static ServiceContainer getServiceContainer() {
        return serviceContainer;
    }

    /**
     * Get the primary stage
     * 
     * @return The primary stage instance
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Main entry point for the Administrative Portal application
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
