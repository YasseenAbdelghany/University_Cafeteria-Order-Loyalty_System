package app.gui.student.controllers;

import Core.*;
import Services.OrderProcessor;
import Services.PaymentRegistry;
import Services.LoyaltyProgramService;
import Services.FreeProductCalculator;
import Services.MenuManager;
import Interfaces.IPaymentProcessor;
import Values.Selection;
import app.gui.shared.AlertHelper;
import app.gui.student.StudentNavigationService;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.VBox;
import terminal.ServiceContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Controller for the order payment screen in the Student Application.
 * Handles order confirmation, loyalty points redemption, and payment processing.
 */
public class OrderPaymentController {
    private static final Logger logger = Logger.getLogger(OrderPaymentController.class.getName());
    private static final double EGP_PER_POINT = 0.1; // 1 point = 0.1 EGP discount
    
    @FXML private Label studentInfoLabel;
    @FXML private ListView<String> orderItemsListView;
    @FXML private Label subtotalLabel;
    @FXML private Label discountLabel;
    @FXML private Label totalLabel;
    @FXML private ComboBox<String> paymentMethodComboBox;
    @FXML private Label availablePointsLabel;
    @FXML private Label maxRedeemablePointsLabel;
    @FXML private Label pointsToEarnLabel;
    @FXML private Spinner<Integer> pointsRedemptionSpinner;
    @FXML private Label pointsDiscountLabel;
    @FXML private Label finalTotalLabel;
    @FXML private VBox freeProductsSection;
    @FXML private Label freeProductsInfoLabel;
    @FXML private ListView<String> freeProductsListView;
    
    private ServiceContainer services;
    private OrderProcessor orderProcessor;
    private PaymentRegistry paymentRegistry;
    private LoyaltyProgramService loyaltyService;
    private FreeProductCalculator freeProductCalculator;
    
    private MenuDashboardController.OrderData orderData;
    private int availablePoints = 0;
    private double pointsDiscount = 0.0;
    
    /**
     * Initialize the controller.
     * Called automatically by JavaFX after FXML loading.
     */
    @FXML
    public void initialize() {
        logger.info("Initializing OrderPaymentController");
        services = StudentNavigationService.getServiceContainer();
        
        if (services != null) {
            orderProcessor = services.getOrderProcessor();
            paymentRegistry = services.getPaymentRegistry();
            loyaltyService = services.getLoyaltyService();
            
            // Initialize FreeProductCalculator
            MenuManager menuManager = services.getMenuManager();
            if (menuManager != null) {
                freeProductCalculator = new FreeProductCalculator(menuManager);
            }
        }
        
        loadPaymentMethods();
        setupPointsRedemptionListener();
    }
    
    /**
     * Set the order data and populate the UI.
     * This method is called by StudentNavigationService when navigating to this screen.
     * 
     * @param data The order data object from MenuDashboardController
     */
    public void setData(Object data) {
        if (data instanceof MenuDashboardController.OrderData) {
            this.orderData = (MenuDashboardController.OrderData) data;
            populateOrderSummary();
            loadLoyaltyPoints();
        } else {
            logger.warning("Invalid data type passed to OrderPaymentController");
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
     * Setup listener for points redemption spinner to update discount in real-time.
     */
    private void setupPointsRedemptionListener() {
        // Will be configured after spinner is initialized with max value
    }
    
    /**
     * Load student's loyalty points and configure the redemption spinner.
     */
    private void loadLoyaltyPoints() {
        try {
            if (loyaltyService == null || orderData == null || orderData.student == null) {
                logger.warning("Cannot load loyalty points - service or student data unavailable");
                return;
            }
            
            availablePoints = loyaltyService.getBalance(orderData.student);
            availablePointsLabel.setText(String.valueOf(availablePoints));
            
            // Calculate and display max redeemable points for this order
            int maxRedeemablePoints = calculateMaxRedeemablePoints();
            maxRedeemablePointsLabel.setText(String.valueOf(maxRedeemablePoints));
            
            // Calculate and display points to be earned from this order
            int pointsToEarn = calculatePointsToEarn();
            pointsToEarnLabel.setText(String.valueOf(pointsToEarn));
            
            // Configure spinner with max value based on available points and order total
            SpinnerValueFactory<Integer> valueFactory = 
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, maxRedeemablePoints, 0);
            pointsRedemptionSpinner.setValueFactory(valueFactory);
            
            // Add listener to update discount when spinner value changes
            pointsRedemptionSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
                updatePointsDiscount(newValue);
                
                // Provide real-time validation feedback
                if (newValue != null) {
                    validatePointRedemptionRealTime(newValue);
                }
            });
            
            logger.info("Loaded loyalty points: " + availablePoints + 
                       ", Max redeemable: " + maxRedeemablePoints + 
                       ", Points to earn: " + pointsToEarn);
            
            // Load free products that can be obtained with current points
            loadFreeProducts();
            
        } catch (Exception e) {
            logger.severe("Error loading loyalty points: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Load and display products that can be obtained for free with current loyalty points.
     */
    private void loadFreeProducts() {
        try {
            if (freeProductCalculator == null || availablePoints <= 0) {
                // Hide free products section if no calculator or no points
                if (freeProductsSection != null) {
                    freeProductsSection.setVisible(false);
                    freeProductsSection.setManaged(false);
                }
                return;
            }
            
            // Get affordable items
            List<FreeProductCalculator.AffordableItem> affordableItems = 
                freeProductCalculator.getAffordableItems(availablePoints);
            
            if (affordableItems.isEmpty()) {
                // Hide section if no affordable items
                freeProductsSection.setVisible(false);
                freeProductsSection.setManaged(false);
                logger.info("No free products available with " + availablePoints + " points");
                return;
            }
            
            // Show section and populate list
            freeProductsSection.setVisible(true);
            freeProductsSection.setManaged(true);
            
            // Update info label
            freeProductsInfoLabel.setText(String.format(
                "You have %d points! Here are %d items you can get completely FREE:",
                availablePoints, affordableItems.size()
            ));
            
            // Populate ListView
            ObservableList<String> freeProductsList = FXCollections.observableArrayList();
            for (FreeProductCalculator.AffordableItem item : affordableItems) {
                String displayText = String.format("✨ %s - %d points (%.2f EGP) - %s",
                    item.menuItem.getName(),
                    item.pointsRequired,
                    item.priceInEGP,
                    item.menuItem.getDescription()
                );
                freeProductsList.add(displayText);
            }
            freeProductsListView.setItems(freeProductsList);
            
            logger.info("Loaded " + affordableItems.size() + " free products");
            
        } catch (Exception e) {
            logger.severe("Error loading free products: " + e.getMessage());
            e.printStackTrace();
            // Hide section on error
            if (freeProductsSection != null) {
                freeProductsSection.setVisible(false);
                freeProductsSection.setManaged(false);
            }
        }
    }
    
    /**
     * Calculate the number of loyalty points that will be earned from this order.
     * 1 point for every 10 EGP spent (floor).
     * 
     * @return Points to be earned
     */
    private int calculatePointsToEarn() {
        if (orderData == null) return 0;
        return (int) Math.floor(orderData.total / 10.0);
    }
    
    /**
     * Calculate the maximum number of points that can be redeemed.
     * Limited by both available points and order total.
     * 
     * @return Maximum redeemable points
     */
    private int calculateMaxRedeemablePoints() {
        if (orderData == null) return 0;
        
        // Maximum points limited by order total (can't redeem more than order value)
        int maxByTotal = (int) Math.floor(orderData.total / EGP_PER_POINT);
        
        // Return the minimum of available points and max by total
        return Math.min(availablePoints, maxByTotal);
    }
    
    /**
     * Update the points discount display based on redeemed points.
     * 
     * @param points Number of points to redeem
     */
    private void updatePointsDiscount(int points) {
        pointsDiscount = points * EGP_PER_POINT;
        pointsDiscountLabel.setText(String.format("%.2f EGP", pointsDiscount));
        
        // Update final total
        double finalTotal = Math.max(0, orderData.total - pointsDiscount);
        
        // Show "FREE" if total is zero or very close to zero
        if (finalTotal <= 0.01) {
            finalTotalLabel.setText("FREE (0.00 EGP)");
            finalTotalLabel.setStyle("-fx-text-fill: #FFD700; -fx-font-weight: bold; -fx-font-size: 18px;");
        } else {
            finalTotalLabel.setText(String.format("%.2f EGP", finalTotal));
            finalTotalLabel.setStyle(""); // Reset to default style
        }
        
        logger.info("Points discount updated: " + points + " points = " + pointsDiscount + " EGP, Final: " + finalTotal);
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
            for (MenuDashboardController.CartItem cartItem : orderData.cartItems) {
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
            
            // Initialize final total (before points redemption)
            finalTotalLabel.setText(String.format("%.2f EGP", orderData.total));
            
            logger.info("Order summary populated successfully");
            
        } catch (Exception e) {
            logger.severe("Error populating order summary: " + e.getMessage());
            e.printStackTrace();
            AlertHelper.showError("Error", "Failed to display order summary: " + e.getMessage());
        }
    }
    
    /**
     * Provide real-time validation feedback as user changes spinner value.
     * Updates UI styling to indicate valid/invalid redemption amounts.
     * 
     * @param redeemedPoints Number of points being considered for redemption
     */
    private void validatePointRedemptionRealTime(int redeemedPoints) {
        if (redeemedPoints == 0) {
            // Reset styling for zero points
            pointsRedemptionSpinner.setStyle("");
            return;
        }
        
        // Check for invalid redemption amounts
        if (redeemedPoints > availablePoints) {
            // Insufficient points - show red border
            pointsRedemptionSpinner.setStyle(
                "-fx-border-color: #FF4444; -fx-border-width: 2px; -fx-background-color: #FFE5E5;"
            );
            logger.warning("Real-time validation: Insufficient points (" + redeemedPoints + " > " + availablePoints + ")");
        } else if (redeemedPoints > calculateMaxRedeemablePoints()) {
            // Over-redemption - show orange border
            pointsRedemptionSpinner.setStyle(
                "-fx-border-color: #FF9800; -fx-border-width: 2px; -fx-background-color: #FFF3E0;"
            );
            logger.warning("Real-time validation: Over-redemption (" + redeemedPoints + " > max)");
        } else {
            // Valid redemption - show green border
            pointsRedemptionSpinner.setStyle(
                "-fx-border-color: #4CAF50; -fx-border-width: 2px; -fx-background-color: #E8F5E9;"
            );
        }
    }
    
    /**
     * Validate point redemption before processing payment.
     * Checks for sufficient points, over-redemption, and provides helpful error messages.
     * 
     * @param redeemedPoints Number of points to redeem
     * @return true if validation passes, false otherwise
     */
    private boolean validatePointRedemption(int redeemedPoints) {
        // No validation needed if not redeeming points
        if (redeemedPoints == 0) {
            return true;
        }
        
        // Check if points are negative (shouldn't happen with spinner, but safety check)
        if (redeemedPoints < 0) {
            AlertHelper.showError("Invalid Points", 
                "Points to redeem cannot be negative.\n\n" +
                "Please enter a valid number of points.");
            return false;
        }
        
        // Check if student has sufficient points
        if (redeemedPoints > availablePoints) {
            AlertHelper.showError("Insufficient Points", 
                String.format(
                    "You don't have enough loyalty points!\n\n" +
                    "Points you want to redeem: %d\n" +
                    "Points available: %d\n" +
                    "Points needed: %d more\n\n" +
                    "💡 Tip: Complete more orders to earn loyalty points!\n" +
                    "(1 point earned for every 10 EGP spent)",
                    redeemedPoints, availablePoints, (redeemedPoints - availablePoints)
                ));
            return false;
        }
        
        // Check if trying to redeem more than order total allows
        int maxRedeemablePoints = calculateMaxRedeemablePoints();
        if (redeemedPoints > maxRedeemablePoints) {
            double orderTotal = orderData.total;
            double maxDiscount = maxRedeemablePoints * EGP_PER_POINT;
            
            AlertHelper.showWarning("Over-Redemption", 
                String.format(
                    "You're trying to redeem more points than this order allows!\n\n" +
                    "Order Total: %.2f EGP\n" +
                    "Maximum Redeemable Points: %d (%.2f EGP discount)\n" +
                    "Points you tried to redeem: %d\n\n" +
                    "💡 The maximum has been set to avoid over-redemption.\n" +
                    "You can use remaining points on future orders!",
                    orderTotal, maxRedeemablePoints, maxDiscount, redeemedPoints
                ));
            return false;
        }
        
        // Check if redemption would result in negative balance (extra safety check)
        int remainingPoints = availablePoints - redeemedPoints;
        if (remainingPoints < 0) {
            AlertHelper.showError("Invalid Redemption", 
                String.format(
                    "This redemption would result in a negative point balance!\n\n" +
                    "Available Points: %d\n" +
                    "Points to Redeem: %d\n" +
                    "Resulting Balance: %d (INVALID)\n\n" +
                    "Please adjust the number of points to redeem.",
                    availablePoints, redeemedPoints, remainingPoints
                ));
            return false;
        }
        
        // Warn if redeeming excessive points (more than 80% of available)
        if (redeemedPoints > 0 && availablePoints > 0) {
            double redemptionPercentage = (double) redeemedPoints / availablePoints * 100;
            if (redemptionPercentage > 80) {
                boolean confirmed = AlertHelper.showConfirmation(
                    "High Point Redemption", 
                    String.format(
                        "You're about to redeem %.0f%% of your loyalty points!\n\n" +
                        "Available Points: %d\n" +
                        "Points to Redeem: %d\n" +
                        "Remaining Points: %d\n" +
                        "Discount: %.2f EGP\n\n" +
                        "Are you sure you want to proceed?",
                        redemptionPercentage, availablePoints, redeemedPoints, 
                        remainingPoints, (redeemedPoints * EGP_PER_POINT)
                    )
                );
                
                if (!confirmed) {
                    logger.info("User cancelled high point redemption");
                    return false;
                }
            }
        }
        
        // All validations passed
        logger.info(String.format("Point redemption validated: %d points (%.2f EGP discount)", 
            redeemedPoints, redeemedPoints * EGP_PER_POINT));
        return true;
    }
    
    /**
     * Handle Confirm Payment button click.
     * Create order, redeem loyalty points if specified, and process payment.
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
        
        // Get redeemed points
        int redeemedPoints = pointsRedemptionSpinner.getValue();
        
        // Validate point redemption
        if (!validatePointRedemption(redeemedPoints)) {
            return; // Validation failed, error message already shown
        }
        
        try {
            // Get payment processor
            IPaymentProcessor processor = paymentRegistry.get(selectedMethod);
            if (processor == null) {
                AlertHelper.showError("Error", "Payment method not available");
                return;
            }
            
            // Redeem loyalty points if any
            if (redeemedPoints > 0) {
                try {
                    loyaltyService.redeem(orderData.student, redeemedPoints);
                    logger.info("Redeemed " + redeemedPoints + " loyalty points");
                } catch (Exception e) {
                    logger.warning("Failed to redeem points: " + e.getMessage());
                    AlertHelper.showError("Error", "Failed to redeem loyalty points: " + e.getMessage());
                    return;
                }
            }
            
            // Create selections list for order
            List<Selection> selections = new ArrayList<>();
            for (MenuDashboardController.CartItem cartItem : orderData.cartItems) {
                selections.add(new Selection(cartItem.menuItem.getId(), cartItem.quantity));
            }
            
            // Place order
            Order order = orderProcessor.placeOrder(orderData.student, selections);
            
            if (order == null || order.getCode() == null) {
                AlertHelper.showError("Error", "Failed to create order");
                return;
            }
            
            logger.info("Order created: " + order.getCode());
            
            // Calculate final payment amount (after points discount)
            double finalAmount = Math.max(0, orderData.total - pointsDiscount);
            
            PaymentResult result;
            
            // Handle FREE order (fully paid with points)
            if (finalAmount <= 0.01) { // Use small threshold to handle floating point precision
                logger.info("Order is FREE - fully covered by loyalty points");
                
                // Create a successful payment result without calling payment processor
                result = new PaymentResult(true, "FREE-" + order.getCode());
                
                // Mark order as completed (you may need to update order status in database)
                // For now, we'll just return success
                
            } else {
                // Normal payment flow
                // Create payment
                String paymentId = "PAY-" + order.getCode() + "-" + System.currentTimeMillis();
                Payment payment = new Payment(paymentId, finalAmount, processor);
                
                // Complete order with loyalty points
                result = orderProcessor.completeOrderWithLoyalty(order.getCode(), payment);
            }
            
            // Display result
            if (result.isSuccess()) {
                String successMessage;
                
                // Check if order was FREE
                if (finalAmount <= 0.01) {
                    successMessage = String.format(
                        "🎉 Order placed successfully! 🎉\n\n" +
                        "Order Code: %s\n" +
                        "Payment ID: %s\n" +
                        "Subtotal: %.2f EGP\n" +
                        "Points Redeemed: %d (%.2f EGP)\n" +
                        "Total Paid: FREE (0.00 EGP)\n\n" +
                        "✨ Your order is completely FREE! ✨\n" +
                        "Paid entirely with loyalty points!\n\n" +
                        "Thank you for your order!",
                        order.getCode(),
                        result.getTxId(),
                        orderData.total,
                        redeemedPoints,
                        pointsDiscount
                    );
                } else if (redeemedPoints > 0) {
                    // Partial redemption
                    successMessage = String.format(
                        "Order placed successfully!\n\n" +
                        "Order Code: %s\n" +
                        "Payment ID: %s\n" +
                        "Subtotal: %.2f EGP\n" +
                        "Points Redeemed: %d (%.2f EGP discount)\n" +
                        "Total Paid: %.2f EGP\n" +
                        "Payment Method: %s\n\n" +
                        "Thank you for your order!",
                        order.getCode(),
                        result.getTxId(),
                        orderData.total,
                        redeemedPoints,
                        pointsDiscount,
                        finalAmount,
                        selectedMethod
                    );
                } else {
                    // No redemption
                    successMessage = String.format(
                        "Order placed successfully!\n\n" +
                        "Order Code: %s\n" +
                        "Payment ID: %s\n" +
                        "Total Paid: %.2f EGP\n" +
                        "Payment Method: %s\n\n" +
                        "Thank you for your order!",
                        order.getCode(),
                        result.getTxId(),
                        finalAmount,
                        selectedMethod
                    );
                }
                
                AlertHelper.showSuccess("Payment Successful", successMessage);
                
                // Navigate back to menu dashboard
                StudentNavigationService.navigateToWithData("menu-dashboard", orderData.student);
                
                // Trigger loyalty points refresh on the menu dashboard
                try {
                    Object controller = StudentNavigationService.getController("menu-dashboard");
                    if (controller instanceof MenuDashboardController) {
                        ((MenuDashboardController) controller).refreshLoyaltyPoints();
                        logger.info("Triggered loyalty points refresh on menu dashboard");
                    }
                } catch (Exception e) {
                    logger.warning("Could not refresh loyalty points: " + e.getMessage());
                }
                
            } else {
                String errorMessage = "Payment failed. Please try again.";
                if (result.getTxId() != null && !result.getTxId().isEmpty()) {
                    errorMessage += "\nReason: " + result.getTxId();
                }
                AlertHelper.showError("Payment Failed", errorMessage);
                
                // Refund loyalty points if payment failed
                if (redeemedPoints > 0) {
                    try {
                        loyaltyService.addPoints(orderData.student, redeemedPoints);
                        logger.info("Refunded " + redeemedPoints + " loyalty points due to payment failure");
                    } catch (Exception e) {
                        logger.severe("Failed to refund points: " + e.getMessage());
                    }
                }
            }
            
        } catch (Exception e) {
            logger.severe("Error processing payment: " + e.getMessage());
            e.printStackTrace();
            AlertHelper.showError("Payment Error", "Failed to process payment: " + e.getMessage());
        }
    }
    
    /**
     * Handle Back button click.
     * Return to menu dashboard.
     */
    @FXML
    private void handleBack() {
        logger.info("Back button clicked");
        
        try {
            if (orderData != null && orderData.student != null) {
                StudentNavigationService.navigateToWithData("menu-dashboard", orderData.student);
            } else {
                StudentNavigationService.navigateTo("student-login");
            }
        } catch (Exception e) {
            logger.severe("Error navigating back: " + e.getMessage());
            AlertHelper.showError("Navigation Error", "Failed to go back: " + e.getMessage());
        }
    }
}
