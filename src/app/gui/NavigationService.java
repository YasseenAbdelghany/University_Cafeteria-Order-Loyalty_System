package app.gui;

import app.gui.shared.AlertHelper;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import terminal.ServiceContainer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Singleton service for managing scene navigation and transitions in the JavaFX application.
 * This class handles loading FXML files, initializing controllers, and switching between scenes.
 */
public class NavigationService {
    private static final Logger logger = Logger.getLogger(NavigationService.class.getName());
    
    private static Stage stage;
    private static ServiceContainer serviceContainer;
    private static Map<String, Scene> sceneCache = new HashMap<>();
    private static Map<String, Object> controllerCache = new HashMap<>();
    
    // Private constructor to prevent instantiation
    private NavigationService() {}
    
    /**
     * Initialize the navigation service with the primary stage and service container
     * 
     * @param primaryStage The primary JavaFX stage
     * @param services The service container with all business services
     */
    public static void initialize(Stage primaryStage, ServiceContainer services) {
        stage = primaryStage;
        serviceContainer = services;
        logger.info("NavigationService initialized.");
    }
    
    /**
     * Navigate to a scene by name without passing data
     * 
     * @param sceneName The name of the FXML file (without .fxml extension)
     */
    public static void navigateTo(String sceneName) {
        navigateToWithData(sceneName, null);
    }
    
    /**
     * Navigate to a scene by name and pass data to the controller
     * 
     * @param sceneName The name of the FXML file (without .fxml extension)
     * @param data Data to pass to the controller (can be null)
     */
    public static void navigateToWithData(String sceneName, Object data) {
        try {
            logger.info("Navigating to scene: " + sceneName);
            
            // Load the scene (either from cache or create new)
            Scene scene = loadScene(sceneName);
            
            // Get the controller and pass data if available
            Object controller = controllerCache.get(sceneName);
            if (controller != null && data != null) {
                // Use reflection or specific interfaces to pass data
                passDataToController(controller, data);
            }
            
            // Set the scene on the stage
            stage.setScene(scene);
            
            logger.info("Successfully navigated to: " + sceneName);
            
        } catch (Exception e) {
            logger.severe("Failed to navigate to " + sceneName + ": " + e.getMessage());
            e.printStackTrace();
            AlertHelper.showError("Navigation Error",
                "Failed to load screen: " + sceneName + "\n" + e.getMessage());
        }
    }
    
    /**
     * Load a scene from FXML file, using cache if available
     * 
     * @param sceneName The name of the FXML file (without .fxml extension)
     * @return The loaded Scene object
     * @throws IOException If the FXML file cannot be loaded
     */
    private static Scene loadScene(String sceneName) throws IOException {
        // Check cache first
        if (sceneCache.containsKey(sceneName)) {
            logger.info("Loading scene from cache: " + sceneName);
            return sceneCache.get(sceneName);
        }
        
        // Load from FXML file
        String fxmlPath = "/app/resources/fxml/" + sceneName + ".fxml";
        logger.info("Loading FXML from: " + fxmlPath);
        
        FXMLLoader loader = new FXMLLoader(NavigationService.class.getResource(fxmlPath));
        Parent root = loader.load();
        
        // Create scene and cache it
        Scene scene = new Scene(root);
        sceneCache.put(sceneName, scene);
        
        // Cache the controller
        Object controller = loader.getController();
        if (controller != null) {
            controllerCache.put(sceneName, controller);
        }
        
        return scene;
    }
    
    /**
     * Pass data to a controller using reflection or specific methods
     * 
     * @param controller The controller instance
     * @param data The data to pass
     */
    private static void passDataToController(Object controller, Object data) {
        try {
            // Try common setter methods based on data type
            if (data instanceof Core.Student) {
                controller.getClass().getMethod("setStudent", Core.Student.class).invoke(controller, data);
            } else if (data instanceof Core.Admin) {
                controller.getClass().getMethod("setAdmin", Core.Admin.class).invoke(controller, data);
            } else if (data instanceof ServiceManagers.ServicesManager) {
                controller.getClass().getMethod("setManager", ServiceManagers.ServicesManager.class).invoke(controller, data);
            } else if (data.getClass().getName().contains("ServiceManagerData")) {
                // Handle ServiceManagerData from ServiceLoginController
                controller.getClass().getMethod("setManagerData", data.getClass()).invoke(controller, data);
            } else {
                // Generic setData method
                controller.getClass().getMethod("setData", Object.class).invoke(controller, data);
            }
        } catch (Exception e) {
            logger.warning("Could not pass data to controller: " + e.getMessage());
            // Not all controllers need data, so this is not a critical error
        }
    }
    
    /**
     * Clear the scene cache to force reload of FXML files
     */
    public static void clearCache() {
        sceneCache.clear();
        controllerCache.clear();
        logger.info("Scene cache cleared.");
    }
    
    /**
     * Clear a specific scene from the cache
     * 
     * @param sceneName The name of the scene to clear
     */
    public static void clearScene(String sceneName) {
        sceneCache.remove(sceneName);
        controllerCache.remove(sceneName);
        logger.info("Cleared scene from cache: " + sceneName);
    }
    
    /**
     * Get the service container
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
    public static Stage getStage() {
        return stage;
    }
}
