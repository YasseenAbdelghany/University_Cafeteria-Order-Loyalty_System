package app.gui.student.controllers;

import Core.NotificationHistory;
import Core.Student;
import Services.NotificationHistoryService;
import app.gui.shared.AlertHelper;
import app.gui.student.StudentNavigationService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import terminal.ServiceContainer;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

/**
 * Controller for the notifications screen in the Student Application.
 * Displays student notifications with message type, date, message, and read status.
 */
public class NotificationsController {
    private static final Logger logger = Logger.getLogger(NotificationsController.class.getName());
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
    
    @FXML private ListView<NotificationHistory> notificationsListView;
    @FXML private Button markAllReadButton;
    @FXML private Button backButton;
    
    private Student currentStudent;
    private ServiceContainer services;
    private NotificationHistoryService notificationService;
    
    /**
     * Initialize the controller.
     * Called automatically by JavaFX after FXML loading.
     */
    @FXML
    public void initialize() {
        logger.info("Initializing NotificationsController (Student App)");
        services = StudentNavigationService.getServiceContainer();
        
        if (services != null) {
            notificationService = services.getNotificationHistoryService();
        }
        
        // Set up custom cell factory for notifications
        setupNotificationListView();
    }
    
    /**
     * Set the student data and load notifications.
     * This method is called by StudentNavigationService when navigating to this screen.
     * 
     * @param student The student object
     */
    public void setStudent(Student student) {
        logger.info("Setting student data: " + (student != null ? student.getStudentCode() : "null"));
        this.currentStudent = student;
        loadNotifications();
    }
    
    /**
     * Set up the ListView with custom cell rendering for notifications.
     */
    private void setupNotificationListView() {
        notificationsListView.setCellFactory(listView -> new ListCell<NotificationHistory>() {
            @Override
            protected void updateItem(NotificationHistory notification, boolean empty) {
                super.updateItem(notification, empty);
                
                if (empty || notification == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    setGraphic(createNotificationCell(notification));
                }
            }
        });
    }
    
    /**
     * Create a custom cell for displaying a notification.
     * 
     * @param notification The notification to display
     * @return VBox containing the notification details
     */
    private VBox createNotificationCell(NotificationHistory notification) {
        VBox cell = new VBox(5);
        cell.setPadding(new Insets(10));
        cell.getStyleClass().add("notification-cell");
        
        // Header with type and date
        HBox header = new HBox(10);
        
        // Message type badge
        Label typeLabel = new Label(getTypeIcon(notification.getMessageType()) + " " + notification.getMessageType());
        typeLabel.getStyleClass().add("notification-type");
        if (notification.getMessageType().equals("ORDER_PREPARING")) {
            typeLabel.getStyleClass().add("type-sale");
        } else if (notification.getMessageType().equals("ORDER_READY")) {
            typeLabel.getStyleClass().add("type-order-ready");
        } else if (notification.getMessageType().equals("SALE")) {
            typeLabel.getStyleClass().add("type-sale");
        } else {
            typeLabel.getStyleClass().add("type-general");
        }
        
        // Date label
        Label dateLabel = new Label(notification.getCreatedAt().format(DATE_FORMATTER));
        dateLabel.getStyleClass().add("notification-date");
        
        // Read status badge
        Label readStatusLabel = new Label(notification.isRead() ? "✓ Read" : "● Unread");
        readStatusLabel.getStyleClass().add(notification.isRead() ? "status-read" : "status-unread");
        
        header.getChildren().addAll(typeLabel, dateLabel, readStatusLabel);
        
        // Message content
        Label messageLabel = new Label(notification.getNotifyMessage());
        messageLabel.setWrapText(true);
        messageLabel.getStyleClass().add("notification-message");
        if (!notification.isRead()) {
            messageLabel.getStyleClass().add("message-unread");
        }
        
        cell.getChildren().addAll(header, messageLabel);
        
        // Apply different styling for unread notifications
        if (!notification.isRead()) {
            cell.getStyleClass().add("notification-unread");
        }
        
        return cell;
    }
    /*Complete 📋 Remaining Tasks (6):

TASK 3: Free Product Calculator

TASK 8: Free Product Display UI

TASK 9: Point Redemption Validation*/
    /**
     * Get an icon for the notification type.
     * 
     * @param messageType The message type
     * @return Icon string
     */
    private String getTypeIcon(String messageType) {
        switch (messageType) {
            case "ORDER_PREPARING":
                return "👨‍🍳";
            case "ORDER_READY":
                return "🍽️";
            case "SALE":
                return "🎉";
            case "GENERAL":
                return "📢";
            default:
                return "📬";
        }
    }
    
    /**
     * Load notifications for the current student.
     */
    private void loadNotifications() {
        if (currentStudent == null) {
            logger.warning("Cannot load notifications: student is null");
            return;
        }
        
        try {
            logger.info("Loading notifications for student: " + currentStudent.getStudentCode());
            
            if (notificationService == null) {
                logger.warning("NotificationHistoryService is not available");
                AlertHelper.showWarning("Service Unavailable", 
                    "Notification service is not available at this time.");
                return;
            }
            
            // Get all notifications for the student
            List<NotificationHistory> notifications = 
                notificationService.getNotificationHistoryForStudent(currentStudent.getStudentCode());
            
            // Clear and populate the list view
            notificationsListView.getItems().clear();
            
            if (notifications == null || notifications.isEmpty()) {
                logger.info("No notifications found for student");
                // Show a message in the list
                Label emptyLabel = new Label("📭 No notifications yet");
                emptyLabel.getStyleClass().add("empty-message");
                // We can't add a Label directly to ListView, so we'll just leave it empty
                // The user will see an empty list
            } else {
                notificationsListView.getItems().addAll(notifications);
                logger.info("Loaded " + notifications.size() + " notifications");
            }
            
        } catch (Exception e) {
            logger.severe("Error loading notifications: " + e.getMessage());
            e.printStackTrace();
            AlertHelper.showError("Error", "Failed to load notifications: " + e.getMessage());
        }
    }
    
    /**
     * Handle Mark All as Read button click.
     * Mark all notifications as read for the current student.
     */
    @FXML
    private void handleMarkAllRead() {
        logger.info("Mark All as Read button clicked");
        
        if (currentStudent == null) {
            AlertHelper.showError("Error", "Student data not available");
            return;
        }
        
        try {
            if (notificationService == null) {
                AlertHelper.showWarning("Service Unavailable", 
                    "Notification service is not available at this time.");
                return;
            }
            
            // Check if there are any unread notifications
            List<NotificationHistory> unreadNotifications = 
                notificationService.getUnreadNotificationsForStudent(currentStudent.getStudentCode());
            
            if (unreadNotifications == null || unreadNotifications.isEmpty()) {
                AlertHelper.showInfo("No Unread Notifications", 
                    "All notifications are already marked as read.");
                return;
            }
            
            // Mark all as read
            boolean success = notificationService.markAllNotificationsAsRead(currentStudent.getStudentCode());
            
            if (success) {
                logger.info("Successfully marked all notifications as read");
                AlertHelper.showSuccess("Success", 
                    "All notifications have been marked as read.");
                
                // Reload notifications to reflect the changes
                loadNotifications();
            } else {
                logger.warning("Failed to mark all notifications as read");
                AlertHelper.showError("Error", 
                    "Failed to mark notifications as read. Please try again.");
            }
            
        } catch (Exception e) {
            logger.severe("Error marking notifications as read: " + e.getMessage());
            e.printStackTrace();
            AlertHelper.showError("Error", 
                "Failed to mark notifications as read: " + e.getMessage());
        }
    }
    
    /**
     * Handle Back button click.
     * Return to the menu dashboard.
     */
    @FXML
    private void handleBack() {
        logger.info("Back button clicked");
        
        if (currentStudent == null) {
            logger.warning("Student data not available, navigating to student login");
            StudentNavigationService.navigateTo("student-login");
            return;
        }
        
        try {
            StudentNavigationService.navigateToWithData("menu-dashboard", currentStudent);
            logger.info("Navigated back to menu dashboard");
        } catch (Exception e) {
            logger.severe("Error navigating back: " + e.getMessage());
            AlertHelper.showError("Navigation Error", 
                "Failed to return to dashboard: " + e.getMessage());
        }
    }
}
