package app.gui.shared;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * Utility class for displaying styled alerts, dialogs, and confirmations in the JavaFX application.
 * Provides consistent user feedback across all screens.
 */
public class AlertHelper {
    private static final Logger logger = Logger.getLogger(AlertHelper.class.getName());
    
    // Private constructor to prevent instantiation
    private AlertHelper() {}
    
    /**
     * Display a success message dialog
     * 
     * @param title The dialog title
     * @param message The success message to display
     */
    public static void showSuccess(String title, String message) {
        logger.info("Success: " + title + " - " + message);
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText("Success");
        alert.setContentText(message);
        styleAlert(alert);
        alert.showAndWait();
    }
    
    /**
     * Display an error message dialog
     * 
     * @param title The dialog title
     * @param message The error message to display
     */
    public static void showError(String title, String message) {
        logger.severe("Error: " + title + " - " + message);
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText("Error");
        alert.setContentText(message);
        styleAlert(alert);
        alert.showAndWait();
    }
    
    /**
     * Display a warning message dialog
     * 
     * @param title The dialog title
     * @param message The warning message to display
     */
    public static void showWarning(String title, String message) {
        logger.warning("Warning: " + title + " - " + message);
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText("Warning");
        alert.setContentText(message);
        styleAlert(alert);
        alert.showAndWait();
    }
    
    /**
     * Display an information message dialog
     * 
     * @param title The dialog title
     * @param message The information message to display
     */
    public static void showInfo(String title, String message) {
        logger.info("Info: " + title + " - " + message);
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText("Information");
        alert.setContentText(message);
        styleAlert(alert);
        alert.showAndWait();
    }
    
    /**
     * Display a confirmation dialog and return the user's choice
     * 
     * @param title The dialog title
     * @param message The confirmation message to display
     * @return true if the user clicked OK, false if they clicked Cancel
     */
    public static boolean showConfirmation(String title, String message) {
        logger.info("Confirmation requested: " + title + " - " + message);
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText("Confirmation");
        alert.setContentText(message);
        styleAlert(alert);
        
        Optional<ButtonType> result = alert.showAndWait();
        boolean confirmed = result.isPresent() && result.get() == ButtonType.OK;
        logger.info("Confirmation result: " + confirmed);
        return confirmed;
    }
    
    /**
     * Display a custom confirmation dialog with custom button text
     * 
     * @param title The dialog title
     * @param message The confirmation message to display
     * @param confirmText The text for the confirm button
     * @param cancelText The text for the cancel button
     * @return true if the user clicked the confirm button, false otherwise
     */
    public static boolean showCustomConfirmation(String title, String message, 
                                                  String confirmText, String cancelText) {
        logger.info("Custom confirmation requested: " + title);
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText("Confirmation");
        alert.setContentText(message);
        
        ButtonType confirmButton = new ButtonType(confirmText);
        ButtonType cancelButton = new ButtonType(cancelText);
        alert.getButtonTypes().setAll(confirmButton, cancelButton);
        
        styleAlert(alert);
        
        Optional<ButtonType> result = alert.showAndWait();
        boolean confirmed = result.isPresent() && result.get() == confirmButton;
        logger.info("Custom confirmation result: " + confirmed);
        return confirmed;
    }
    
    /**
     * Apply consistent styling to alerts
     * 
     * @param alert The alert to style
     */
    private static void styleAlert(Alert alert) {
        // Apply CSS styling if available
        try {
            String cssPath = AlertHelper.class.getResource("/app/resources/css/cafeteria-theme.css").toExternalForm();
            alert.getDialogPane().getStylesheets().add(cssPath);
        } catch (Exception e) {
            // CSS not available yet, use default styling
            logger.fine("CSS theme not available for alert styling");
        }
        
        // Set minimum width for better readability
        alert.getDialogPane().setMinWidth(400);
    }
}
