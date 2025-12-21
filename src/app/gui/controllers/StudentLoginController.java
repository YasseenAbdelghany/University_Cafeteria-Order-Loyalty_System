package app.gui.controllers;

import app.gui.shared.AlertHelper;
import app.gui.NavigationService;
import Core.Student;
import Services.StudentManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import terminal.ServiceContainer;

import java.util.logging.Logger;

/**
 * Controller for the student login and registration screen.
 * Handles student authentication and account creation.
 */
public class StudentLoginController {
    private static final Logger logger = Logger.getLogger(StudentLoginController.class.getName());
    
    @FXML private TabPane tabPane;
    
    // Login tab fields
    @FXML private TextField loginStudentCodeField;
    @FXML private Button loginButton;
    
    // Register tab fields
    @FXML private TextField registerNameField;
    @FXML private TextField registerPhoneField;
    @FXML private Button registerButton;
    
    // Navigation
    @FXML private Button backButton;
    
    private ServiceContainer services;
    private StudentManager studentManager;
    
    /**
     * Initialize the controller after FXML loading
     */
    @FXML
    public void initialize() {
        logger.info("StudentLoginController initialized");
        services = NavigationService.getServiceContainer();
        studentManager = services.getStudentManager();
    }
    
    /**
     * Handle login button click
     * Validates student code and authenticates the student
     */
    @FXML
    private void handleLogin() {
        logger.info("Login attempt initiated");
        
        String studentCode = loginStudentCodeField.getText();
        
        // Validate input
        if (studentCode == null || studentCode.trim().isEmpty()) {
            AlertHelper.showError("Validation Error", "Student code cannot be empty");
            return;
        }
        
        try {
            // Attempt login
            Student student = studentManager.login(studentCode.trim());
            
            if (student == null) {
                AlertHelper.showError("Login Failed", 
                    "Invalid student code. Please check your code and try again.");
                logger.warning("Login failed for code: " + studentCode);
                return;
            }
            
            // Login successful
            logger.info("Login successful for student: " + student.getCode());
            AlertHelper.showSuccess("Login Successful", 
                "Welcome back, " + student.getName() + "!");
            
            // Navigate to student dashboard
            NavigationService.navigateToWithData("student-dashboard", student);
            
        } catch (Exception e) {
            logger.severe("Login error: " + e.getMessage());
            AlertHelper.showError("Login Error", 
                "An error occurred during login: " + e.getMessage());
        }
    }
    
    /**
     * Handle register button click
     * Validates inputs and creates a new student account
     */
    @FXML
    private void handleRegister() {
        logger.info("Registration attempt initiated");
        
        String name = registerNameField.getText();
        String phoneNumber = registerPhoneField.getText();
        
        // Validate inputs
        if (name == null || name.trim().isEmpty()) {
            AlertHelper.showError("Validation Error", "Name cannot be empty");
            return;
        }
        
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            AlertHelper.showError("Validation Error", "Phone number cannot be empty");
            return;
        }
        
        // Basic phone number validation (optional but good practice)
        if (!phoneNumber.trim().matches("\\d{10,15}")) {
            AlertHelper.showError("Validation Error", 
                "Please enter a valid phone number (10-15 digits)");
            return;
        }
        
        try {
            // Attempt registration
            Student student = studentManager.register(name.trim(), phoneNumber.trim());
            
            if (student == null) {
                AlertHelper.showError("Registration Failed", 
                    "Failed to create student account. Please try again.");
                logger.warning("Registration failed for name: " + name);
                return;
            }
            
            // Registration successful
            logger.info("Registration successful for student: " + student.getCode());
            AlertHelper.showSuccess("Registration Successful", 
                "Welcome, " + student.getName() + "!\n\n" +
                "Your student code is: " + student.getCode() + "\n" +
                "Please save this code for future logins.");
            
            // Navigate to student dashboard
            NavigationService.navigateToWithData("student-dashboard", student);
            
        } catch (IllegalArgumentException e) {
            logger.warning("Registration validation error: " + e.getMessage());
            AlertHelper.showError("Validation Error", e.getMessage());
        } catch (Exception e) {
            logger.severe("Registration error: " + e.getMessage());
            AlertHelper.showError("Registration Error", 
                "An error occurred during registration: " + e.getMessage());
        }
    }
    
    /**
     * Handle back button click
     * Returns to the main menu
     */
    @FXML
    private void handleBack() {
        logger.info("Navigating back to main menu");
        NavigationService.navigateTo("main-menu");
    }
}
