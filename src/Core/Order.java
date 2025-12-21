package Core;

import java.util.ArrayList;
import java.util.List;
import Enums.OrderStatus;
import Values.Money;

public class Order {
    private int id;
    private String code;  // Added order code field for new database schema
    private String studentCode;
    private OrderStatus status;
    private List<OrderItem> items;

    public Order() {
        this.items = new ArrayList<>();
        this.status = OrderStatus.NEW;
    }

    public Order(String studentCode) {
        this.studentCode = studentCode;
        this.items = new ArrayList<>();
        this.status = OrderStatus.NEW;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    // Deprecated - for backward compatibility
    @Deprecated
    public int getStudentId() {
        return 0; // Legacy compatibility
    }

    @Deprecated
    public void setStudentId(int studentId) {
        // Legacy compatibility - no-op
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status != null ? status : OrderStatus.NEW;
    }

    public List<OrderItem> getItems() {
        return new ArrayList<>(items);
    }

    public void setItems(List<OrderItem> items) {
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
    }

    public void addItem(MenuItem item, int qty) {
        if (item == null || qty <= 0) {
            throw new IllegalArgumentException("Invalid item or quantity");
        }
        OrderItem orderItem = new OrderItem(item.getId(), item.getName(), item.getPrice(), qty);
        items.add(orderItem);
    }

    public Money total() {
        Money total = new Money(0);
        for (OrderItem item : items) {
            total = total.add(item.lineTotal());
        }
        return total;
    }

    public void markPreparing() {
        if (status == OrderStatus.NEW) {
            this.status = OrderStatus.PREPARING;
        } else {
            throw new IllegalStateException("Can only mark NEW orders as PREPARING");
        }
    }

    public void markReady() {
        if (status == OrderStatus.PREPARING) {
            this.status = OrderStatus.READY;
        } else {
            throw new IllegalStateException("Can only mark PREPARING orders as READY");
        }
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", studentCode='" + studentCode + '\'' +
                ", status=" + status +
                ", items=" + items.size() +
                ", total=" + total() +
                '}';
    }
}
