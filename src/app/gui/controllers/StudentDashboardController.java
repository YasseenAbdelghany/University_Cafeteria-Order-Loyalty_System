package app.gui.controllers;

import Core.Student;
import Services.NotificationHistoryService;
import app.gui.shared.AlertHelper;
import app.gui.NavigationService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import terminal.ServiceContainer;

import java.util.logging.Logger;

/**
 * Controller for the student dashboard screen.
 * Displays student information, loyalty points, notifications, and provides navigation to various student features.
 */
public class StudentDashboardController {
    private static final Logger logger = Logger.getLogger(StudentDashboardController.class.getName());
    
    @FXML private Label welcomeLabel;
    @FXML private Label studentCodeLabel;
    @FXML private Label loyaltyPointsLabel;
    @FXML private Label notificationBadgeLabel;
    @FXML private Button viewMenuButton;
    @FXML private Button checkPointsButton;
    @FXML private Button orderHistoryButton;
    @FXML private Button notificationsButton;
    @FXML private Button logoutButton;
    
    private Student currentStudent;
    private ServiceContainer services;
    private NotificationHistoryService notificationService;
    
    /**
     * Initialize the controller.
     * Called automatically by JavaFX after FXML loading.
     */
    @FXML
    public void initialize() {
        logger.info("Initializing StudentDashboardController");
        services = NavigationService.getServiceContainer();
        
        if (services != null) {
            notificationService = services.getNotificationHistoryService();
        }
    }
    
    /**
     * Set the student data and update the UI.
     * This method is called by NavigationService when navigating to this screen.
     * 
     * @param student The student object to display
     */
    public void setStudent(Student student) {
        logger.info("Setting student data: " + (student != null ? student.getStudentCode() : "null"));
        this.currentStudent = student;
        updateUI();
    }
    
    /**
     * Update the UI with current student data.
     */
    private void updateUI() {
        if (currentStudent == null) {
            logger.warning("Cannot update UI: student is null");
            return;
        }
        
        try {
            // Update welcome message
            welcomeLabel.setText("Welcome, " + currentStudent.getName());
            
            // Update student code
            studentCodeLabel.setText("Code: " + currentStudent.getStudentCode());
            
            // Update loyalty points
            int points = currentStudent.getAccount() != null ? currentStudent.getAccount().balance() : 0;
            loyaltyPointsLabel.setText(String.valueOf(points));
            
            // Update notification count
            loadNotificationCount();
            
            logger.info("UI updated successfully for student: " + currentStudent.getStudentCode());
            
        } catch (Exception e) {
            logger.severe("Error updating UI: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Load and display the unread notification count.
     */
    private void loadNotificationCount() {
        try {
            if (notificationService != null && currentStudent != null) {
                int unreadCount = notificationService.getUnreadNotificationCount(currentStudent.getStudentCode());
                notificationBadgeLabel.setText(String.valueOf(unreadCount));
                logger.info("Loaded notification count: " + unreadCount);
            } else {
                notificationBadgeLabel.setText("0");
            }
        } catch (Exception e) {
            logger.warning("Error loading notification count: " + e.getMessage());
            notificationBadgeLabel.setText("0");
        }
    }
    
    /**
     * Handle View Menu button click.
     * Navigate to the menu browse screen.
     */
    @FXML
    private void handleViewMenu() {
        logger.info("View Menu button clicked");
        
        if (currentStudent == null) {
            AlertHelper.showError("Error", "Student data not available");
            return;
        }
        
        try {
            NavigationService.navigateToWithData("menu-browse", currentStudent);
        } catch (Exception e) {
            logger.severe("Error navigating to menu browse: " + e.getMessage());
            AlertHelper.showError("Navigation Error", "Failed to open menu: " + e.getMessage());
        }
    }
    
    /**
     * Handle Check Points button click.
     * Display loyalty points in a dialog.
     */
    @FXML
    private void handleCheckPoints() {
        logger.info("Check Points button clicked");
        
        if (currentStudent == null) {
            AlertHelper.showError("Error", "Student data not available");
            return;
        }
        
        try {
            int points = currentStudent.getAccount() != null ? currentStudent.getAccount().balance() : 0;
            String message = String.format(
                "Student: %s\nStudent Code: %s\n\nCurrent Loyalty Points: %d\n\n" +
                "You can redeem your points when placing orders!",
                currentStudent.getName(),
                currentStudent.getStudentCode(),
                points
            );
            
            AlertHelper.showInfo("Loyalty Points", message);
            
        } catch (Exception e) {
            logger.severe("Error displaying loyalty points: " + e.getMessage());
            AlertHelper.showError("Error", "Failed to retrieve loyalty points: " + e.getMessage());
        }
    }
    
    /**
     * Handle Order History button click.
     * Navigate to the order history screen.
     */
    @FXML
    private void handleOrderHistory() {
        logger.info("Order History button clicked");
        
        if (currentStudent == null) {
            AlertHelper.showError("Error", "Student data not available");
            return;
        }
        
        try {
            NavigationService.navigateToWithData("order-history", currentStudent);
        } catch (Exception e) {
            logger.severe("Error navigating to order history: " + e.getMessage());
            AlertHelper.showError("Navigation Error", "Failed to open order history: " + e.getMessage());
        }
    }
    
    /**
     * Handle Notifications button click.
     * Navigate to the notifications screen.
     */
    @FXML
    private void handleNotifications() {
        logger.info("Notifications button clicked");
        
        if (currentStudent == null) {
            AlertHelper.showError("Error", "Student data not available");
            return;
        }
        
        try {
            NavigationService.navigateToWithData("notifications", currentStudent);
        } catch (Exception e) {
            logger.severe("Error navigating to notifications: " + e.getMessage());
            AlertHelper.showError("Navigation Error", "Failed to open notifications: " + e.getMessage());
        }
    }
    
    /**
     * Handle Logout button click.
     * Return to the main menu.
     */
    @FXML
    private void handleLogout() {
        logger.info("Logout button clicked");
        
        try {
            // Clear the current student
            currentStudent = null;
            
            // Navigate back to main menu
            NavigationService.navigateTo("main-menu");
            
            logger.info("Logged out successfully");
            
        } catch (Exception e) {
            logger.severe("Error during logout: " + e.getMessage());
            AlertHelper.showError("Logout Error", "Failed to logout: " + e.getMessage());
        }
    }
}
