package app.gui.student.controllers;

import Core.MenuItem;
import Core.Student;
import Services.MenuManager;
import Services.NotificationHistoryService;
import Services.LoyaltyProgramService;
import app.gui.shared.AlertHelper;
import app.gui.student.StudentNavigationService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import terminal.ServiceContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Controller for the menu dashboard screen in the Student Application.
 * This is the main screen students see after login, displaying menu items,
 * shopping cart, and navigation to other features.
 */
public class MenuDashboardController {
    private static final Logger logger = Logger.getLogger(MenuDashboardController.class.getName());
    
    @FXML private Label studentInfoLabel;
    @FXML private Label loyaltyPointsLabel;
    @FXML private Button notificationButton;
    @FXML private Label notificationBadge;

    // Welcome banner elements
    @FXML private HBox welcomeBanner;
    @FXML private Label welcomeTitle;
    @FXML private Label welcomeMessage;

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
    @FXML private Label discountLabel;
    @FXML private Label totalLabel;
    @FXML private Button checkoutButton;
    
    private Student currentStudent;
    private ServiceContainer services;
    private MenuManager menuManager;
    private NotificationHistoryService notificationService;
    private LoyaltyProgramService loyaltyService;
    
    // Cart data structures
    private Map<Integer, CartItem> cart = new HashMap<>();
    private Map<Integer, Spinner<Integer>> quantitySpinners = new HashMap<>();
    private double subtotal = 0.0;
    private double discount = 0.0;

    // Flag to track if this is a new registration
    private boolean isNewRegistration = false;

    /**
     * Initialize the controller.
     * Called automatically by JavaFX after FXML loading.
     */
    @FXML
    public void initialize() {
        logger.info("Initializing MenuDashboardController");
        services = StudentNavigationService.getServiceContainer();
        
        if (services != null) {
            menuManager = services.getMenuManager();
            notificationService = services.getNotificationHistoryService();
            loyaltyService = services.getLoyaltyService();
        }
        
        setupTableColumns();
    }
    
    /**
     * Set the student data and load menu items.
     * This method is called by StudentNavigationService when navigating to this screen.
     * 
     * @param student The student object
     */
    public void setStudent(Student student) {
        setStudent(student, false);
    }

    /**
     * Set the student data and load menu items, with option to show welcome banner.
     *
     * @param student The student object
     * @param showWelcome Whether to show the welcome banner (for new registrations)
     */
    public void setStudent(Student student, boolean showWelcome) {
        logger.info("Setting student data: " + (student != null ? student.getStudentCode() : "null") +
                   ", showWelcome: " + showWelcome);
        this.currentStudent = student;
        this.isNewRegistration = showWelcome;

        // Always hide the banner first (important for cached scenes)
        if (welcomeBanner != null) {
            welcomeBanner.setVisible(false);
            welcomeBanner.setManaged(false);
        }

        updateStudentInfo();
        updateLoyaltyPoints();
        loadMenuItems();
        updateNotificationBadge();

        // Show welcome banner ONLY if this is a new registration
        if (showWelcome && student != null) {
            showWelcomeBanner(student);
        }
    }

    /**
     * Show the welcome banner for new registrations
     */
    private void showWelcomeBanner(Student student) {
        if (welcomeBanner != null && welcomeMessage != null) {
            welcomeTitle.setText("🎉 Welcome to our Cafeteria, " + student.getName() + "!");
            welcomeMessage.setText("Your student code is: " + student.getStudentCode().toUpperCase() +
                                 " - Please save this for future logins! ☕");
            welcomeBanner.setVisible(true);
            welcomeBanner.setManaged(true);
            logger.info("Showing welcome banner for new student: " + student.getStudentCode());
        }
    }

    /**
     * Handle close banner button click
     */
    @FXML
    private void handleCloseBanner() {
        if (welcomeBanner != null) {
            welcomeBanner.setVisible(false);
            welcomeBanner.setManaged(false);
            logger.info("Welcome banner closed by user");
        }
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
        }
    }
    
    /**
     * Update loyalty points display.
     */
    private void updateLoyaltyPoints() {
        try {
            if (currentStudent != null && loyaltyService != null) {
                int points = loyaltyService.getBalance(currentStudent);
                loyaltyPointsLabel.setText(String.format("Loyalty Points: %d", points));
                logger.info("Updated loyalty points display: " + points);
            } else {
                loyaltyPointsLabel.setText("Loyalty Points: 0");
            }
        } catch (Exception e) {
            logger.warning("Error updating loyalty points: " + e.getMessage());
            loyaltyPointsLabel.setText("Loyalty Points: 0");
        }
    }
    
    /**
     * Refresh loyalty points from database and update UI.
     * This method should be called after order completion to show updated points immediately.
     */
    public void refreshLoyaltyPoints() {
        try {
            if (currentStudent != null && loyaltyService != null) {
                // Query database for current points
                int updatedPoints = loyaltyService.getBalance(currentStudent);
                
                // Update the cached student object's loyalty account
                if (currentStudent.getAccount() != null) {
                    currentStudent.getAccount().setPoints(updatedPoints);
                }
                
                // Update UI display
                loyaltyPointsLabel.setText(String.format("Loyalty Points: %d", updatedPoints));
                
                // Add visual feedback
                loyaltyPointsLabel.setStyle("-fx-text-fill: #FFD700; -fx-font-weight: bold;");
                
                // Reset style after 2 seconds
                javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(2));
                pause.setOnFinished(e -> loyaltyPointsLabel.setStyle(""));
                pause.play();
                
                logger.info("Refreshed loyalty points: " + updatedPoints);
            }
        } catch (Exception e) {
            logger.warning("Error refreshing loyalty points: " + e.getMessage());
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
     * Get the count of unread notifications for the current student.
     * This method queries the NotificationHistoryService for the unread count.
     * 
     * @return The number of unread notifications, or 0 if service is unavailable or student is null
     */
    public int getUnreadNotificationCount() {
        try {
            if (notificationService != null && currentStudent != null) {
                return notificationService.getUnreadNotificationCount(currentStudent.getStudentCode());
            }
        } catch (Exception e) {
            logger.warning("Error getting unread notification count: " + e.getMessage());
        }
        return 0;
    }
    
    /**
     * Refresh the notification badge by reloading the count from the database.
     * This method should be called when returning from the notifications screen
     * to update the badge with the latest unread count.
     */
    public void refreshNotificationBadge() {
        logger.info("Refreshing notification badge");
        updateNotificationBadge();
        updateLoyaltyPoints(); // Also refresh loyalty points when returning to dashboard
    }

    /**
     * Update notification badge with unread count.
     * Shows badge when unread notifications > 0, hides otherwise.
     */
    private void updateNotificationBadge() {
        try {
            if (notificationService != null && currentStudent != null) {
                int unreadCount = getUnreadNotificationCount();

                if (unreadCount > 0) {
                    notificationBadge.setVisible(true);
                    notificationBadge.setManaged(true);

                    notificationBadge.setText(String.valueOf(unreadCount));
                } else {
                    notificationBadge.setVisible(false);
                    notificationBadge.setManaged(false);
                }

                logger.info("Updated notification badge: " + unreadCount);
            } else {
                notificationBadge.setVisible(false);
                notificationBadge.setManaged(false);
            }
        } catch (Exception e) {
            logger.warning("Error updating notification badge: " + e.getMessage());
            notificationBadge.setVisible(false);
            notificationBadge.setManaged(false);
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
        
        // Update discount display (currently 0, will be used in payment screen)
        discountLabel.setText(String.format("Discount: %.2f EGP", discount));
        
        double total = subtotal - discount;
        totalLabel.setText(String.format("%.2f EGP", Math.max(0, total)));
        
        // Enable/disable checkout button
        checkoutButton.setDisable(cart.isEmpty());
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
            orderData.total = Math.max(0, subtotal - discount);
            
            // Navigate to order payment screen
            StudentNavigationService.navigateToWithData("order-payment", orderData);
            
        } catch (Exception e) {
            logger.severe("Error during checkout: " + e.getMessage());
            e.printStackTrace();
            AlertHelper.showError("Checkout Error", "Failed to proceed to checkout: " + e.getMessage());
        }
    }
    
    /**
     * Handle Refresh button click.
     * Refresh notification badge and loyalty points from database.
     */
    @FXML
    private void handleRefresh() {
        logger.info("Refresh button clicked");
        
        if (currentStudent == null) {
            AlertHelper.showWarning("Warning", "Student data not available");
            return;
        }
        
        try {
            // Refresh notification badge
            updateNotificationBadge();
            
            // Refresh loyalty points
            updateLoyaltyPoints();
            
            logger.info("Dashboard refreshed successfully");
            
        } catch (Exception e) {
            logger.severe("Error refreshing dashboard: " + e.getMessage());
            AlertHelper.showError("Refresh Error", "Failed to refresh data: " + e.getMessage());
        }
    }
    
    /**
     * Handle View Notifications button click.
     * Navigate to notifications screen.
     */
    @FXML
    private void handleViewNotifications() {
        logger.info("View Notifications button clicked");
        
        if (currentStudent == null) {
            AlertHelper.showError("Error", "Student data not available");
            return;
        }
        
        try {
            StudentNavigationService.navigateToWithData("notifications", currentStudent);
        } catch (Exception e) {
            logger.severe("Error navigating to notifications: " + e.getMessage());
            AlertHelper.showError("Navigation Error", "Failed to open notifications: " + e.getMessage());
        }
    }
    
    /**
     * Handle View Order History button click.
     * Navigate to order history screen.
     */
    @FXML
    private void handleViewOrderHistory() {
        logger.info("View Order History button clicked");
        
        if (currentStudent == null) {
            AlertHelper.showError("Error", "Student data not available");
            return;
        }
        
        try {
            StudentNavigationService.navigateToWithData("order-history", currentStudent);
        } catch (Exception e) {
            logger.severe("Error navigating to order history: " + e.getMessage());
            AlertHelper.showError("Navigation Error", "Failed to open order history: " + e.getMessage());
        }
    }
    
    /**
     * Handle Logout button click.
     * Return to student login screen.
     */
    @FXML
    private void handleLogout() {
        logger.info("Logout button clicked");
        
        try {
            // Clear the current student
            currentStudent = null;
            
            // Navigate back to student login
            StudentNavigationService.navigateTo("student-login");
            
            logger.info("Logged out successfully");
            
        } catch (Exception e) {
            logger.severe("Error during logout: " + e.getMessage());
            AlertHelper.showError("Logout Error", "Failed to logout: " + e.getMessage());
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
        public double total;
    }
}
