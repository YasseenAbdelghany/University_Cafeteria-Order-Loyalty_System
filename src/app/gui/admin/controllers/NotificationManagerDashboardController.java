package app.gui.admin.controllers;

import Core.Student;
import Services.StudentManager;
import Services.NotificationHistoryService;
import ServiceManagers.NotifcationService_Manager;
import app.gui.shared.AlertHelper;
import app.gui.admin.AdminNavigationService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import terminal.ServiceContainer;

import java.util.List;
import java.util.logging.Logger;

/**
 * Controller for Notification Manager Dashboard.
 * Allows notification manager to send notifications to individual students or broadcast to all.
 */
public class NotificationManagerDashboardController {
    private static final Logger logger = Logger.getLogger(NotificationManagerDashboardController.class.getName());
    
    @FXML private Label welcomeLabel;
    @FXML private TableView<Student> studentsTable;
    @FXML private TableColumn<Student, Integer> idColumn;
    @FXML private TableColumn<Student, String> nameColumn;
    @FXML private TableColumn<Student, String> emailColumn;
    
    @FXML private TextField studentIdField;
    @FXML private TextField studentNameField;
    @FXML private TextArea messageArea;
    @FXML private TextArea broadcastMessageArea;
    
    private NotifcationService_Manager manager;
    private ServiceContainer services;
    private StudentManager studentManager;
    private NotificationHistoryService notificationHistoryService;
    private ObservableList<Student> studentsList;
    
    @FXML
    public void initialize() {
        services = AdminNavigationService.getServiceContainer();
        studentManager = services.getStudentManager();
        notificationHistoryService = services.getNotificationHistoryService();
        
        // Setup table columns
        idColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        nameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        emailColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStudentCode()));
        
        studentsList = FXCollections.observableArrayList();
        studentsTable.setItems(studentsList);
        
        // Load student details on selection
        studentsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                studentIdField.setText(String.valueOf(newSelection.getId()));
                studentNameField.setText(newSelection.getName());
            }
        });
        
        logger.info("NotificationManagerDashboardController initialized");
    }
    
    public void setManagerData(NotifcationService_Manager manager) {
        this.manager = manager;
        welcomeLabel.setText("Welcome, " + manager.getName() + " - Notification Manager");
        loadStudents();
    }
    
    @FXML
    private void handleLoadStudents() {
        loadStudents();
    }
    
    private void loadStudents() {
        try {
            List<Student> students = studentManager.listAll();
            studentsList.clear();
            studentsList.addAll(students);
            logger.info("Loaded " + students.size() + " students");
        } catch (Exception e) {
            logger.severe("Error loading students: " + e.getMessage());
            AlertHelper.showError("Load Error", "Failed to load students: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleSendNotification() {
        try {
            String idText = studentIdField.getText().trim();
            String message = messageArea.getText().trim();
            
            if (idText.isEmpty()) {
                AlertHelper.showError("Validation Error", "Please select a student");
                return;
            }
            
            if (message.isEmpty()) {
                AlertHelper.showError("Validation Error", "Please enter a message");
                return;
            }
            
            int studentId = Integer.parseInt(idText);
            
            // Find student by ID from the list
            Student student = studentsList.stream()
                .filter(s -> s.getId() == studentId)
                .findFirst()
                .orElse(null);
            
            if (student == null) {
                AlertHelper.showError("Error", "Student not found");
                return;
            }
            
            // Send notification using NotificationHistoryService (stores in notification_history table)
            boolean success = notificationHistoryService.sendGeneralNotification(student, message);
            
            if (success) {
                AlertHelper.showSuccess("Success", "Notification sent to " + student.getName());
                logger.info("Notification sent to student: " + student.getStudentCode());
                messageArea.clear();
                studentIdField.clear();
                studentNameField.clear();
                studentsTable.getSelectionModel().clearSelection();
            } else {
                AlertHelper.showError("Error", "Failed to send notification to database");
            }
            
        } catch (NumberFormatException e) {
            AlertHelper.showError("Validation Error", "Invalid student ID");
        } catch (Exception e) {
            logger.severe("Error sending notification: " + e.getMessage());
            e.printStackTrace();
            AlertHelper.showError("Error", "Failed to send notification: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleBroadcastNotification() {
        try {
            String message = broadcastMessageArea.getText().trim();
            
            if (message.isEmpty()) {
                AlertHelper.showError("Validation Error", "Please enter a broadcast message");
                return;
            }
            
            boolean confirmed = AlertHelper.showConfirmation("Confirm Broadcast", 
                "Send this notification to ALL students?");
            
            if (confirmed) {
                List<Student> students = studentManager.listAll();
                int successCount = 0;
                int failCount = 0;
                
                for (Student student : students) {
                    try {
                        // Send notification using NotificationHistoryService (stores in notification_history table)
                        boolean success = notificationHistoryService.sendGeneralNotification(student, message);
                        if (success) {
                            successCount++;
                        } else {
                            failCount++;
                        }
                    } catch (Exception e) {
                        failCount++;
                        logger.warning("Failed to send notification to student " + student.getStudentCode() + ": " + e.getMessage());
                    }
                }
                
                String resultMessage = "Broadcast sent to " + successCount + " students!";
                if (failCount > 0) {
                    resultMessage += "\n" + failCount + " failed.";
                }
                
                AlertHelper.showSuccess("Broadcast Complete", resultMessage);
                logger.info("Broadcast notification sent: " + successCount + " success, " + failCount + " failed");
                broadcastMessageArea.clear();
            }
            
        } catch (Exception e) {
            logger.severe("Error broadcasting notification: " + e.getMessage());
            e.printStackTrace();
            AlertHelper.showError("Error", "Failed to broadcast notification: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleClear() {
        messageArea.clear();
        studentIdField.clear();
        studentNameField.clear();
        studentsTable.getSelectionModel().clearSelection();
    }
    
    @FXML
    private void handleLogout() {
        logger.info("Notification Manager logging out");
        AdminNavigationService.navigateTo("unified-login");
    }
}
