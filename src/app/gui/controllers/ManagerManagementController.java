package app.gui.controllers;

import Core.Admin;
import Enums.ManagerType;
import ServiceManagers.*;
import Services.AdminManagement_Services;
import app.gui.shared.AlertHelper;
import app.gui.NavigationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import terminal.ServiceContainer;

import java.util.List;
import java.util.logging.Logger;

/**
 * Controller for the manager management screen.
 * Allows admins to add, update, delete, and view service managers.
 */
public class ManagerManagementController {
    private static final Logger logger = Logger.getLogger(ManagerManagementController.class.getName());
    
    @FXML
    private ComboBox<ManagerType> managerTypeComboBox;
    
    @FXML
    private TableView<ServicesManager> managersTableView;
    
    @FXML
    private TableColumn<ServicesManager, Integer> idColumn;
    
    @FXML
    private TableColumn<ServicesManager, String> nameColumn;
    
    @FXML
    private TableColumn<ServicesManager, String> phoneColumn;
    
    @FXML
    private TableColumn<ServicesManager, String> usernameColumn;
    
    @FXML
    private Button addManagerButton;
    
    @FXML
    private Button updateManagerButton;
    
    @FXML
    private Button deleteManagerButton;
    
    @FXML
    private Button viewStatisticsButton;
    
    @FXML
    private VBox formPanel;
    
    @FXML
    private Label formTitleLabel;
    
    @FXML
    private TextField nameField;
    
    @FXML
    private TextField phoneField;
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Button saveButton;
    
    @FXML
    private Button cancelButton;
    
    @FXML
    private Button backButton;
    
    private ServiceContainer services;
    private AdminManagement_Services adminManagementServices;
    private Admin currentAdmin;
    private boolean isEditMode = false;
    private String originalUsername = null;
    
    /**
     * Initialize the controller.
     * Called automatically by JavaFX after FXML loading.
     */
    @FXML
    public void initialize() {
        services = NavigationService.getServiceContainer();
        adminManagementServices = services.getAdminManagementServices();
        
        // Populate ManagerType ComboBox
        managerTypeComboBox.setItems(FXCollections.observableArrayList(ManagerType.values()));
        
        // Configure table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        
        logger.info("ManagerManagementController initialized.");
    }
    
    /**
     * Set the current admin.
     * Called by NavigationService when navigating to this screen with admin data.
     * 
     * @param admin The logged-in admin
     */
    public void setAdmin(Admin admin) {
        this.currentAdmin = admin;
        logger.info("Manager management screen loaded for admin: " + admin.getName());
    }
    
    /**
     * Handle manager type selection.
     * Loads managers of the selected type into the table.
     */
    @FXML
    private void handleTypeSelection() {
        ManagerType selectedType = managerTypeComboBox.getValue();
        if (selectedType == null) {
            return;
        }
        
        try {
            logger.info("Loading managers of type: " + selectedType);
            List<? extends ServicesManager> managers = adminManagementServices.getAllManagers(selectedType);
            
            // Convert to ObservableList for TableView
            ObservableList<ServicesManager> managerList = FXCollections.observableArrayList(managers);
            managersTableView.setItems(managerList);
            
            logger.info("Loaded " + managers.size() + " managers of type " + selectedType);
        } catch (Exception e) {
            logger.severe("Error loading managers: " + e.getMessage());
            AlertHelper.showError("Error", "Failed to load managers: " + e.getMessage());
        }
    }
    
    /**
     * Handle Add Manager button click.
     * Shows the form panel for adding a new manager.
     */
    @FXML
    private void handleAddManager() {
        ManagerType selectedType = managerTypeComboBox.getValue();
        if (selectedType == null) {
            AlertHelper.showWarning("No Type Selected", "Please select a manager type first.");
            return;
        }
        
        isEditMode = false;
        originalUsername = null;
        formTitleLabel.setText("Add New " + selectedType.name() + " Manager");
        clearForm();
        showFormPanel(true);
        logger.info("Add manager form opened for type: " + selectedType);
    }
    
    /**
     * Handle Update Manager button click.
     * Shows the form panel with selected manager data for editing.
     */
    @FXML
    private void handleUpdateManager() {
        ServicesManager selectedManager = managersTableView.getSelectionModel().getSelectedItem();
        if (selectedManager == null) {
            AlertHelper.showWarning("No Manager Selected", "Please select a manager to update.");
            return;
        }
        
        ManagerType selectedType = managerTypeComboBox.getValue();
        if (selectedType == null) {
            AlertHelper.showWarning("No Type Selected", "Please select a manager type first.");
            return;
        }
        
        isEditMode = true;
        originalUsername = selectedManager.getUsername();
        formTitleLabel.setText("Update " + selectedType.name() + " Manager");
        
        // Populate form with selected manager data
        nameField.setText(selectedManager.getName());
        phoneField.setText(selectedManager.getPhoneNumber());
        usernameField.setText(selectedManager.getUsername());
        passwordField.setText(selectedManager.getPassword());
        
        showFormPanel(true);
        logger.info("Update manager form opened for: " + selectedManager.getUsername());
    }
    
    /**
     * Handle Delete Manager button click.
     * Shows confirmation dialog and deletes the selected manager.
     */
    @FXML
    private void handleDeleteManager() {
        ServicesManager selectedManager = managersTableView.getSelectionModel().getSelectedItem();
        if (selectedManager == null) {
            AlertHelper.showWarning("No Manager Selected", "Please select a manager to delete.");
            return;
        }
        
        ManagerType selectedType = managerTypeComboBox.getValue();
        if (selectedType == null) {
            AlertHelper.showWarning("No Type Selected", "Please select a manager type first.");
            return;
        }
        
        // Show confirmation dialog
        boolean confirmed = AlertHelper.showConfirmation(
            "Confirm Deletion",
            "Are you sure you want to delete manager '" + selectedManager.getName() + "'?\n" +
            "Username: " + selectedManager.getUsername() + "\n\n" +
            "This action cannot be undone."
        );
        
        if (!confirmed) {
            logger.info("Manager deletion cancelled by user.");
            return;
        }
        
        try {
            boolean success = adminManagementServices.deleteManager(selectedType, selectedManager.getUsername());
            if (success) {
                AlertHelper.showSuccess("Success", "Manager deleted successfully.");
                logger.info("Manager deleted: " + selectedManager.getUsername());
                handleTypeSelection(); // Refresh the table
            } else {
                AlertHelper.showError("Error", "Failed to delete manager.");
                logger.warning("Failed to delete manager: " + selectedManager.getUsername());
            }
        } catch (Exception e) {
            logger.severe("Error deleting manager: " + e.getMessage());
            AlertHelper.showError("Error", "Failed to delete manager: " + e.getMessage());
        }
    }
    
    /**
     * Handle View Statistics button click.
     * Displays manager counts by type in a dialog.
     */
    @FXML
    private void handleViewStatistics() {
        try {
            StringBuilder stats = new StringBuilder();
            stats.append("Service Manager Statistics\n");
            stats.append("═══════════════════════════\n\n");
            
            int totalCount = 0;
            for (ManagerType type : ManagerType.values()) {
                int count = adminManagementServices.getManagerCount(type);
                stats.append(String.format("%-15s: %d\n", type.name(), count));
                totalCount += count;
            }
            
            stats.append("\n═══════════════════════════\n");
            stats.append(String.format("%-15s: %d", "TOTAL", totalCount));
            
            AlertHelper.showInfo("Manager Statistics", stats.toString());
            logger.info("Manager statistics displayed.");
        } catch (Exception e) {
            logger.severe("Error loading statistics: " + e.getMessage());
            AlertHelper.showError("Error", "Failed to load statistics: " + e.getMessage());
        }
    }
    
    /**
     * Handle Save button click.
     * Validates form and calls addManager() or updateManager().
     */
    @FXML
    private void handleSave() {
        ManagerType selectedType = managerTypeComboBox.getValue();
        if (selectedType == null) {
            AlertHelper.showWarning("No Type Selected", "Please select a manager type first.");
            return;
        }
        
        // Validate form inputs
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        
        if (name.isEmpty() || phone.isEmpty() || username.isEmpty() || password.isEmpty()) {
            AlertHelper.showError("Validation Error", "All fields are required.");
            return;
        }
        
        try {
            ServicesManager manager = createManagerInstance(selectedType, name, phone, username, password);
            
            if (isEditMode) {
                // Update existing manager
                boolean success = adminManagementServices.updateManager(selectedType, originalUsername, manager);
                if (success) {
                    AlertHelper.showSuccess("Success", "Manager updated successfully.");
                    logger.info("Manager updated: " + username);
                    showFormPanel(false);
                    handleTypeSelection(); // Refresh the table
                } else {
                    AlertHelper.showError("Error", "Failed to update manager.");
                    logger.warning("Failed to update manager: " + username);
                }
            } else {
                // Add new manager
                // Check if username already exists
                if (adminManagementServices.managerExists(selectedType, username)) {
                    AlertHelper.showError("Validation Error", "A manager with username '" + username + "' already exists.");
                    return;
                }
                
                boolean success = adminManagementServices.addManager(selectedType, manager);
                if (success) {
                    AlertHelper.showSuccess("Success", "Manager added successfully.");
                    logger.info("Manager added: " + username);
                    showFormPanel(false);
                    handleTypeSelection(); // Refresh the table
                } else {
                    AlertHelper.showError("Error", "Failed to add manager.");
                    logger.warning("Failed to add manager: " + username);
                }
            }
        } catch (Exception e) {
            logger.severe("Error saving manager: " + e.getMessage());
            AlertHelper.showError("Error", "Failed to save manager: " + e.getMessage());
        }
    }
    
    /**
     * Handle Cancel button click.
     * Hides the form panel without saving.
     */
    @FXML
    private void handleCancel() {
        showFormPanel(false);
        clearForm();
        logger.info("Form cancelled.");
    }
    
    /**
     * Handle Back button click.
     * Returns to the admin dashboard.
     */
    @FXML
    private void handleBack() {
        logger.info("Returning to admin dashboard...");
        NavigationService.navigateToWithData("admin-dashboard", currentAdmin);
    }
    
    /**
     * Show or hide the form panel.
     * 
     * @param show True to show, false to hide
     */
    private void showFormPanel(boolean show) {
        formPanel.setVisible(show);
        formPanel.setManaged(show);
    }
    
    /**
     * Clear all form fields.
     */
    private void clearForm() {
        nameField.clear();
        phoneField.clear();
        usernameField.clear();
        passwordField.clear();
    }
    
    /**
     * Create a manager instance based on the type.
     * 
     * @param type Manager type
     * @param name Manager name
     * @param phone Manager phone number
     * @param username Manager username
     * @param password Manager password
     * @return ServicesManager instance
     */
    private ServicesManager createManagerInstance(ManagerType type, String name, String phone, 
                                                   String username, String password) {
        switch (type) {
            case STUDENT:
                return new StudentManagement(name, phone, username, password);
            case MENU:
                return new MenuManagement(name, phone, username, password);
            case ORDER:
                return new OrderManagement(name, phone, username, password);
            case PAYMENT:
                return new PaymentService_Manager(name, phone, username, password);
            case REPORT:
                return new ReportService_Manager(name, phone, username, password);
            case NOTIFICATION:
                return new NotifcationService_Manager(name, phone, username, password);
            default:
                throw new IllegalArgumentException("Unsupported manager type: " + type);
        }
    }
}
