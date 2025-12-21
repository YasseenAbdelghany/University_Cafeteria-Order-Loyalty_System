package Core;

import java.time.LocalDateTime;

public class OrderHistory {
    private int id;
    private String studentName;
    private String studentCode;
    private String orderCode;
    private String paymentMethod;
    private double totalAmount;
    private LocalDateTime orderDate;
    private String orderStatus;

    public OrderHistory() {}

    public OrderHistory(String studentName, String studentCode, String orderCode,
                       String paymentMethod, double totalAmount, LocalDateTime orderDate, String orderStatus) {
        this.studentName = studentName;
        this.studentCode = studentCode;
        this.orderCode = orderCode;
        this.paymentMethod = paymentMethod;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentCode() { return studentCode; }
    public void setStudentCode(String studentCode) { this.studentCode = studentCode; }

    public String getOrderCode() { return orderCode; }
    public void setOrderCode(String orderCode) { this.orderCode = orderCode; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }

    @Override
    public String toString() {
        return String.format("Order: %s | Date: %s | Amount: %.2f EGP | Payment: %s | Status: %s",
                orderCode, orderDate.toLocalDate(), totalAmount, paymentMethod, orderStatus);
    }
}
