package app.gui.student.controllers;

import app.gui.shared.AlertHelper;
import app.gui.student.StudentNavigationService;
import Core.Student;
import Services.StudentManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import terminal.ServiceContainer;

import java.util.logging.Logger;

/**
 * Controller for the student login and registration screen in the Student Portal.
 * Handles student authentication and account creation, navigating to menu-dashboard on success.
 */
public class StudentLoginController {
    private static final Logger logger = Logger.getLogger(StudentLoginController.class.getName());
    
    // Login card and fields
    @FXML private javafx.scene.layout.VBox loginCard;
    @FXML private TextField loginStudentCodeField;
    @FXML private javafx.scene.control.PasswordField loginPasswordField;
    @FXML private Button loginButton;
    
    // Register card and fields
    @FXML private javafx.scene.layout.VBox registerCard;
    @FXML private TextField registerNameField;
    @FXML private TextField registerPhoneField;
    @FXML private javafx.scene.control.PasswordField registerPasswordField;
    @FXML private javafx.scene.control.PasswordField registerConfirmPasswordField;
    @FXML private Button registerButton;
    
    private ServiceContainer services;
    private StudentManager studentManager;
    
    /**
     * Initialize the controller after FXML loading
     */
    @FXML
    public void initialize() {
        logger.info("StudentLoginController initialized");
        services = StudentNavigationService.getServiceContainer();
        studentManager = services.getStudentManager();
        
        // Clear login fields on initialization (for logout)
        if (loginStudentCodeField != null) {
            loginStudentCodeField.clear();
            loginStudentCodeField.setOnAction(event -> handleLogin());
        }
        if (loginPasswordField != null) {
            loginPasswordField.clear();
            loginPasswordField.setOnAction(event -> handleLogin());
        }
    }
    
    /**
     * Show register card and hide login card
     */
    @FXML
    private void handleShowRegister() {
        if (loginCard != null) {
            loginCard.setVisible(false);
            loginCard.setManaged(false);
        }
        if (registerCard != null) {
            registerCard.setVisible(true);
            registerCard.setManaged(true);
        }
    }
    
    /**
     * Hide register card and show login card
     */
    @FXML
    private void handleBackToLogin() {
        if (registerCard != null) {
            registerCard.setVisible(false);
            registerCard.setManaged(false);
            // Clear register fields
            if (registerNameField != null) registerNameField.clear();
            if (registerPhoneField != null) registerPhoneField.clear();
            if (registerPasswordField != null) registerPasswordField.clear();
            if (registerConfirmPasswordField != null) registerConfirmPasswordField.clear();
        }
        if (loginCard != null) {
            loginCard.setVisible(true);
            loginCard.setManaged(true);
        }
    }
    
    /**
     * Handle login button click
     * Validates student code and authenticates the student
     */
    @FXML
    private void handleLogin() {
        logger.info("Login attempt initiated");
        
        String studentCode = loginStudentCodeField.getText();
        String password = loginPasswordField.getText();
        
        // Validate inputs
        if (studentCode == null || studentCode.trim().isEmpty()) {
            AlertHelper.showError("Validation Error", "Student code cannot be empty");
            return;
        }
        
        if (password == null || password.trim().isEmpty()) {
            AlertHelper.showError("Validation Error", "Password cannot be empty");
            return;
        }
        
        try {
            // Attempt login with password authentication
            Student student = services.getStudentDAO().authenticateStudent(studentCode.trim(), password.trim());
            
            if (student == null) {
                AlertHelper.showError("Login Failed", 
                    "Invalid student code or password. Please check your credentials and try again.");
                logger.warning("Login failed for code: " + studentCode);
                return;
            }
            
            // Login successful
            logger.info("Login successful for student: " + student.getCode());
//            AlertHelper.showSuccess("Login Successful",
//                "Welcome back, " + student.getName() + "!");

            // Navigate to menu-dashboard
            StudentNavigationService.navigateToWithData("menu-dashboard", student);
            
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
        String password = registerPasswordField.getText();
        String confirmPassword = registerConfirmPasswordField.getText();
        
        // Validate inputs
        if (name == null || name.trim().isEmpty()) {
            AlertHelper.showError("Validation Error", "Name cannot be empty");
            return;
        }
        
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            AlertHelper.showError("Validation Error", "Phone number cannot be empty");
            return;
        }
        
        if (password == null || password.trim().isEmpty()) {
            AlertHelper.showError("Validation Error", "Password cannot be empty");
            return;
        }
        
        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            AlertHelper.showError("Validation Error", "Please confirm your password");
            return;
        }
        
        // Validate password match
        if (!password.equals(confirmPassword)) {
            AlertHelper.showError("Validation Error", "Passwords do not match. Please try again.");
            return;
        }
        
        // Validate password length
        if (password.length() < 4) {
            AlertHelper.showError("Validation Error", "Password must be at least 4 characters long");
            return;
        }
        
        // Basic phone number validation (optional but good practice)
        if (!phoneNumber.trim().matches("\\d{10,15}")) {
            AlertHelper.showError("Validation Error", 
                "Please enter a valid phone number (10-15 digits)");
            return;
        }
        
        try {
            // Create student object with password
            Student student = new Student();
            student.setName(name.trim());
            student.setPhoneNumber(phoneNumber.trim());
            student.setPassword(password.trim());
            
            // Generate student code
            String studentCode = generateStudentCode();
            student.setCode(studentCode);
            
            // Save to database
            boolean saved = services.getStudentDAO().Save(student);
            
            if (!saved) {
                AlertHelper.showError("Registration Failed", 
                    "Failed to create student account. Please try again.");
                logger.warning("Registration failed for name: " + name);
                return;
            }
            
            // Registration successful - Show student code
            logger.info("Registration successful for student: " + student.getCode());
            
            AlertHelper.showInfo("Registration Successful", 
                "Your account has been created!\n\n" +
                "Your Student Code is: " + studentCode + "\n\n" +
                "Please save this code for future logins.");

            // Navigate to menu-dashboard with new registration flag
            navigateToMenuDashboardWithWelcome(student);

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
     * Generate a unique student code
     */
    private String generateStudentCode() {
        int count = services.getStudentDAO().countStudents();
        return String.format("ST%03d", count + 1);
    }

    /**
     * Navigate to menu dashboard with welcome banner for new registrations
     */
    private void navigateToMenuDashboardWithWelcome(Student student) {
        try {
            // First navigate to the menu dashboard
            StudentNavigationService.navigateToWithData("menu-dashboard", student);

            // Then get the controller and show the welcome banner
            Object controller = StudentNavigationService.getController("menu-dashboard");

            if (controller instanceof app.gui.student.controllers.MenuDashboardController) {
                app.gui.student.controllers.MenuDashboardController dashboardController =
                    (app.gui.student.controllers.MenuDashboardController) controller;
                dashboardController.setStudent(student, true);
                logger.info("Welcome banner triggered for new registration");
            }
        } catch (Exception e) {
            logger.warning("Could not show welcome banner: " + e.getMessage());
            // Fallback: just navigate normally
            StudentNavigationService.navigateToWithData("menu-dashboard", student);
        }
    }
}
