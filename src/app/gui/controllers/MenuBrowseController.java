package app.gui.controllers;

import Core.MenuItem;
import Core.Student;
import Services.MenuManager;
import Services.LoyaltyProgramService;
import Values.Discount;
import Values.Selection;
import app.gui.shared.AlertHelper;
import app.gui.NavigationService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import terminal.ServiceContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Controller for the menu browsing and order placement screen.
 * Allows students to browse menu items, add them to cart, and proceed to checkout.
 */
public class MenuBrowseController {
    private static final Logger logger = Logger.getLogger(MenuBrowseController.class.getName());
    
    @FXML private Label studentInfoLabel;
    @FXML private TableView<MenuItem> menuTableView;
    @FXML private TableColumn<MenuItem, Integer> idColumn;
    @FXML private TableColumn<MenuItem, String> nameColumn;
    @FXML private TableColumn<MenuItem, String> descriptionColumn;
    @FXML private TableColumn<MenuItem, String> priceColumn;
    @FXML private TableColumn<MenuItem, String> categoryColumn;
    @FXML private TableColumn<MenuItem, Void> quantityColumn;
    @FXML private TableColumn<MenuItem, Void> actionColumn;
    
    @FXML private ListView<String> cartListView;
    @FXML private Label subtotalLabel;
    @FXML private Label availablePointsLabel;
    @FXML private TextField pointsRedemptionField;
    @FXML private Label discountLabel;
    @FXML private Label totalLabel;
    @FXML private Button checkoutButton;
    
    private Student currentStudent;
    private ServiceContainer services;
    private MenuManager menuManager;
    private LoyaltyProgramService loyaltyService;
    
    // Cart data structures
    private Map<Integer, CartItem> cart = new HashMap<>();
    private Map<Integer, Spinner<Integer>> quantitySpinners = new HashMap<>();
    private double subtotal = 0.0;
    private double discount = 0.0;
    private int redeemedPoints = 0;
    
    /**
     * Initialize the controller.
     * Called automatically by JavaFX after FXML loading.
     */
    @FXML
    public void initialize() {
        logger.info("Initializing MenuBrowseController");
        services = NavigationService.getServiceContainer();
        
        if (services != null) {
            menuManager = services.getMenuManager();
            loyaltyService = services.getLoyaltyService();
        }
        
        setupTableColumns();
    }
    
    /**
     * Set the student data and load menu items.
     * This method is called by NavigationService when navigating to this screen.
     * 
     * @param student The student object
     */
    public void setStudent(Student student) {
        logger.info("Setting student data: " + (student != null ? student.getStudentCode() : "null"));
        this.currentStudent = student;
        updateStudentInfo();
        loadMenuItems();
    }
    
    /**
     * Setup table columns with cell value factories and custom cells.
     */
    private void setupTableColumns() {
        // ID column
        idColumn.setCellValueFactory(cellData -> 
            new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        
        // Name column
        nameColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getName()));
        
        // Description column
        descriptionColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDescription()));
        
        // Price column
        priceColumn.setCellValueFactory(cellData -> {
            MenuItem item = cellData.getValue();
            String priceStr = item.getPrice() != null ? 
                String.format("%.2f EGP", item.getPrice().getAmount().doubleValue()) : "N/A";
            return new SimpleStringProperty(priceStr);
        });
        
        // Category column
        categoryColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getCategory().toString()));
        
        // Quantity column with spinner
        quantityColumn.setCellFactory(col -> new TableCell<MenuItem, Void>() {
            private final Spinner<Integer> spinner = new Spinner<>(1, 99, 1);
            
            {
                spinner.setEditable(true);
                spinner.setPrefWidth(80);
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    MenuItem menuItem = getTableRow().getItem();
                    quantitySpinners.put(menuItem.getId(), spinner);
                    setGraphic(spinner);
                }
            }
        });
        
        // Action column with "Add to Cart" button
        actionColumn.setCellFactory(col -> new TableCell<MenuItem, Void>() {
            private final Button addButton = new Button("Add");
            
            {
                addButton.getStyleClass().add("secondary-button");
                addButton.setOnAction(event -> {
                    MenuItem menuItem = getTableRow().getItem();
                    if (menuItem != null) {
                        handleAddToCart(menuItem);
                    }
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    setGraphic(addButton);
                }
            }
        });
    }
    
    /**
     * Update student information display.
     */
    private void updateStudentInfo() {
        if (currentStudent != null) {
            studentInfoLabel.setText(String.format("Student: %s (%s)", 
                currentStudent.getName(), currentStudent.getStudentCode()));
            
            int points = currentStudent.getAccount() != null ? 
                currentStudent.getAccount().balance() : 0;
            availablePointsLabel.setText(String.valueOf(points));
        }
    }
    
    /**
     * Load menu items from MenuManager into TableView.
     */
    private void loadMenuItems() {
        try {
            if (menuManager == null) {
                logger.warning("MenuManager is null");
                AlertHelper.showError("Error", "Menu service not available");
                return;
            }
            
            List<MenuItem> items = menuManager.getAvailableItems();
            ObservableList<MenuItem> menuItems = FXCollections.observableArrayList(items);
            menuTableView.setItems(menuItems);
            
            logger.info("Loaded " + items.size() + " menu items");
            
        } catch (Exception e) {
            logger.severe("Error loading menu items: " + e.getMessage());
            e.printStackTrace();
            AlertHelper.showError("Error", "Failed to load menu items: " + e.getMessage());
        }
    }
    
    /**
     * Handle Add to Cart button click.
     * 
     * @param menuItem The menu item to add
     */
    private void handleAddToCart(MenuItem menuItem) {
        try {
            Spinner<Integer> spinner = quantitySpinners.get(menuItem.getId());
            int quantity = spinner != null ? spinner.getValue() : 1;
            
            if (cart.containsKey(menuItem.getId())) {
                // Update existing cart item
                CartItem cartItem = cart.get(menuItem.getId());
                cartItem.quantity += quantity;
            } else {
                // Add new cart item
                cart.put(menuItem.getId(), new CartItem(menuItem, quantity));
            }
            
            updateCartDisplay();
            
            logger.info("Added to cart: " + menuItem.getName() + " x" + quantity);
            
        } catch (Exception e) {
            logger.severe("Error adding to cart: " + e.getMessage());
            AlertHelper.showError("Error", "Failed to add item to cart: " + e.getMessage());
        }
    }
    
    /**
     * Handle Remove from Cart action.
     * 
     * @param menuItemId The ID of the menu item to remove
     */
    private void handleRemoveFromCart(int menuItemId) {
        cart.remove(menuItemId);
        updateCartDisplay();
        logger.info("Removed item from cart: " + menuItemId);
    }
    
    /**
     * Update the cart display with current items and totals.
     */
    private void updateCartDisplay() {
        ObservableList<String> cartDisplay = FXCollections.observableArrayList();
        subtotal = 0.0;
        
        for (CartItem cartItem : cart.values()) {
            double lineTotal = cartItem.menuItem.getPrice().getAmount().doubleValue() * cartItem.quantity;
            subtotal += lineTotal;
            
            String displayText = String.format("%s x%d - %.2f EGP", 
                cartItem.menuItem.getName(), cartItem.quantity, lineTotal);
            cartDisplay.add(displayText);
        }
        
        // Add remove buttons functionality through custom cell factory
        cartListView.setItems(cartDisplay);
        cartListView.setCellFactory(lv -> new ListCell<String>() {
            private final Button removeButton = new Button("Remove");
            private final HBox hbox = new HBox(10);
            private final Label label = new Label();
            
            {
                removeButton.getStyleClass().add("secondary-button");
                removeButton.setStyle("-fx-font-size: 10px; -fx-padding: 2 8;");
                hbox.setAlignment(Pos.CENTER_LEFT);
                hbox.getChildren().addAll(label, removeButton);
                HBox.setHgrow(label, javafx.scene.layout.Priority.ALWAYS);
                
                removeButton.setOnAction(event -> {
                    int index = getIndex();
                    if (index >= 0 && index < cart.size()) {
                        List<Integer> keys = new ArrayList<>(cart.keySet());
                        int menuItemId = keys.get(index);
                        handleRemoveFromCart(menuItemId);
                    }
                });
            }
            
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    label.setText(item);
                    setGraphic(hbox);
                }
            }
        });
        
        // Update totals
        subtotalLabel.setText(String.format("%.2f EGP", subtotal));
        
        double total = subtotal - discount;
        totalLabel.setText(String.format("%.2f EGP", Math.max(0, total)));
        
        // Enable/disable checkout button
        checkoutButton.setDisable(cart.isEmpty());
    }
    
    /**
     * Handle Redeem Points button click.
     */
    @FXML
    private void handleRedeemPoints() {
        try {
            String pointsText = pointsRedemptionField.getText().trim();
            
            if (pointsText.isEmpty()) {
                AlertHelper.showWarning("Validation Error", "Please enter points to redeem");
                return;
            }
            
            int pointsToRedeem = Integer.parseInt(pointsText);
            
            if (pointsToRedeem <= 0) {
                AlertHelper.showWarning("Validation Error", "Points must be greater than 0");
                return;
            }
            
            if (currentStudent == null) {
                AlertHelper.showError("Error", "Student data not available");
                return;
            }
            
            int availablePoints = currentStudent.getAccount() != null ? 
                currentStudent.getAccount().balance() : 0;
            
            if (pointsToRedeem > availablePoints) {
                AlertHelper.showWarning("Insufficient Points", 
                    String.format("You only have %d points available", availablePoints));
                return;
            }
            
            // Calculate discount (1 point = 0.1 EGP)
            discount = pointsToRedeem * 0.1;
            redeemedPoints = pointsToRedeem;
            
            // Update display
            discountLabel.setText(String.format("Discount: %.2f EGP (-%d points)", 
                discount, redeemedPoints));
            updateCartDisplay();
            
            logger.info("Applied discount: " + discount + " EGP for " + redeemedPoints + " points");
            
        } catch (NumberFormatException e) {
            AlertHelper.showError("Validation Error", "Please enter a valid number");
        } catch (Exception e) {
            logger.severe("Error redeeming points: " + e.getMessage());
            AlertHelper.showError("Error", "Failed to redeem points: " + e.getMessage());
        }
    }
    
    /**
     * Handle Checkout button click.
     * Navigate to payment screen with order data.
     */
    @FXML
    private void handleCheckout() {
        logger.info("Checkout button clicked");
        
        if (cart.isEmpty()) {
            AlertHelper.showWarning("Empty Cart", "Please add items to cart before checkout");
            return;
        }
        
        if (currentStudent == null) {
            AlertHelper.showError("Error", "Student data not available");
            return;
        }
        
        try {
            // Prepare order data
            OrderData orderData = new OrderData();
            orderData.student = currentStudent;
            orderData.cartItems = new ArrayList<>(cart.values());
            orderData.subtotal = subtotal;
            orderData.discount = discount;
            orderData.redeemedPoints = redeemedPoints;
            orderData.total = Math.max(0, subtotal - discount);
            
            // Navigate to order cart (payment) screen
            NavigationService.navigateToWithData("order-cart", orderData);
            
        } catch (Exception e) {
            logger.severe("Error during checkout: " + e.getMessage());
            e.printStackTrace();
            AlertHelper.showError("Checkout Error", "Failed to proceed to checkout: " + e.getMessage());
        }
    }
    
    /**
     * Handle Back button click.
     * Return to student dashboard.
     */
    @FXML
    private void handleBack() {
        logger.info("Back button clicked");
        
        try {
            if (currentStudent != null) {
                NavigationService.navigateToWithData("student-dashboard", currentStudent);
            } else {
                NavigationService.navigateTo("main-menu");
            }
        } catch (Exception e) {
            logger.severe("Error navigating back: " + e.getMessage());
            AlertHelper.showError("Navigation Error", "Failed to go back: " + e.getMessage());
        }
    }
    
    /**
     * Inner class to represent a cart item.
     */
    public static class CartItem {
        public MenuItem menuItem;
        public int quantity;
        
        public CartItem(MenuItem menuItem, int quantity) {
            this.menuItem = menuItem;
            this.quantity = quantity;
        }
    }
    
    /**
     * Inner class to hold order data for passing to payment screen.
     */
    public static class OrderData {
        public Student student;
        public List<CartItem> cartItems;
        public double subtotal;
        public double discount;
        public int redeemedPoints;
        public double total;
    }
}
