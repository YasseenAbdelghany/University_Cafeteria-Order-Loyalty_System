package app.gui.admin.controllers;

import Core.Student;
import Services.StudentManager;
import Services.LoyaltyProgramService;
import ServiceManagers.StudentManagement;
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
 * Controller for Student Manager Dashboard.
 * Allows student manager to view, register, update, delete students and send loyalty points.
 */
public class StudentManagerDashboardController {
    private static final Logger logger = Logger.getLogger(StudentManagerDashboardController.class.getName());
    
    @FXML private Label welcomeLabel;
    @FXML private TableView<Student> studentsTable;
    @FXML private TableColumn<Student, Integer> idColumn;
    @FXML private TableColumn<Student, String> nameColumn;
    @FXML private TableColumn<Student, String> emailColumn;
    @FXML private TableColumn<Student, String> phoneColumn;
    @FXML private TableColumn<Student, Integer> pointsColumn;
    
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField passwordField;
    @FXML private TextField pointsField;
    @FXML private TextField studentIdField;
    @FXML private TextField broadcastPointsField;
    
    private StudentManagement manager;
    private ServiceContainer services;
    private StudentManager studentManager;
    private LoyaltyProgramService loyaltyService;
    private ObservableList<Student> studentsList;
    
    @FXML
    public void initialize() {
        services = AdminNavigationService.getServiceContainer();
        studentManager = services.getStudentManager();
        loyaltyService = services.getLoyaltyService();
        
        // Setup table columns
        idColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        nameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        emailColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStudentCode())); // Use student code instead of email
        phoneColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPhoneNumber()));
        pointsColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getAccount().balance()).asObject());
        
        studentsList = FXCollections.observableArrayList();
        studentsTable.setItems(studentsList);
        
        // Load students on selection
        studentsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateFields(newSelection);
            }
        });
        
        logger.info("StudentManagerDashboardController initialized");
    }
    
    public void setManagerData(StudentManagement manager) {
        this.manager = manager;
        welcomeLabel.setText("Welcome, " + manager.getName() + " - Student Manager");
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
    
    private void populateFields(Student student) {
        studentIdField.setText(String.valueOf(student.getId()));
        nameField.setText(student.getName());
        emailField.setText(student.getStudentCode());
        phoneField.setText(student.getPhoneNumber());
        passwordField.setText(""); // No password field in Student
        pointsField.setText(String.valueOf(student.getAccount().balance()));
    }
    
    @FXML
    private void handleRegisterStudent() {
        try {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            
            if (name.isEmpty()) {
                AlertHelper.showError("Validation Error", "Name is required");
                return;
            }
            
            Student student = studentManager.register(name, phone);
            if (student != null) {
                AlertHelper.showSuccess("Success", "Student registered successfully! Code: " + student.getStudentCode());
                clearFields();
                loadStudents();
            } else {
                AlertHelper.showError("Registration Failed", "Failed to register student");
            }
        } catch (Exception e) {
            logger.severe("Error registering student: " + e.getMessage());
            AlertHelper.showError("Error", "Failed to register student: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleUpdateStudent() {
        try {
            String studentCode = emailField.getText().trim(); // Using email field for student code
            if (studentCode.isEmpty()) {
                AlertHelper.showError("Validation Error", "Please select a student to update");
                return;
            }
            
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            
            if (name.isEmpty()) {
                AlertHelper.showError("Validation Error", "Name is required");
                return;
            }
            
            Student student = studentManager.findByCode(studentCode);
            if (student != null) {
                student.setName(name);
                student.setPhoneNumber(phone);
                
                studentManager.update(student);
                AlertHelper.showSuccess("Success", "Student updated successfully!");
                clearFields();
                loadStudents();
            } else {
                AlertHelper.showError("Update Failed", "Student not found");
            }
        } catch (Exception e) {
            logger.severe("Error updating student: " + e.getMessage());
            AlertHelper.showError("Error", "Failed to update student: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleDeleteStudent() {
        try {
            String studentCode = emailField.getText().trim(); // Using email field for student code
            if (studentCode.isEmpty()) {
                AlertHelper.showError("Validation Error", "Please select a student to delete");
                return;
            }
            
            boolean confirmed = AlertHelper.showConfirmation("Confirm Delete", 
                "Are you sure you want to delete this student?");
            
            if (confirmed) {
                boolean success = studentManager.delete(studentCode);
                if (success) {
                    AlertHelper.showSuccess("Success", "Student deleted successfully!");
                    clearFields();
                    loadStudents();
                } else {
                    AlertHelper.showError("Delete Failed", "Failed to delete student");
                }
            }
        } catch (Exception e) {
            logger.severe("Error deleting student: " + e.getMessage());
            AlertHelper.showError("Error", "Failed to delete student: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleSendPoints() {
        try {
            String studentCode = emailField.getText().trim(); // Using email field for student code
            String pointsText = pointsField.getText().trim();
            
            if (studentCode.isEmpty() || pointsText.isEmpty()) {
                AlertHelper.showError("Validation Error", "Please select a student and enter points");
                return;
            }
            
            int points = Integer.parseInt(pointsText);
            
            if (points <= 0) {
                AlertHelper.showError("Validation Error", "Points must be positive");
                return;
            }
            
            Student student = studentManager.findByCode(studentCode);
            if (student != null) {
                loyaltyService.addPoints(student, points);
                
                // Send reward notification
                sendRewardNotification(studentCode, points);
                
                AlertHelper.showSuccess("Success", "Points added successfully!");
                clearFields();
                loadStudents();
            } else {
                AlertHelper.showError("Error", "Student not found");
            }
        } catch (Exception e) {
            logger.severe("Error sending points: " + e.getMessage());
            AlertHelper.showError("Error", "Failed to send points: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleBroadcastPoints() {
        try {
            String pointsText = broadcastPointsField.getText().trim();
            
            if (pointsText.isEmpty()) {
                AlertHelper.showError("Validation Error", "Please enter points to broadcast");
                return;
            }
            
            int points = Integer.parseInt(pointsText);
            
            if (points <= 0) {
                AlertHelper.showError("Validation Error", "Points must be positive");
                return;
            }
            
            boolean confirmed = AlertHelper.showConfirmation("Confirm Broadcast", 
                "Send " + points + " points to ALL students?");
            
            if (confirmed) {
                List<Student> students = studentManager.listAll();
                for (Student student : students) {
                    loyaltyService.addPoints(student, points);
                    
                    // Send reward notification to each student
                    sendRewardNotification(student.getStudentCode(), points);
                }
                AlertHelper.showSuccess("Success", "Points broadcasted to " + students.size() + " students!");
                broadcastPointsField.clear();
                loadStudents();
            }
        } catch (Exception e) {
            logger.severe("Error broadcasting points: " + e.getMessage());
            AlertHelper.showError("Error", "Failed to broadcast points: " + e.getMessage());
        }
    }
    
    /**
     * Send a reward notification to student when bonus points are awarded
     */
    private void sendRewardNotification(String studentCode, int points) {
        try {
            Services.NotificationHistoryService notificationService = services.getNotificationHistoryService();
            if (notificationService != null) {
                String message = String.format("🎁 Surprise! You've received %d bonus loyalty points! 🎁", points);
                
                notificationService.sendNotification(
                    studentCode,
                    message,
                    "GENERAL" // Using GENERAL type for reward notifications
                );
                
                logger.info("Sent reward notification for " + points + " points to student: " + studentCode);
            }
        } catch (Exception e) {
            logger.warning("Failed to send reward notification: " + e.getMessage());
            // Don't fail the point award if notification fails
        }
    }
    
    @FXML
    private void handleClear() {
        clearFields();
    }
    
    private void clearFields() {
        studentIdField.clear();
        nameField.clear();
        emailField.clear();
        phoneField.clear();
        passwordField.clear();
        pointsField.clear();
        studentsTable.getSelectionModel().clearSelection();
    }
    
    @FXML
    private void handleLogout() {
        logger.info("Student Manager logging out");
        AdminNavigationService.navigateTo("unified-login");
    }
}
