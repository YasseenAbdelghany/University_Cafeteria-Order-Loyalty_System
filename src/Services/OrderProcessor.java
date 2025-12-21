package Services;

import Core.*;
import DataBase.NotificationDAO;
import Interfaces.*;
import Enums.OrderStatus;
import Values.Selection;

import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class OrderProcessor {
    private static final Logger logger = Logger.getLogger(OrderProcessor.class.getName());

    private final IOrderRepository orders;
    private final IMenuProvider menu;
    private final LoyaltyProgramService loyalty;
    private final OrderHistoryService orderHistoryService;
    private final NotificationHistoryService notificationHistoryService;
    private final NotificationDAO notificationDAO;

    public OrderProcessor(IOrderRepository orders, IMenuProvider menu, LoyaltyProgramService loyalty) {
        this.orders = orders;
        this.menu = menu;
        this.loyalty = loyalty;
        this.orderHistoryService = null; // backward compatible
        this.notificationHistoryService = null; // backward compatible
        this.notificationDAO = new NotificationDAO();
    }

    public OrderProcessor(IOrderRepository orders, IMenuProvider menu, LoyaltyProgramService loyalty, OrderHistoryService orderHistoryService) {
        this.orders = orders;
        this.menu = menu;
        this.loyalty = loyalty;
        this.orderHistoryService = orderHistoryService;
        this.notificationHistoryService = null; // backward compatible
        this.notificationDAO = new NotificationDAO();
    }

    public OrderProcessor(IOrderRepository orders, IMenuProvider menu, LoyaltyProgramService loyalty,
                          OrderHistoryService orderHistoryService, NotificationHistoryService notificationHistoryService) {
        this.orders = orders;
        this.menu = menu;
        this.loyalty = loyalty;
        this.orderHistoryService = orderHistoryService;
        this.notificationHistoryService = notificationHistoryService;
        this.notificationDAO = new NotificationDAO();
    }

    public Order placeOrder(Student student, List<Selection> selections) {
        if (student == null || selections == null || selections.isEmpty()) {
            throw new IllegalArgumentException("Invalid student or selections");
        }

        Order order = new Order(student.getStudentCode());

        // Add items to order
        for (Selection selection : selections) {
            MenuItem item = menu.findById(selection.getItemId());
            if (item != null) {
                order.addItem(item, selection.getQty());
            } else {
                logger.log(Level.WARNING, "Menu item not found: " + selection.getItemId());
            }
        }

        // Save order - this will generate the order code
        orders.save(order);

        // Record an ORDER_STATUS notification for the student (code + status only)
        try {

            String concise = String.format("Order: %s | Status: %s", order.getCode(), order.getStatus());
            notificationDAO.sendNotification(student.getStudentCode(), concise, "ORDER_STATUS");
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to insert order notification", e);
        }

        logger.log(Level.INFO, "Order placed: " + order.getCode() + " for student: " + student.getStudentCode());
        return order;
    }

    public PaymentResult handlePayment(Payment payment) {
        if (payment == null) {
            return new PaymentResult(false, "Invalid payment");
        }

        try {
            boolean paymentSuccess = payment.getProcessor().processPayment(payment.getAmount());
            return new PaymentResult(paymentSuccess, paymentSuccess ? payment.getPaymentID() : null);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Payment processing failed", e);
            return new PaymentResult(false, "Payment processing error: " + e.getMessage());
        }
    }

    private String toPaymentMethodEnum(IPaymentProcessor p) {
        // Must match DB enum: ENUM('CASH','CREDIT_CARD','MOBILE_WALLET')
        if (p == null) return "CASH";
        if (p instanceof CashPaymentProcessor) return "CASH";
        if (p instanceof CreditCardPaymentProcessor) return "CREDIT_CARD";
        if (p instanceof MobileWalletPaymentProcessor) return "MOBILE_WALLET";
        if (p instanceof SimpleNamedPaymentProcessor) {
            String name = ((SimpleNamedPaymentProcessor) p).getName();
            if (name == null) return "CASH";
            String up = name.trim().toUpperCase().replace(' ', '_');
            if (up.equals("CASH") || up.equals("CREDIT_CARD") || up.equals("MOBILE_WALLET")) return up;
        }
        return "CASH";
    }

    public void advanceStatus(int orderId, OrderStatus newStatus) {
        Order order = orders.findById(orderId);
        if (order == null) {
            logger.log(Level.WARNING, "Order not found: " + orderId);
            return;
        }
        advanceStatusByCode(order.getCode(), newStatus);
    }

    public void advanceStatusByCode(String orderCode, OrderStatus newStatus) {
        Order order = ((DataBase.OrderDAO) orders).findByCode(orderCode);
        if (order == null) {
            logger.log(Level.WARNING, "Order not found: " + orderCode);
            return;
        }

        try {
            order.setStatus(newStatus);
            orders.update(order);

            // Update order status in history if service is available
            if (orderHistoryService != null) {
                orderHistoryService.updateOrderStatus(orderCode, newStatus.toString());
            }

            // Persist to per-student notification history if READY
            if (newStatus == OrderStatus.READY && notificationHistoryService != null) {
                Student student = findStudentByCode(order.getStudentCode());
                if (student != null) {
                    notificationHistoryService.sendOrderReadyNotification(student, orderCode);
                }
            }

            // Also record/update concise ORDER_STATUS notification in DB so student sees current state
            try {
                String concise = String.format("Order: %s | Status: %s", order.getCode(), order.getStatus());
                notificationDAO.sendNotification(order.getStudentCode(), concise, "ORDER_STATUS");
            } catch (Exception e) {
                logger.log(Level.WARNING, "Failed to update DB notification for status change", e);
            }

            logger.log(Level.INFO, "Order status updated: " + orderCode + " -> " + newStatus);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to advance order status", e);
        }
    }

    public PaymentResult completeOrderWithLoyalty(String orderCode, Payment payment) {
        Order order = ((DataBase.OrderDAO) orders).findByCode(orderCode);
        if (order == null) {
            logger.log(Level.WARNING, "Order not found for loyalty completion: " + orderCode);
            return new PaymentResult(false, "Order not found: " + orderCode);
        }

        try {
            // Process payment
            PaymentResult result = handlePayment(payment);

            if (result.isSuccess()) {
                // Award loyalty points for successful payment
                Student student = findStudentByCode(order.getStudentCode());
                if (student != null) {
                    loyalty.awardPoints(student, order.total(), orderCode, "Points earned from order " + orderCode);

                    // Record order in history if service is available
                    if (orderHistoryService != null) {
                        String paymentMethod = getPaymentMethodName(payment.getProcessor());
                        orderHistoryService.recordOrderInHistory(order, student, paymentMethod, payment.getAmount());
                        logger.log(Level.INFO, "Order recorded in history: " + orderCode);
                    }

                    // Student-facing loyalty notification via notification history (not DB notifications)
                    if (notificationHistoryService != null) {
                        int pointsEarned = (int) Math.floor(order.total().getAmount().doubleValue() / 10.0);
                        if (pointsEarned > 0) {
                            String message = String.format("Earned %d loyalty points from order %s. New balance: %d points",
                                                          pointsEarned, orderCode, student.getAccount().balance());
                            notificationHistoryService.sendGeneralNotification(student, message);
                        }
                    }
                }

                logger.log(Level.INFO, "Order completed with loyalty points: " + orderCode);
            }
            return result;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to complete order with loyalty", e);
            return new PaymentResult(false, "Completion error: " + e.getMessage());
        }
    }

    // Add missing method for OrderManagerConsole compatibility
    public List<Order> trackPendingOrders() {
        return orders.findPending();
    }

    // Helper method to get payment method name for history
    private String getPaymentMethodName(IPaymentProcessor processor) {
        if (processor instanceof CashPaymentProcessor) return "Cash";
        if (processor instanceof CreditCardPaymentProcessor) return "Credit Card";
        if (processor instanceof MobileWalletPaymentProcessor) return "Mobile Wallet";
        if (processor instanceof SimpleNamedPaymentProcessor) {
            return ((SimpleNamedPaymentProcessor) processor).getName();
        }
        return "Unknown";
    }

    // Helper method to extract order code from payment ID
    private String extractOrderCodeFromPaymentId(String paymentId) {
        if (paymentId == null) return null;
        String[] parts = paymentId.split("-");
        // Supported formats:
        // 1) ORD-YYYYMMDD-SEQ-<ts>
        // 2) PAY-ORD-YYYYMMDD-SEQ-<ts>
        if (parts.length >= 4 && parts[0].equals("ORD")) {
            // ORD-YYYYMMDD-SEQ-<ts>
            return parts[0] + "-" + parts[1] + "-" + parts[2];
        }
        if (parts.length >= 5 && parts[0].equals("PAY") && parts[1].equals("ORD")) {
            // PAY-ORD-YYYYMMDD-SEQ-<ts>
            return parts[1] + "-" + parts[2] + "-" + parts[3];
        }
        return null;
    }

    // Helper method to find student by code (would need to inject StudentRepository)
    private Student findStudentByCode(String studentCode) {
        try {
            DataBase.StudentDAO studentDAO = new DataBase.StudentDAO();
            return studentDAO.FindByCode(studentCode);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Could not find student: " + studentCode, e);
            return null;
        }
    }
}
