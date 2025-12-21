package app.gui.admin.controllers;

import Core.Order;
import Core.OrderItem;
import Services.OrderProcessor;
import ServiceManagers.OrderManagement;
import app.gui.shared.AlertHelper;
import app.gui.admin.AdminNavigationService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import terminal.ServiceContainer;
import Enums.OrderStatus;

import java.util.List;
import java.util.logging.Logger;

/**
 * Controller for Order Manager Dashboard.
 * Allows order manager to view pending orders and update their status.
 */
public class OrderManagerDashboardController {
    private static final Logger logger = Logger.getLogger(OrderManagerDashboardController.class.getName());
    
    @FXML private Label welcomeLabel;
    @FXML private TableView<Order> ordersTable;
    @FXML private TableColumn<Order, Integer> orderIdColumn;
    @FXML private TableColumn<Order, String> studentIdColumn;
    @FXML private TableColumn<Order, Double> totalColumn;
    @FXML private TableColumn<Order, String> statusColumn;
    @FXML private TableColumn<Order, String> dateColumn;
    
    @FXML private TextArea orderDetailsArea;
    @FXML private ComboBox<OrderStatus> statusComboBox;
    
    private OrderManagement manager;
    private ServiceContainer services;
    private OrderProcessor orderProcessor;
    private ObservableList<Order> ordersList;
    private Order selectedOrder;
    
    @FXML
    public void initialize() {
        services = AdminNavigationService.getServiceContainer();
        orderProcessor = services.getOrderProcessor();
        
        // Setup table columns
        orderIdColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        studentIdColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStudentCode()));
        totalColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().total().getAmount().doubleValue()).asObject());
        statusColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus().toString()));
        dateColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCode() != null ? data.getValue().getCode() : "N/A"));
        
        ordersList = FXCollections.observableArrayList();
        ordersTable.setItems(ordersList);
        
        // Setup status combo box
        statusComboBox.setItems(FXCollections.observableArrayList(OrderStatus.values()));
        
        // Load order details on selection
        ordersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedOrder = newSelection;
                displayOrderDetails(newSelection);
                statusComboBox.setValue(newSelection.getStatus());
            }
        });
        
        logger.info("OrderManagerDashboardController initialized");
    }
    
    public void setManagerData(OrderManagement manager) {
        this.manager = manager;
        welcomeLabel.setText("Welcome, " + manager.getName() + " - Order Manager");
        loadPendingOrders();
    }
    
    @FXML
    private void handleLoadOrders() {
        loadPendingOrders();
    }
    
    private void loadPendingOrders() {
        try {
            List<Order> orders = orderProcessor.trackPendingOrders();
            ordersList.clear();
            ordersList.addAll(orders);
            logger.info("Loaded " + orders.size() + " pending orders");
        } catch (Exception e) {
            logger.severe("Error loading orders: " + e.getMessage());
            AlertHelper.showError("Load Error", "Failed to load orders: " + e.getMessage());
        }
    }
    
    private void displayOrderDetails(Order order) {
        StringBuilder details = new StringBuilder();
        details.append("Order ID: ").append(order.getId()).append("\n");
        details.append("Order Code: ").append(order.getCode() != null ? order.getCode() : "N/A").append("\n");
        details.append("Student Code: ").append(order.getStudentCode()).append("\n");
        details.append("Status: ").append(order.getStatus()).append("\n");
        details.append("Total Amount: ").append(String.format("%.2f EGP", order.total().getAmount().doubleValue())).append("\n\n");
        details.append("Items:\n");
        
        for (OrderItem item : order.getItems()) {
            details.append(String.format("  - %s x%d @ %.2f EGP = %.2f EGP\n", 
                item.getNameSnapshot(), 
                item.getQty(), 
                item.getUnitPrice().getAmount().doubleValue(), 
                item.lineTotal().getAmount().doubleValue()));
        }
        
        orderDetailsArea.setText(details.toString());
    }
    
    @FXML
    private void handleMarkPreparing() {
        updateOrderStatus(OrderStatus.PREPARING);
    }
    
    @FXML
    private void handleMarkReady() {
        updateOrderStatus(OrderStatus.READY);
    }
    
    @FXML
    private void handleUpdateStatus() {
        OrderStatus newStatus = statusComboBox.getValue();
        if (newStatus != null) {
            updateOrderStatus(newStatus);
        } else {
            AlertHelper.showError("Validation Error", "Please select a status");
        }
    }
    
    private void updateOrderStatus(OrderStatus newStatus) {
        if (selectedOrder == null) {
            AlertHelper.showError("Selection Error", "Please select an order first");
            return;
        }
        
        try {
            // Use advanceStatus method from OrderProcessor
            orderProcessor.advanceStatus(selectedOrder.getId(), newStatus);
            
            // Send notification if status is PREPARING
            if (newStatus == OrderStatus.PREPARING) {
                sendOrderPreparingNotification(selectedOrder);
            }
            
            AlertHelper.showSuccess("Success", "Order status updated to " + newStatus);
            loadPendingOrders();
            orderDetailsArea.clear();
            selectedOrder = null;
        } catch (Exception e) {
            logger.severe("Error updating order status: " + e.getMessage());
            AlertHelper.showError("Error", "Failed to update order: " + e.getMessage());
        }
    }
    
    /**
     * Send a gold notification to student when their order is being prepared
     */
    private void sendOrderPreparingNotification(Order order) {
        try {
            Services.NotificationHistoryService notificationService = services.getNotificationHistoryService();
            if (notificationService != null) {
                String message = String.format("Your order %s is now being prepared",
                    order.getCode() != null ? order.getCode() : "#" + order.getId());
                
                notificationService.sendNotification(
                    order.getStudentCode(),
                    message,
                    "ORDER_PREPARING" // Using ORDER_READY type for special styling
                );
                
                logger.info("Sent preparing notification for order: " + order.getCode() + " to student: " + order.getStudentCode());
            }
        } catch (Exception e) {
            logger.warning("Failed to send preparing notification: " + e.getMessage());
            // Don't fail the status update if notification fails
        }
    }
    
    @FXML
    private void handleLogout() {
        logger.info("Order Manager logging out");
        AdminNavigationService.navigateTo("unified-login");
    }
}
