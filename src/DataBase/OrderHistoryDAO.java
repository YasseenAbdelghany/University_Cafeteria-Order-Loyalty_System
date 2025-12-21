package DataBase;

import Core.OrderHistory;
import Interfaces.IOrderHistory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderHistoryDAO implements IOrderHistory {
    private final Connection conn;

    public OrderHistoryDAO() {
        DBconnection db = new DBconnection();
        this.conn = db.getConnection();
    }

    public OrderHistoryDAO(Connection conn) {
        this.conn = conn;
    }

    @Override
    public boolean saveOrderHistory(OrderHistory history) {
        String sql = "INSERT INTO order_history (student_name, student_code, order_code, payment_method, total_amount, order_date, order_status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, history.getStudentName());
            stmt.setString(2, history.getStudentCode());
            stmt.setString(3, history.getOrderCode());
            stmt.setString(4, history.getPaymentMethod());
            stmt.setDouble(5, history.getTotalAmount());
            stmt.setTimestamp(6, Timestamp.valueOf(history.getOrderDate()));
            stmt.setString(7, history.getOrderStatus());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<OrderHistory> getOrderHistoryByStudent(String studentCode) {
        String sql = "SELECT * FROM order_history WHERE student_code = ? ORDER BY order_date DESC";
        List<OrderHistory> list = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentCode);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean updateOrderStatus(String orderCode, String newStatus) {
        String sql = "UPDATE order_history SET order_status = ? WHERE order_code = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            stmt.setString(2, orderCode);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public OrderHistory getOrderHistoryByOrderCode(String orderCode) {
        String sql = "SELECT * FROM order_history WHERE order_code = ? ORDER BY order_date DESC LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, orderCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<OrderHistory> getAllOrderHistory() {
        String sql = "SELECT * FROM order_history ORDER BY order_date DESC";
        List<OrderHistory> list = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private OrderHistory mapRow(ResultSet rs) throws SQLException {
        OrderHistory h = new OrderHistory();
        h.setId(rs.getInt("id"));
        h.setStudentName(rs.getString("student_name"));
        h.setStudentCode(rs.getString("student_code"));
        h.setOrderCode(rs.getString("order_code"));
        h.setPaymentMethod(rs.getString("payment_method"));
        h.setTotalAmount(rs.getDouble("total_amount"));
        Timestamp ts = rs.getTimestamp("order_date");
        h.setOrderDate(ts != null ? ts.toLocalDateTime() : null);
        h.setOrderStatus(rs.getString("order_status"));
        return h;
    }
}
