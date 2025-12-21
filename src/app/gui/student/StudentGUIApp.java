package app.gui.student;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;
import terminal.ServiceContainer;
import app.gui.shared.AlertHelper;

import java.util.logging.Logger;

/**
 * Main JavaFX application entry point for the Student Portal.
 * This class initializes the student application, sets up the primary stage, 
 * and launches the student login screen.
 */
public class StudentGUIApp extends Application {
    private static final Logger logger = Logger.getLogger(StudentGUIApp.class.getName());
    private static ServiceContainer serviceContainer;
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        try {
            logger.info("Starting Student Portal Application...");
            
            primaryStage = stage;
            
            // Initialize the service container with all business services
            logger.info("Initializing service container...");
            serviceContainer = new ServiceContainer();
            
            // Initialize the navigation service with stage and services
            StudentNavigationService.initialize(stage, serviceContainer);
            
            // Get screen bounds for optimal sizing
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

            // Configure the primary stage
            stage.setTitle("Cafeteria - Student Portal");

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

            // Navigate to the student login screen
            logger.info("Navigating to student login...");
            StudentNavigationService.navigateTo("student-login");
            
            // Show the stage
            stage.show();
            
            logger.info("Student Portal Application started successfully.");
            Image icon = new Image("file:D:\\inteljii  ULTIMATE\\IdeaProjects\\CafeteriaSystem1\\icon.png"); // file:CopyPathOfImage
            stage.getIcons().add(icon);
        } catch (Exception e) {
            logger.severe("Failed to start application: " + e.getMessage());
            e.printStackTrace();
            AlertHelper.showError("Startup Error", 
                "Failed to start the Student Portal: " + e.getMessage());
        }
    }

    @Override
    public void stop() {
        logger.info("Shutting down Student Portal Application...");
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
     * Main entry point for the Student Portal application
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
