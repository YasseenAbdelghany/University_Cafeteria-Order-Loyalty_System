package DataBase;

import Core.NotificationHistory;
import Interfaces.INotificationHistory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationHistoryDAO implements INotificationHistory {
    private final Connection conn;

    public NotificationHistoryDAO() {
        DBconnection db = new DBconnection();
        this.conn = db.getConnection();
    }

    public NotificationHistoryDAO(Connection conn) {
        this.conn = conn;
    }

    @Override
    public boolean saveNotificationHistory(NotificationHistory notification) {
        String sql = "INSERT INTO notification_history (student_name, student_code, notify_message, message_type, created_at, is_read) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, notification.getStudentName());
            stmt.setString(2, notification.getStudentCode());
            stmt.setString(3, notification.getNotifyMessage());
            stmt.setString(4, notification.getMessageType());
            stmt.setTimestamp(5, Timestamp.valueOf(notification.getCreatedAt()));
            stmt.setBoolean(6, notification.isRead());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<NotificationHistory> getNotificationHistoryForStudent(String studentCode) {
        String sql = "SELECT * FROM notification_history WHERE student_code = ? ORDER BY created_at DESC";
        List<NotificationHistory> list = new ArrayList<>();
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
    public List<NotificationHistory> getUnreadNotificationsForStudent(String studentCode) {
        String sql = "SELECT * FROM notification_history WHERE student_code = ? AND is_read = FALSE ORDER BY created_at DESC";
        List<NotificationHistory> list = new ArrayList<>();
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
    public boolean markNotificationAsRead(int notificationId) {
        String sql = "UPDATE notification_history SET is_read = TRUE WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, notificationId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean markAllNotificationsAsRead(String studentCode) {
        String sql = "UPDATE notification_history SET is_read = TRUE WHERE student_code = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentCode);
            return stmt.executeUpdate() >= 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int getUnreadNotificationCount(String studentCode) {
        String sql = "SELECT COUNT(*) FROM notification_history WHERE student_code = ? AND is_read = FALSE";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public List<NotificationHistory> getAllNotificationHistory() {
        String sql = "SELECT * FROM notification_history ORDER BY created_at DESC";
        List<NotificationHistory> list = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private NotificationHistory mapRow(ResultSet rs) throws SQLException {
        NotificationHistory notification = new NotificationHistory();
        notification.setId(rs.getInt("id"));
        notification.setStudentName(rs.getString("student_name"));
        notification.setStudentCode(rs.getString("student_code"));
        notification.setNotifyMessage(rs.getString("notify_message"));
        notification.setMessageType(rs.getString("message_type"));
        notification.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        notification.setRead(rs.getBoolean("is_read"));
        return notification;
    }
}
