package Services;

import Core.NotificationHistory;
import Core.Student;
import DataBase.NotificationHistoryDAO;
import Interfaces.INotificationHistory;
import java.time.LocalDateTime;
import java.util.List;

public class NotificationHistoryService {
    private final INotificationHistory notificationHistoryDAO;

    public NotificationHistoryService() {
        this.notificationHistoryDAO = new NotificationHistoryDAO();
    }

    public NotificationHistoryService(INotificationHistory notificationHistoryDAO) {
        this.notificationHistoryDAO = notificationHistoryDAO;
    }

    // Send sale notification to student and save to history
    public boolean sendSaleNotification(Student student, String saleMessage) {
        try {
            NotificationHistory notification = new NotificationHistory(
                student.getName(),
                student.getStudentCode(),
                saleMessage,
                "SALE",
                LocalDateTime.now(),
                false
            );
            return notificationHistoryDAO.saveNotificationHistory(notification);
        } catch (Exception e) {
            System.err.println("Error sending sale notification: " + e.getMessage());
            return false;
        }
    }

    // Send order ready notification to student and save to history
    public boolean sendOrderReadyNotification(Student student, String orderCode) {
        try {
            String message = "Your order " + orderCode + " is ready for pickup!";
            NotificationHistory notification = new NotificationHistory(
                student.getName(),
                student.getStudentCode(),
                message,
                "ORDER_READY",
                LocalDateTime.now(),
                false
            );
            return notificationHistoryDAO.saveNotificationHistory(notification);
        } catch (Exception e) {
            System.err.println("Error sending order ready notification: " + e.getMessage());
            return false;
        }
    }

    // Send general notification to student and save to history
    public boolean sendGeneralNotification(Student student, String message) {
        try {
            NotificationHistory notification = new NotificationHistory(
                student.getName(),
                student.getStudentCode(),
                message,
                "GENERAL",
                LocalDateTime.now(),
                false
            );
            return notificationHistoryDAO.saveNotificationHistory(notification);
        } catch (Exception e) {
            System.err.println("Error sending general notification: " + e.getMessage());
            return false;
        }
    }

    // Get all notifications for a student
    public List<NotificationHistory> getNotificationHistoryForStudent(String studentCode) {
        return notificationHistoryDAO.getNotificationHistoryForStudent(studentCode);
    }

    // Get unread notifications for a student
    public List<NotificationHistory> getUnreadNotificationsForStudent(String studentCode) {
        return notificationHistoryDAO.getUnreadNotificationsForStudent(studentCode);
    }

    // Mark notification as read
    public boolean markNotificationAsRead(int notificationId) {
        return notificationHistoryDAO.markNotificationAsRead(notificationId);
    }

    // Mark all notifications as read for a student
    public boolean markAllNotificationsAsRead(String studentCode) {
        return notificationHistoryDAO.markAllNotificationsAsRead(studentCode);
    }

    // Get unread notification count for a student
    public int getUnreadNotificationCount(String studentCode) {
        return notificationHistoryDAO.getUnreadNotificationCount(studentCode);
    }

    // Get all notification history (for admin use)
    public List<NotificationHistory> getAllNotificationHistory() {
        return notificationHistoryDAO.getAllNotificationHistory();
    }

    /**
     * Send a notification to a student with custom message and type.
     * This is a generic method that can be used by admin controllers.
     * 
     * @param studentCode The student code
     * @param message The notification message
     * @param messageType The message type (SALE, ORDER_READY, GENERAL)
     * @return true if notification was sent successfully
     */
    public boolean sendNotification(String studentCode, String message, String messageType) {
        try {
            // We need the student name, so we'll use a placeholder or fetch it
            // For now, use student code as name (can be improved later)
            NotificationHistory notification = new NotificationHistory(
                studentCode, // Using student code as name temporarily
                studentCode,
                message,
                messageType,
                LocalDateTime.now(),
                false
            );
            return notificationHistoryDAO.saveNotificationHistory(notification);
        } catch (Exception e) {
            System.err.println("Error sending notification: " + e.getMessage());
            return false;
        }
    }
}
