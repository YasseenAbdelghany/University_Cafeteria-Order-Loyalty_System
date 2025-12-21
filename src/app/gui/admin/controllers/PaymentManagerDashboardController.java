package app.gui.admin.controllers;

import Interfaces.IPaymentProcessor;
import Services.PaymentRegistry;
import ServiceManagers.PaymentService_Manager;
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
 * Controller for Payment Manager Dashboard.
 * Allows payment manager to view, add, and remove payment methods.
 */
public class PaymentManagerDashboardController {
    private static final Logger logger = Logger.getLogger(PaymentManagerDashboardController.class.getName());
    
    @FXML private Label welcomeLabel;
    @FXML private ListView<String> paymentMethodsList;
    @FXML private TextField methodNameField;
    @FXML private TextArea methodDetailsArea;
    
    private PaymentService_Manager manager;
    private ServiceContainer services;
    private PaymentRegistry paymentRegistry;
    private ObservableList<String> methodsList;
    
    @FXML
    public void initialize() {
        services = AdminNavigationService.getServiceContainer();
        paymentRegistry = services.getPaymentRegistry();
        
        methodsList = FXCollections.observableArrayList();
        paymentMethodsList.setItems(methodsList);
        
        // Load method details on selection
        paymentMethodsList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                methodNameField.setText(newSelection);
                displayMethodDetails(newSelection);
            }
        });
        
        logger.info("PaymentManagerDashboardController initialized");
    }
    
    public void setManagerData(PaymentService_Manager manager) {
        this.manager = manager;
        welcomeLabel.setText("Welcome, " + manager.getName() + " - Payment Manager");
        loadPaymentMethods();
    }
    
    @FXML
    private void handleLoadMethods() {
        loadPaymentMethods();
    }
    
    private void loadPaymentMethods() {
        try {
            List<String> methods = paymentRegistry.listNames();
            methodsList.clear();
            methodsList.addAll(methods);
            logger.info("Loaded " + methods.size() + " payment methods");
        } catch (Exception e) {
            logger.severe("Error loading payment methods: " + e.getMessage());
            AlertHelper.showError("Load Error", "Failed to load payment methods: " + e.getMessage());
        }
    }
    
    private void displayMethodDetails(String methodName) {
        try {
            IPaymentProcessor method = paymentRegistry.get(methodName);
            if (method != null) {
                StringBuilder details = new StringBuilder();
                details.append("Payment Method: ").append(methodName).append("\n");
                details.append("Type: ").append(method.getClass().getSimpleName()).append("\n");
                details.append("\nDescription:\n");
                details.append("This payment method is available for student orders.\n");
                
                methodDetailsArea.setText(details.toString());
            }
        } catch (Exception e) {
            logger.warning("Error displaying method details: " + e.getMessage());
            methodDetailsArea.setText("Unable to load method details");
        }
    }
    
    @FXML
    private void handleAddMethod() {
        try {
            String methodName = methodNameField.getText().trim();
            
            if (methodName.isEmpty()) {
                AlertHelper.showError("Validation Error", "Please enter a method name");
                return;
            }
            
            // Check if method already exists
            if (methodsList.contains(methodName)) {
                AlertHelper.showError("Duplicate Error", "Payment method already exists");
                return;
            }
            
            // For now, we'll show a message that custom payment methods need to be implemented
            // In a real system, you'd create a new payment method class instance
            AlertHelper.showInfo("Add Payment Method", 
                "To add a new payment method, you need to:\n" +
                "1. Create a new class implementing IPaymentProcessor\n" +
                "2. Register it with the PaymentRegistry\n\n" +
                "This requires code-level changes.");
            
        } catch (Exception e) {
            logger.severe("Error adding payment method: " + e.getMessage());
            AlertHelper.showError("Error", "Failed to add payment method: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleRemoveMethod() {
        try {
            String methodName = paymentMethodsList.getSelectionModel().getSelectedItem();
            
            if (methodName == null) {
                AlertHelper.showError("Selection Error", "Please select a payment method to remove");
                return;
            }
            
            boolean confirmed = AlertHelper.showConfirmation("Confirm Remove", 
                "Are you sure you want to remove the payment method: " + methodName + "?");
            
            if (confirmed) {
                boolean success = paymentRegistry.remove(methodName);
                
                if (success) {
                    AlertHelper.showSuccess("Success", "Payment method removed successfully!");
                    methodNameField.clear();
                    methodDetailsArea.clear();
                    loadPaymentMethods();
                } else {
                    AlertHelper.showError("Remove Failed", "Failed to remove payment method");
                }
            }
            
        } catch (Exception e) {
            logger.severe("Error removing payment method: " + e.getMessage());
            AlertHelper.showError("Error", "Failed to remove payment method: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleClear() {
        methodNameField.clear();
        methodDetailsArea.clear();
        paymentMethodsList.getSelectionModel().clearSelection();
    }
    
    @FXML
    private void handleLogout() {
        logger.info("Payment Manager logging out");
        AdminNavigationService.navigateTo("unified-login");
    }
}
