package app.gui.admin.controllers;

import Core.Admin;
import Services.AdminManager;
import app.gui.shared.AlertHelper;
import app.gui.admin.AdminNavigationService;
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
 * Controller for the IT admin dashboard screen in the Administrative Application.
 * Handles administrator management operations (CRUD).
 */
public class ITAdminDashboardController {
    private static final Logger logger = Logger.getLogger(ITAdminDashboardController.class.getName());
    
    @FXML
    private Label welcomeLabel;
    
    @FXML
    private TableView<Admin> adminTable;
    
    @FXML
    private TableColumn<Admin, Integer> idColumn;
    
    @FXML
    private TableColumn<Admin, String> usernameColumn;
    
    @FXML
    private TableColumn<Admin, String> nameColumn;
    
    @FXML
    private Button addButton;
    
    @FXML
    private Button updateButton;
    
    @FXML
    private Button deleteButton;
    
    @FXML
    private Button logoutButton;
    
    @FXML
    private VBox formPanel;
    
    @FXML
    private Label formTitleLabel;
    
    @FXML
    private TextField usernameFormField;
    
    @FXML
    private TextField nameFormField;
    
    @FXML
    private PasswordField passwordFormField;
    
    @FXML
    private Button saveButton;
    
    @FXML
    private Button cancelButton;
    
    private ServiceContainer services;
    private AdminManager adminManager;
    private Admin currentAdmin;
    private boolean isEditMode = false;
    private Admin selectedAdminForEdit = null;
    
    private ObservableList<Admin> adminList;
    
    /**
     * Initialize the controller.
     * Called automatically by JavaFX after FXML loading.
     */
    @FXML
    public void initialize() {
        services = AdminNavigationService.getServiceContainer();
        adminManager = services.getAdminManager();
        
        // Setup table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        // Load administrators
        loadAdministrators();
        
        logger.info("ITAdminDashboardController initialized.");
    }
    
    /**
     * Set the current admin data.
     * Called by AdminNavigationService when navigating to this screen.
     */
    public void setData(Object data) {
        if (data instanceof Admin) {
            this.currentAdmin = (Admin) data;
            welcomeLabel.setText("Welcome, " + currentAdmin.getName());
            logger.info("IT Admin dashboard loaded for: " + currentAdmin.getName());
        }
    }
    
    /**
     * Load all administrators from the database and populate the table.
     */
    private void loadAdministrators() {
        try {
            List<Admin> admins = adminManager.getAdminQueryDAO().getAllAdmins();
            adminList = FXCollections.observableArrayList(admins);
            adminTable.setItems(adminList);
            logger.info("Loaded " + admins.size() + " administrators.");
        } catch (Exception e) {
            logger.severe("Error loading administrators: " + e.getMessage());
            e.printStackTrace();
            AlertHelper.showError("Load Error", "Failed to load administrators: " + e.getMessage());
        }
    }
    
    /**
     * Handle Add Administrator button click.
     * Shows the form panel for adding a new administrator.
     */
    @FXML
    private void handleAddAdmin() {
        logger.info("Add administrator initiated.");
        isEditMode = false;
        selectedAdminForEdit = null;
        formTitleLabel.setText("Add New Administrator");
        
        // Clear form fields
        usernameFormField.clear();
        nameFormField.clear();
        passwordFormField.clear();
        
        // Show form panel
        formPanel.setVisible(true);
        formPanel.setManaged(true);
        
        usernameFormField.requestFocus();
    }
    
    /**
     * Handle Update Administrator button click.
     * Shows the form panel with selected administrator data for editing.
     */
    @FXML
    private void handleUpdateAdmin() {
        Admin selectedAdmin = adminTable.getSelectionModel().getSelectedItem();
        
        if (selectedAdmin == null) {
            AlertHelper.showWarning("No Selection", "Please select an administrator to update.");
            return;
        }
        
        logger.info("Update administrator initiated for: " + selectedAdmin.getUsername());
        isEditMode = true;
        selectedAdminForEdit = selectedAdmin;
        formTitleLabel.setText("Update Administrator");
        
        // Populate form fields with selected admin data
        usernameFormField.setText(selectedAdmin.getUsername());
        nameFormField.setText(selectedAdmin.getName());
        passwordFormField.setText(selectedAdmin.getPassword());
        
        // Show form panel
        formPanel.setVisible(true);
        formPanel.setManaged(true);
        
        nameFormField.requestFocus();
    }
    
    /**
     * Handle Delete Administrator button click.
     * Shows confirmation dialog and deletes the selected administrator.
     */
    @FXML
    private void handleDeleteAdmin() {
        Admin selectedAdmin = adminTable.getSelectionModel().getSelectedItem();
        
        if (selectedAdmin == null) {
            AlertHelper.showWarning("No Selection", "Please select an administrator to delete.");
            return;
        }
        
        // Prevent deleting yourself
        if (currentAdmin != null && selectedAdmin.getUsername().equals(currentAdmin.getUsername())) {
            AlertHelper.showError("Cannot Delete", "You cannot delete your own administrator account.");
            return;
        }
        
        // Show confirmation dialog
        boolean confirmed = AlertHelper.showConfirmation(
            "Confirm Deletion",
            "Are you sure you want to delete administrator '" + selectedAdmin.getName() + "'?\n" +
            "This action cannot be undone."
        );
        
        if (!confirmed) {
            logger.info("Administrator deletion cancelled by user.");
            return;
        }
        
        try {
            logger.info("Deleting administrator: " + selectedAdmin.getUsername());
            adminManager.deleteAdmin(selectedAdmin);
            
            AlertHelper.showSuccess("Success", "Administrator deleted successfully.");
            
            // Reload table
            loadAdministrators();
            
        } catch (Exception e) {
            logger.severe("Error deleting administrator: " + e.getMessage());
            e.printStackTrace();
            AlertHelper.showError("Delete Error", "Failed to delete administrator: " + e.getMessage());
        }
    }
    
    /**
     * Handle Save button click.
     * Validates form and adds or updates administrator.
     */
    @FXML
    private void handleSave() {
        String username = usernameFormField.getText().trim();
        String name = nameFormField.getText().trim();
        String password = passwordFormField.getText().trim();
        
        // Validate inputs
        if (username.isEmpty()) {
            AlertHelper.showError("Validation Error", "Username cannot be empty.");
            usernameFormField.requestFocus();
            return;
        }
        
        if (name.isEmpty()) {
            AlertHelper.showError("Validation Error", "Name cannot be empty.");
            nameFormField.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            AlertHelper.showError("Validation Error", "Password cannot be empty.");
            passwordFormField.requestFocus();
            return;
        }
        
        try {
            if (isEditMode) {
                // Update existing administrator
                logger.info("Updating administrator: " + selectedAdminForEdit.getUsername());
                
                Admin updatedAdmin = new Admin(name, username, password);
                updatedAdmin.setId(selectedAdminForEdit.getId());
                
                adminManager.updateAdmin(selectedAdminForEdit, updatedAdmin);
                
                AlertHelper.showSuccess("Success", "Administrator updated successfully.");
                
            } else {
                // Add new administrator
                logger.info("Adding new administrator: " + username);
                
                Admin newAdmin = new Admin(name, username, password);
                boolean success = adminManager.insertAdmin(newAdmin);
                
                if (success) {
                    AlertHelper.showSuccess("Success", "Administrator added successfully.");
                } else {
                    AlertHelper.showError("Error", "Failed to add administrator. Username may already exist.");
                    return;
                }
            }
            
            // Hide form and reload table
            handleCancel();
            loadAdministrators();
            
        } catch (Exception e) {
            logger.severe("Error saving administrator: " + e.getMessage());
            e.printStackTrace();
            AlertHelper.showError("Save Error", "Failed to save administrator: " + e.getMessage());
        }
    }
    
    /**
     * Handle Cancel button click.
     * Hides the form panel without saving.
     */
    @FXML
    private void handleCancel() {
        logger.info("Form cancelled.");
        
        // Clear form fields
        usernameFormField.clear();
        nameFormField.clear();
        passwordFormField.clear();
        
        // Hide form panel
        formPanel.setVisible(false);
        formPanel.setManaged(false);
        
        isEditMode = false;
        selectedAdminForEdit = null;
    }
    
    /**
     * Handle Logout button click.
     * Returns to the unified login screen.
     */
    @FXML
    private void handleLogout() {
        logger.info("IT Admin logging out: " + (currentAdmin != null ? currentAdmin.getName() : "Unknown"));
        AdminNavigationService.navigateTo("unified-login");
    }
}
