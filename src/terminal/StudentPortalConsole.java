package terminal;

import Core.*;
import Enums.OrderStatus;
import Interfaces.IPaymentProcessor;
import Services.*;
import Values.Discount;
import Values.Money;
import Values.Selection;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class StudentPortalConsole {
    private final StudentManager studentManager;
    private final MenuManager menuManager;
    private final OrderProcessor orderProcessor;
    private final LoyaltyProgramService loyaltyService;
    private final OrderHistoryService orderHistoryService;
    private final PaymentRegistry paymentRegistry;
    // Keep service injected and show notifications for the student
    private final NotificationHistoryService notificationHistoryService;
    private final Scanner scanner;

    private Student current;

    public StudentPortalConsole(StudentManager studentManager, MenuManager menuManager,
                                OrderProcessor orderProcessor, LoyaltyProgramService loyaltyService,
                                OrderHistoryService orderHistoryService, PaymentRegistry paymentRegistry,
                                NotificationHistoryService notificationHistoryService, Scanner scanner) {
        this.studentManager = studentManager;
        this.menuManager = menuManager;
        this.orderProcessor = orderProcessor;
        this.loyaltyService = loyaltyService;
        this.orderHistoryService = orderHistoryService;
        this.paymentRegistry = paymentRegistry;
        this.notificationHistoryService = notificationHistoryService;
        this.scanner = scanner;
    }

    public void run() {
        while (true) {
            if (current == null) {
                ConsoleUI.title("STUDENT \uD83C\uDF93");
                ConsoleUI.menuItem(1, "Login \uD83D\uDD10");
                ConsoleUI.menuItem(2, "Register \u2705");
                ConsoleUI.menuItem(0, "Back to Main Menu \u21A9\uFE0F");
                ConsoleUI.prompt("Choose:");
                int c = InputUtils.readInt(scanner);
                if (c == 0) return;
                if (c == 2) register(); else if (c == 1) login(); else ConsoleUI.error("Invalid option.");
                continue;
            }

            int unreadCount = notificationHistoryService.getUnreadNotificationCount(current.getStudentCode());
            String badge = unreadCount > 0 ? (" "+ConsoleUI.badge("\uD83D\uDD14 "+unreadCount+" new")) : "";
            ConsoleUI.title("Welcome, " + current.getName() + " | Code: " + current.getStudentCode() + badge);
            ConsoleUI.menuItem(1, "View Menu & Place Order \uD83D\uDED2");
            ConsoleUI.menuItem(2, "Check Points \u2728");
            ConsoleUI.menuItem(3, "View Order History \uD83D\uDCCB");
            ConsoleUI.menuItem(4, "View Notifications \uD83D\uDCE2" + (unreadCount > 0 ? " ("+unreadCount+" new)" : ""));
            ConsoleUI.menuItem(5, "Logout \uD83D\uDEAA");
            ConsoleUI.prompt(":");
            int c = InputUtils.readInt(scanner);
            switch (c) {
                case 1 -> placeOrderFlow();
                case 2 -> loyaltyFlow();
                case 3 -> viewOrderHistory();
                case 4 -> viewNotifications();
                case 5 -> { current = null; return; }
                default -> ConsoleUI.error("Invalid option.");
            }
        }
    }

    private void register() {
        String name = InputUtils.readLine(scanner, "Enter name: ").trim();
        String Phone_Number = InputUtils.readLine(scanner, "Enter Your Phone Number : ").trim();
        String password = InputUtils.readLine(scanner, "Enter Password: ").trim();
        String confirmPassword = InputUtils.readLine(scanner, "Confirm Password: ").trim();
        
        // Validate password match
        if (!password.equals(confirmPassword)) {
            ConsoleUI.error("Passwords do not match. Please try again.");
            return;
        }
        
        // Validate password length
        if (password.length() < 4) {
            ConsoleUI.error("Password must be at least 4 characters long.");
            return;
        }
        
        try {
            // Create student with password
            Student student = new Student();
            student.setName(name);
            student.setPhoneNumber(Phone_Number);
            student.setPassword(password);
            
            // Generate student code
            int count = studentManager.getStudentDAO().countStudents();
            String studentCode = String.format("ST%03d", count + 1);
            student.setCode(studentCode);
            
            // Save to database
            boolean saved = studentManager.getStudentDAO().Save(student);
            if (saved) {
                current = student;
                ConsoleUI.success("Registered successfully! Your student code is: " + current.getStudentCode());
                ConsoleUI.info("Please save this code for future logins.");
            } else {
                ConsoleUI.error("Registration failed. Please try again.");
            }
        } catch (Exception e) {
            ConsoleUI.error("Registration failed: " + e.getMessage());
        }
    }

    private void login() {
        String code = InputUtils.readLine(scanner, "Enter student code: ").trim();
        String password = InputUtils.readLine(scanner, "Enter password: ").trim();
        
        // Authenticate with code and password
        current = studentManager.getStudentDAO().authenticateStudent(code, password);
        
        if (current == null) {
            ConsoleUI.error("Invalid student code or password.");
        } else {
            ConsoleUI.success("Logged in successfully. Welcome, " + current.getName() + "!");
        }
    }

    private void placeOrderFlow() {
        List<MenuItem> items = menuManager.getAvailableItems();
        if (items == null || items.isEmpty()) { ConsoleUI.warn("No menu items available."); return; }
        ConsoleUI.title("MENU \uD83C\uDF7D\uFE0F");
        for (MenuItem item : items) {
            System.out.printf("ID:%d | %s | %s | %s | %s%n", item.getId(), item.getName(), item.getDescription(), item.getPrice(), item.getCategory());
        }
        List<Selection> selections = new ArrayList<>();
        while (true) {
            ConsoleUI.prompt("Enter item ID (0 to finish):");
            int id = InputUtils.readInt(scanner);
            if (id == 0) break;
            MenuItem mi = menuManager.findMenuItem(id);
            if (mi == null) { ConsoleUI.error("Not found."); continue; }
            ConsoleUI.prompt("Qty:");
            int qty = InputUtils.readInt(scanner);
            if (qty <= 0) { ConsoleUI.error("Invalid qty."); continue; }
            selections.add(new Selection(id, qty));
        }
        if (selections.isEmpty()) { ConsoleUI.warn("No items selected."); return; }
        Order order = orderProcessor.placeOrder(current, selections);
        ConsoleUI.info("Order ID: " + order.getId() + " | Total: " + order.total() + " | Status: " + order.getStatus());

        // Ask for loyalty points redemption before payment
        Money finalTotal = order.total();
        int pointsRedeemed = 0;
        Discount appliedDiscount = null;

        int currentPoints = loyaltyService.getBalance(current);
        if (currentPoints > 0) {
            ConsoleUI.info("You have " + currentPoints + " loyalty points available.");
            System.out.println("Would you like to redeem points for a discount? (y/n)");
            String response = InputUtils.readLine(scanner, "").trim().toLowerCase();

            if (response.equals("y") || response.equals("yes")) {
                System.out.println("Each point = 0.1 EGP discount");
                System.out.println("Maximum points you can use for this order: " + Math.min(currentPoints, (int)(finalTotal.getAmount().doubleValue() * 10)));
                ConsoleUI.prompt("Enter points to redeem (0 to skip):");
                pointsRedeemed = InputUtils.readInt(scanner);

                if (pointsRedeemed > 0) {
                    if (pointsRedeemed > currentPoints) {
                        ConsoleUI.error("You don't have enough points. You have " + currentPoints + " points.");
                        pointsRedeemed = 0;
                    } else {
                        double maxDiscount = finalTotal.getAmount().doubleValue();
                        double requestedDiscount = pointsRedeemed * 0.1;

                        if (requestedDiscount > maxDiscount) {
                            ConsoleUI.error("Discount cannot exceed order total. Maximum points for this order: " + (int)(maxDiscount * 10));
                            pointsRedeemed = 0;
                        } else {
                            try {
                                appliedDiscount = loyaltyService.redeem(current, pointsRedeemed);
                                finalTotal = finalTotal.subtract(new Money(appliedDiscount.getAmount()));
                                ConsoleUI.success("Applied discount: " + appliedDiscount.getAmount() + " EGP");
                                ConsoleUI.info("New order total: " + finalTotal);
                                ConsoleUI.info("Remaining points: " + current.getAccount().balance());
                            } catch (Exception e) {
                                ConsoleUI.error("Failed to redeem points: " + e.getMessage());
                                pointsRedeemed = 0;
                                appliedDiscount = null;
                            }
                        }
                    }
                }
            }
        }

        // Payment
        List<String> methods = paymentRegistry.listNames();
        if (methods.isEmpty()) { ConsoleUI.warn("No payment methods available."); return; }
        ConsoleUI.title("PAYMENT \uD83D\uDCB3");
        for (int i = 0; i < methods.size(); i++) ConsoleUI.menuItem(i+1, methods.get(i));
        int ch = InputUtils.readInt(scanner);
        if (ch <= 0 || ch > methods.size()) { ConsoleUI.error("Invalid choice."); return; }
        String methodName = methods.get(ch-1);
        IPaymentProcessor proc = paymentRegistry.get(methodName);
        String paymentId = "PAY-" + order.getCode() + "-" + System.currentTimeMillis();
        Payment payment = new Payment(paymentId, finalTotal.getAmount().doubleValue(), proc);
        PaymentResult result = orderProcessor.completeOrderWithLoyalty(order.getCode(), payment);
        ConsoleUI.prompt("Confirm Your Order Payment of " + finalTotal + " using " + methodName + "? (y/n)");
        String confirm = InputUtils.readLine(scanner, "").trim().toLowerCase();

        if (confirm.equals("y") || confirm.equals("yes")) {
            if (result.isSuccess()) {
                // Refresh current student from DB to reflect newly awarded points
                Student refreshed = studentManager.findByCode(current.getStudentCode());
                if (refreshed != null) current = refreshed;

                ConsoleUI.success("Payment successful. Tx: " + result.getTxId());
                if (appliedDiscount != null) {
                    ConsoleUI.info("Total discount applied: " + appliedDiscount.getAmount() + " EGP (" + pointsRedeemed + " points)");
                }
                ConsoleUI.info("Loyalty points updated. New balance: " + current.getAccount().balance());
                if (order.getStatus() == OrderStatus.NEW) {
                    orderProcessor.advanceStatus(order.getId(), OrderStatus.PREPARING);
                    ConsoleUI.info("Order moved to PREPARING.");
                }
            } else {
                ConsoleUI.error("Payment failed.");
                if (pointsRedeemed > 0 && appliedDiscount != null) {
                    try {
                        loyaltyService.addPoints(current, pointsRedeemed);
                        // Also refresh to stay consistent
                        Student refreshed = studentManager.findByCode(current.getStudentCode());
                        if (refreshed != null) current = refreshed;
                        ConsoleUI.warn("Points refunded due to payment failure.");
                    } catch (Exception e) {
                        ConsoleUI.warn("Warning: Could not refund points. Please contact support.");
                    }
                }
            }
        } else if (confirm.equals("n") || confirm.equals("no")) {
            ConsoleUI.warn("YOUR ORDER HAS BEEN CANCELED.");
        }
    }

    private void loyaltyFlow() {
        int bal = loyaltyService.getBalance(current);
        ConsoleUI.info("Current points: " + bal);
    }

    private void viewOrderHistory() {
        var history = orderHistoryService.getOrderHistoryForStudent(current.getStudentCode());
        if (history == null || history.isEmpty()) {
            ConsoleUI.warn("No order history found.");
            return;
        }
        ConsoleUI.title("Order History \uD83D\uDCCB");
        for (var record : history) {
            System.out.printf("Order Code: %s | Payment: %s | Status: %s | Date: %s | Total: %.2f%n",
                record.getOrderCode(), record.getPaymentMethod(), record.getOrderStatus(), String.valueOf(record.getOrderDate()), record.getTotalAmount());
        }
    }

    private void viewNotifications() {
        var notifications = notificationHistoryService.getNotificationHistoryForStudent(current.getStudentCode());

        // Fetch DB notifications (pending order updates) and mark them as read automatically
        DataBase.NotificationDAO dbNotif = new DataBase.NotificationDAO();
        var pendingDbNotifs = dbNotif.getUnreadNotifications(current.getStudentCode());

        if ((notifications == null || notifications.isEmpty()) && (pendingDbNotifs == null || pendingDbNotifs.isEmpty())) {
            ConsoleUI.warn("No notifications found.");
            return;
        }

        ConsoleUI.title("Your Notifications \uD83D\uDCE2");
        for (var notification : notifications) {
            System.out.printf("[%s] %s - %s | %s%n",
                notification.getMessageType(),
                notification.getCreatedAt().toLocalDate(),
                notification.getNotifyMessage(),
                notification.isRead() ? "READ" : "UNREAD");
        }

        ConsoleUI.divider();
        ConsoleUI.menuItem(1, "Mark all as read \u2705");
        ConsoleUI.menuItem(0, "Back \u21A9\uFE0F");
        ConsoleUI.prompt("Choose:");
        int choice = InputUtils.readInt(scanner);
        if (choice == 1) {
            boolean okHistory = notificationHistoryService.markAllNotificationsAsRead(current.getStudentCode());
            boolean okDb = dbNotif.markAllAsRead(current.getStudentCode());
            if (okHistory || okDb) {
                ConsoleUI.success("All notifications marked as read.");
            } else {
                ConsoleUI.error("Failed to mark notifications as read.");
            }
        }
    }
}
