package app.gui.admin.controllers;

import Core.MenuItem;
import Services.MenuManager;
import ServiceManagers.MenuManagement;
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
 * Controller for Menu Manager Dashboard.
 * Allows menu manager to view, add, update, and remove menu items.
 */
public class MenuManagerDashboardController {
    private static final Logger logger = Logger.getLogger(MenuManagerDashboardController.class.getName());
    
    @FXML private Label welcomeLabel;
    @FXML private TableView<MenuItem> menuTable;
    @FXML private TableColumn<MenuItem, Integer> idColumn;
    @FXML private TableColumn<MenuItem, String> nameColumn;
    @FXML private TableColumn<MenuItem, Double> priceColumn;
    @FXML private TableColumn<MenuItem, String> categoryColumn;
    @FXML private TableColumn<MenuItem, String> statusColumn;
    
    @FXML private TextField itemIdField;
    @FXML private TextField itemNameField;
    @FXML private TextField priceField;
    @FXML private TextField categoryField;
    @FXML private TextArea descriptionArea;
    @FXML private CheckBox availableCheckBox;
    
    private MenuManagement manager;
    private ServiceContainer services;
    private MenuManager menuManager;
    private ObservableList<MenuItem> menuList;
    
    @FXML
    public void initialize() {
        services = AdminNavigationService.getServiceContainer();
        menuManager = services.getMenuManager();
        
        // Setup table columns
        idColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        nameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        priceColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getPrice().getAmount().doubleValue()).asObject());
        categoryColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCategory() != null ? data.getValue().getCategory().toString() : "N/A"));
        statusColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().isActive() ? "Active" : "Inactive"));
        
        menuList = FXCollections.observableArrayList();
        menuTable.setItems(menuList);
        
        // Load item details on selection
        menuTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateFields(newSelection);
            }
        });
        
        logger.info("MenuManagerDashboardController initialized");
    }
    
    public void setManagerData(MenuManagement manager) {
        this.manager = manager;
        welcomeLabel.setText("Welcome, " + manager.getName() + " - Menu Manager");
        loadMenuItems();
    }
    
    @FXML
    private void handleLoadMenu() {
        loadMenuItems();
    }
    
    private void loadMenuItems() {
        try {
            // Load ALL items including inactive ones for admin view
            List<MenuItem> items = menuManager.getAllItems();
            menuList.clear();
            menuList.addAll(items);
            logger.info("Loaded " + items.size() + " menu items (including inactive)");
        } catch (Exception e) {
            logger.severe("Error loading menu items: " + e.getMessage());
            AlertHelper.showError("Load Error", "Failed to load menu: " + e.getMessage());
        }
    }
    
    private void populateFields(MenuItem item) {
        itemIdField.setText(String.valueOf(item.getId()));
        itemNameField.setText(item.getName());
        priceField.setText(String.valueOf(item.getPrice().getAmount().doubleValue()));
        categoryField.setText(item.getCategory() != null ? item.getCategory().toString() : "");
        descriptionArea.setText(item.getDescription());
        availableCheckBox.setSelected(item.isActive());
    }
    
    @FXML
    private void handleAddItem() {
        try {
            String name = itemNameField.getText().trim();
            String priceText = priceField.getText().trim();
            String categoryText = categoryField.getText().trim().toUpperCase();
            String description = descriptionArea.getText().trim();
            
            if (name.isEmpty() || priceText.isEmpty() || categoryText.isEmpty()) {
                AlertHelper.showError("Validation Error", "Name, price, and category are required");
                return;
            }
            
            double priceValue = Double.parseDouble(priceText);
            if (priceValue <= 0) {
                AlertHelper.showError("Validation Error", "Price must be positive");
                return;
            }
            
            // Parse category enum
            Enums.Category category;
            try {
                category = Enums.Category.valueOf(categoryText.replace(" ", "_"));
            } catch (IllegalArgumentException e) {
                AlertHelper.showError("Validation Error", "Invalid category. Use: MAIN_COURSE, SNACK, or DRINK");
                return;
            }
            
            MenuItem item = new MenuItem(name, description, new Values.Money(priceValue), category);
            menuManager.addMenuItem(item);
            
            AlertHelper.showSuccess("Success", "Menu item added successfully!");
            clearFields();
            loadMenuItems();
        } catch (NumberFormatException e) {
            AlertHelper.showError("Validation Error", "Invalid price format");
        } catch (Exception e) {
            logger.severe("Error adding menu item: " + e.getMessage());
            AlertHelper.showError("Error", "Failed to add item: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleUpdateItem() {
        try {
            String idText = itemIdField.getText().trim();
            if (idText.isEmpty()) {
                AlertHelper.showError("Validation Error", "Please select an item to update");
                return;
            }
            
            int id = Integer.parseInt(idText);
            String name = itemNameField.getText().trim();
            String priceText = priceField.getText().trim();
            String categoryText = categoryField.getText().trim().toUpperCase();
            String description = descriptionArea.getText().trim();
            
            if (name.isEmpty() || priceText.isEmpty() || categoryText.isEmpty()) {
                AlertHelper.showError("Validation Error", "Name, price, and category are required");
                return;
            }
            
            double priceValue = Double.parseDouble(priceText);
            if (priceValue <= 0) {
                AlertHelper.showError("Validation Error", "Price must be positive");
                return;
            }
            
            // Parse category enum
            Enums.Category category;
            try {
                category = Enums.Category.valueOf(categoryText.replace(" ", "_"));
            } catch (IllegalArgumentException e) {
                AlertHelper.showError("Validation Error", "Invalid category. Use: MAIN_COURSE, SNACK, or DRINK");
                return;
            }
            
            MenuItem item = new MenuItem(name, description, new Values.Money(priceValue), category);
            item.setId(id);
            menuManager.updateMenuItem(item);
            
            AlertHelper.showSuccess("Success", "Menu item updated successfully!");
            clearFields();
            loadMenuItems();
        } catch (NumberFormatException e) {
            AlertHelper.showError("Validation Error", "Invalid number format");
        } catch (Exception e) {
            logger.severe("Error updating menu item: " + e.getMessage());
            AlertHelper.showError("Error", "Failed to update item: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleRemoveItem() {
        try {
            String idText = itemIdField.getText().trim();
            if (idText.isEmpty()) {
                AlertHelper.showError("Validation Error", "Please select an item to remove");
                return;
            }
            
            int id = Integer.parseInt(idText);
            boolean confirmed = AlertHelper.showConfirmation("Confirm Remove", 
                "Are you sure you want to remove this menu item?");
            
            if (confirmed) {
                menuManager.removeMenuItem(id);
                AlertHelper.showSuccess("Success", "Menu item removed successfully!");
                clearFields();
                loadMenuItems();
            }
        } catch (Exception e) {
            logger.severe("Error removing menu item: " + e.getMessage());
            AlertHelper.showError("Error", "Failed to remove item: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleClear() {
        clearFields();
    }
    
    private void clearFields() {
        itemIdField.clear();
        itemNameField.clear();
        priceField.clear();
        categoryField.clear();
        descriptionArea.clear();
        availableCheckBox.setSelected(true);
        menuTable.getSelectionModel().clearSelection();
    }
    
    @FXML
    private void handleToggleActive() {
        try {
            String idText = itemIdField.getText().trim();
            if (idText.isEmpty()) {
                AlertHelper.showError("Validation Error", "Please select an item to toggle");
                return;
            }
            
            int id = Integer.parseInt(idText);
            MenuItem selectedItem = menuTable.getSelectionModel().getSelectedItem();
            
            if (selectedItem == null) {
                AlertHelper.showError("Validation Error", "Please select an item from the table");
                return;
            }
            
            boolean newStatus = !selectedItem.isActive();
            String action = newStatus ? "activate" : "deactivate";
            
            boolean confirmed = AlertHelper.showConfirmation("Confirm Toggle", 
                "Are you sure you want to " + action + " this menu item?\n\n" +
                (newStatus ? "Students will be able to see and order this item." : 
                            "Students will NOT be able to see or order this item."));
            
            if (confirmed) {
                menuManager.toggleActiveStatus(id, newStatus);
                AlertHelper.showSuccess("Success", "Menu item " + (newStatus ? "activated" : "deactivated") + " successfully!");
                loadMenuItems();
                clearFields();
            }
        } catch (Exception e) {
            logger.severe("Error toggling menu item status: " + e.getMessage());
            AlertHelper.showError("Error", "Failed to toggle item status: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleLogout() {
        logger.info("Menu Manager logging out");
        AdminNavigationService.navigateTo("unified-login");
    }
}
