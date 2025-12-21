package app.gui.controllers;

import Core.*;
import Services.OrderProcessor;
import Services.PaymentRegistry;
import Services.LoyaltyProgramService;
import Interfaces.IPaymentProcessor;
import Values.Selection;
import app.gui.shared.AlertHelper;
import app.gui.NavigationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import terminal.ServiceContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Controller for the order cart and payment processing screen.
 * Handles order confirmation and payment processing.
 */
public class OrderCartController {
    private static final Logger logger = Logger.getLogger(OrderCartController.class.getName());
    
    @FXML private Label studentInfoLabel;
    @FXML private ListView<String> orderItemsListView;
    @FXML private Label subtotalLabel;
    @FXML private Label discountLabel;
    @FXML private Label totalLabel;
    @FXML private ComboBox<String> paymentMethodComboBox;
    
    private ServiceContainer services;
    private OrderProcessor orderProcessor;
    private PaymentRegistry paymentRegistry;
    private LoyaltyProgramService loyaltyService;
    
    private MenuBrowseController.OrderData orderData;
    
    /**
     * Initialize the controller.
     * Called automatically by JavaFX after FXML loading.
     */
    @FXML
    public void initialize() {
        logger.info("Initializing OrderCartController");
        services = NavigationService.getServiceContainer();
        
        if (services != null) {
            orderProcessor = services.getOrderProcessor();
            paymentRegistry = services.getPaymentRegistry();
            loyaltyService = services.getLoyaltyService();
        }
        
        loadPaymentMethods();
    }
    
    /**
     * Set the order data and populate the UI.
     * This method is called by NavigationService when navigating to this screen.
     * 
     * @param data The order data object
     */
    public void setData(Object data) {
        if (data instanceof MenuBrowseController.OrderData) {
            this.orderData = (MenuBrowseController.OrderData) data;
            populateOrderSummary();
        } else {
            logger.warning("Invalid data type passed to OrderCartController");
            AlertHelper.showError("Error", "Invalid order data");
        }
    }
    
    /**
     * Load available payment methods from PaymentRegistry.
     */
    private void loadPaymentMethods() {
        try {
            if (paymentRegistry == null) {
                logger.warning("PaymentRegistry is null");
                return;
            }
            
            List<String> paymentMethods = paymentRegistry.listNames();
            ObservableList<String> methods = FXCollections.observableArrayList(paymentMethods);
            paymentMethodComboBox.setItems(methods);
            
            // Select first method by default
            if (!methods.isEmpty()) {
                paymentMethodComboBox.getSelectionModel().selectFirst();
            }
            
            logger.info("Loaded " + paymentMethods.size() + " payment methods");
            
        } catch (Exception e) {
            logger.severe("Error loading payment methods: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Populate the order summary with cart items and totals.
     */
    private void populateOrderSummary() {
        if (orderData == null) {
            logger.warning("Order data is null");
            return;
        }
        
        try {
            // Update student info
            if (orderData.student != null) {
                studentInfoLabel.setText(String.format("Student: %s (%s)", 
                    orderData.student.getName(), orderData.student.getStudentCode()));
            }
            
            // Populate order items list
            ObservableList<String> items = FXCollections.observableArrayList();
            for (MenuBrowseController.CartItem cartItem : orderData.cartItems) {
                double lineTotal = cartItem.menuItem.getPrice().getAmount().doubleValue() * cartItem.quantity;
                String itemText = String.format("%s x%d - %.2f EGP", 
                    cartItem.menuItem.getName(), cartItem.quantity, lineTotal);
                items.add(itemText);
            }
            orderItemsListView.setItems(items);
            
            // Update totals
            subtotalLabel.setText(String.format("%.2f EGP", orderData.subtotal));
            discountLabel.setText(String.format("-%.2f EGP", orderData.discount));
            totalLabel.setText(String.format("%.2f EGP", orderData.total));
            
            logger.info("Order summary populated successfully");
            
        } catch (Exception e) {
            logger.severe("Error populating order summary: " + e.getMessage());
            e.printStackTrace();
            AlertHelper.showError("Error", "Failed to display order summary: " + e.getMessage());
        }
    }
    
    /**
     * Handle Confirm Payment button click.
     * Create order and process payment.
     */
    @FXML
    private void handleConfirmPayment() {
        logger.info("Confirm Payment button clicked");
        
        if (orderData == null) {
            AlertHelper.showError("Error", "Order data not available");
            return;
        }
        
        // Validate payment method selection
        String selectedMethod = paymentMethodComboBox.getValue();
        if (selectedMethod == null || selectedMethod.isEmpty()) {
            AlertHelper.showWarning("Validation Error", "Please select a payment method");
            return;
        }
        
        try {
            // Get payment processor
            IPaymentProcessor processor = paymentRegistry.get(selectedMethod);
            if (processor == null) {
                AlertHelper.showError("Error", "Payment method not available");
                return;
            }
            
            // Create selections list for order
            List<Selection> selections = new ArrayList<>();
            for (MenuBrowseController.CartItem cartItem : orderData.cartItems) {
                selections.add(new Selection(cartItem.menuItem.getId(), cartItem.quantity));
            }
            
            // Redeem loyalty points if any
            if (orderData.redeemedPoints > 0) {
                try {
                    loyaltyService.redeem(orderData.student, orderData.redeemedPoints);
                    logger.info("Redeemed " + orderData.redeemedPoints + " loyalty points");
                } catch (Exception e) {
                    logger.warning("Failed to redeem points: " + e.getMessage());
                    AlertHelper.showError("Error", "Failed to redeem loyalty points: " + e.getMessage());
                    return;
                }
            }
            
            // Place order
            Order order = orderProcessor.placeOrder(orderData.student, selections);
            
            if (order == null || order.getCode() == null) {
                AlertHelper.showError("Error", "Failed to create order");
                return;
            }
            
            logger.info("Order created: " + order.getCode());
            
            // Create payment
            String paymentId = "PAY-" + order.getCode() + "-" + System.currentTimeMillis();
            Payment payment = new Payment(paymentId, orderData.total, processor);
            
            // Complete order with loyalty points
            PaymentResult result = orderProcessor.completeOrderWithLoyalty(order.getCode(), payment);
            
            // Display result
            if (result.isSuccess()) {
                String successMessage = String.format(
                    "Order placed successfully!\n\n" +
                    "Order Code: %s\n" +
                    "Payment ID: %s\n" +
                    "Total Paid: %.2f EGP\n" +
                    "Payment Method: %s\n\n" +
                    "Thank you for your order!",
                    order.getCode(),
                    result.getTxId(),
                    orderData.total,
                    selectedMethod
                );
                
                AlertHelper.showSuccess("Payment Successful", successMessage);
                
                // Navigate back to student dashboard
                NavigationService.navigateToWithData("student-dashboard", orderData.student);
                
            } else {
                String errorMessage = "Payment failed. Please try again.";
                if (result.getTxId() != null && !result.getTxId().isEmpty()) {
                    errorMessage += "\nReason: " + result.getTxId();
                }
                AlertHelper.showError("Payment Failed", errorMessage);
            }
            
        } catch (Exception e) {
            logger.severe("Error processing payment: " + e.getMessage());
            e.printStackTrace();
            AlertHelper.showError("Payment Error", "Failed to process payment: " + e.getMessage());
        }
    }
    
    /**
     * Handle Cancel button click.
     * Return to menu browse screen.
     */
    @FXML
    private void handleCancel() {
        logger.info("Cancel button clicked");
        
        try {
            if (orderData != null && orderData.student != null) {
                // Return points if they were redeemed (since we haven't processed payment yet)
                // Note: Points are only actually redeemed in handleConfirmPayment
                NavigationService.navigateToWithData("menu-browse", orderData.student);
            } else {
                NavigationService.navigateTo("main-menu");
            }
        } catch (Exception e) {
            logger.severe("Error navigating back: " + e.getMessage());
            AlertHelper.showError("Navigation Error", "Failed to go back: " + e.getMessage());
        }
    }
}
