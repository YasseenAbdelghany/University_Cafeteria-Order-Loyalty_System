package DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class NotificationDAO {
    private static final Logger logger = Logger.getLogger(NotificationDAO.class.getName());
    private final Connection connection;

    public NotificationDAO() {
        DBconnection db = new DBconnection();
        this.connection = db.getConnection();
    }

    public NotificationDAO(Connection connection) {
        this.connection = connection;
    }

    public boolean sendNotification(String userCode, String message, String notificationType) {
        String sql = "INSERT INTO notifications (user_code, message, notification_type) VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE message = ?, notification_type = ?, is_read = FALSE, created_at = CURRENT_TIMESTAMP";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userCode);
            ps.setString(2, message);
            ps.setString(3, notificationType);
            // update params
            ps.setString(4, message);
            ps.setString(5, notificationType);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to send notification to user: " + userCode, e);
            return false;
        }
    }

    public List<String> getUnreadNotifications(String userCode) {
        String sql = "SELECT id, message, notification_type, created_at FROM notifications WHERE user_code = ? AND is_read = FALSE ORDER BY created_at DESC";
        List<String> notifications = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userCode);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String notification = String.format("[%s] %s - %s",
                            rs.getString("notification_type"),
                            rs.getString("message"),
                            rs.getTimestamp("created_at"));
                    notifications.add(notification);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to get unread notifications for user: " + userCode, e);
        }
        return notifications;
    }

    public List<String> getAllNotifications(String userCode) {
        String sql = "SELECT id, message, notification_type, is_read, created_at FROM notifications WHERE user_code = ? ORDER BY created_at DESC";
        List<String> notifications = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userCode);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String status = rs.getBoolean("is_read") ? "READ" : "UNREAD";
                    String notification = String.format("[%s] %s - %s (%s)",
                            rs.getString("notification_type"),
                            rs.getString("message"),
                            rs.getTimestamp("created_at"),
                            status);
                    notifications.add(notification);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to get all notifications for user: " + userCode, e);
        }
        return notifications;
    }

    public boolean markAsRead(String userCode, int notificationId) {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE user_code = ? AND id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userCode);
            ps.setInt(2, notificationId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to mark notification as read for user: " + userCode, e);
            return false;
        }
    }

    public boolean markAllAsRead(String userCode) {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE user_code = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userCode);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to mark all notifications as read for user: " + userCode, e);
            return false;
        }
    }

    public int getUnreadCount(String userCode) {
        String sql = "SELECT COUNT(*) FROM notifications WHERE user_code = ? AND is_read = FALSE";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to get unread count for user: " + userCode, e);
        }
        return 0;
    }
}
