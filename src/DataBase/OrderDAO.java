package DataBase;

import Core.Order;
import Core.OrderItem;
import Enums.OrderStatus;
import Enums.Currency;
import Values.Money;
import Interfaces.IOrderRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class OrderDAO implements IOrderRepository {
    private static final Logger logger = Logger.getLogger(OrderDAO.class.getName());
    private final Connection connection;

    public OrderDAO() {
        DBconnection db = new DBconnection();
        this.connection = db.getConnection();
    }

    public OrderDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Order order) {
        // Generate order code if not set
        if (order.getCode() == null || order.getCode().isEmpty()) {
            order.setCode(generateOrderCode());
        }

        String sql = "INSERT INTO orders (code, student_code, status, total_amount) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, order.getCode());
            ps.setString(2, order.getStudentCode());
            ps.setString(3, order.getStatus().name());
            ps.setDouble(4, order.total().getAmount().doubleValue());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    order.setId(rs.getInt(1));
                    saveOrderItems(order);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to save order for student: " + order.getStudentCode(), e);
        }
    }

    @Override
    public Order findById(int id) {
        String sql = "SELECT id, code, student_code, status, total_amount FROM orders WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Order order = mapOrder(rs);
                    loadOrderItems(order);
                    return order;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to find order: " + id, e);
        }
        return null;
    }

    public Order findByCode(String code) {
        String sql = "SELECT id, code, student_code, status, total_amount FROM orders WHERE code = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Order order = mapOrder(rs);
                    loadOrderItems(order);
                    return order;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to find order by code: " + code, e);
        }
        return null;
    }

    @Override
    public List<Order> findPending() {
        String sql = "SELECT id, code, student_code, status, total_amount FROM orders WHERE status IN ('NEW', 'PREPARING')";
        List<Order> orders = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Order order = mapOrder(rs);
                loadOrderItems(order);
                orders.add(order);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to find pending orders", e);
        }
        return orders;
    }

    public List<Order> findByStudentCode(String studentCode) {
        String sql = "SELECT id, code, student_code, status, total_amount FROM orders WHERE student_code = ?";
        List<Order> orders = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, studentCode);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order order = mapOrder(rs);
                    loadOrderItems(order);
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to find orders for student: " + studentCode, e);
        }
        return orders;
    }

    @Override
    @Deprecated
    public List<Order> findByStudentId(int studentId) {
        // Legacy compatibility - return empty list
        return new ArrayList<>();
    }

    @Override
    public void update(Order order) {
        String sql = "UPDATE orders SET status = ?, total_amount = ? WHERE code = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, order.getStatus().name());
            ps.setDouble(2, order.total().getAmount().doubleValue());
            ps.setString(3, order.getCode());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to update order: " + order.getCode(), e);
        }
    }

    @Override
    public List<Order> findAll() {
        String sql = "SELECT id, code, student_code, status, total_amount FROM orders";
        List<Order> orders = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Order order = mapOrder(rs);
                loadOrderItems(order);
                orders.add(order);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to list all orders", e);
        }
        return orders;
    }

    private void saveOrderItems(Order order) {
        String sql = "INSERT INTO order_items (order_code, menu_item_id, name_snapshot, unit_price, unit_currency, quantity) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (OrderItem item : order.getItems()) {
                ps.setString(1, order.getCode());
                ps.setInt(2, item.getMenuItemId());
                ps.setString(3, item.getNameSnapshot());
                ps.setDouble(4, item.getUnitPrice().getAmount().doubleValue());
                ps.setString(5, item.getUnitCurrency().name());
                ps.setInt(6, item.getQty());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to save order items for order: " + order.getCode(), e);
        }
    }

    private void loadOrderItems(Order order) {
        String sql = "SELECT menu_item_id, name_snapshot, unit_price, unit_currency, quantity FROM order_items WHERE order_code = ?";
        List<OrderItem> items = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, order.getCode());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setMenuItemId(rs.getInt("menu_item_id"));
                    item.setNameSnapshot(rs.getString("name_snapshot"));

                    // Handle unit_currency - default to EGP if null
                    String currencyStr = rs.getString("unit_currency");
                    Currency currency = Currency.EGP;
                    if (currencyStr != null) {
                        try {
                            currency = Currency.valueOf(currencyStr);
                        } catch (IllegalArgumentException e) {
                            currency = Currency.EGP;
                        }
                    }
                    item.setUnitCurrency(currency);
                    item.setUnitPrice(new Money(rs.getDouble("unit_price"), currency));
                    item.setQty(rs.getInt("quantity"));
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to load order items for order: " + order.getCode(), e);
        }
        order.setItems(items);
    }

    private Order mapOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getInt("id"));
        order.setCode(rs.getString("code"));
        order.setStudentCode(rs.getString("student_code"));
        order.setStatus(OrderStatus.valueOf(rs.getString("status")));
        return order;
    }

    private String generateOrderCode() {
        // Generate a unique order code like ORD-20250829-001
        String datePart = java.time.LocalDate.now().toString().replace("-", "");
        String sql = "SELECT COUNT(*) FROM orders WHERE code LIKE ? AND DATE(created_at) = CURDATE()";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "ORD-" + datePart + "-%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1) + 1;
                    return String.format("ORD-%s-%03d", datePart, count);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Failed to generate order code, using timestamp", e);
        }
        return "ORD-" + System.currentTimeMillis();
    }
}
