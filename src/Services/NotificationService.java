package Services;

import Interfaces.INotifier;
import DataBase.NotificationDAO;
import java.util.List;
import java.util.ArrayList;

public class NotificationService implements INotifier {
    private final NotificationDAO notificationDAO;

    public NotificationService() {
        this.notificationDAO = new NotificationDAO();
    }

    public NotificationService(NotificationDAO notificationDAO) {
        this.notificationDAO = notificationDAO;
    }

    @Override
    public void notify(int userId, String message) {
        if (message == null || message.trim().isEmpty()) {
            return;
        }

        // Convert userId to userCode format for database storage
        String userCode = "USER_" + userId;

        // Store in database instead of memory
        boolean success = notificationDAO.sendNotification(userCode, message.trim(), "GENERAL");

        if (success) {
            // In a real system, this would send SMS, email, or push notification
            System.out.println("Notification sent to user " + userId + ": " + message);
        } else {
            System.err.println("Failed to store notification for user " + userId);
        }
    }

    public void broadcast(List<Integer> userIds, String message) {
        if (userIds == null || userIds.isEmpty() || message == null || message.isBlank()) return;
        for (Integer id : userIds) {
            if (id != null) notify(id, message);
        }
    }

    public List<String> getNotifications(int userId) {
        String userCode = "USER_" + userId;
        return notificationDAO.getUnreadNotifications(userCode);
    }

    public void clearNotifications(int userId) {
        String userCode = "USER_" + userId;
        notificationDAO.markAllAsRead(userCode);
    }

    // Additional methods for enhanced functionality
    public void notifyWithType(int userId, String message, String notificationType) {
        if (message == null || message.trim().isEmpty()) {
            return;
        }

        String userCode = "USER_" + userId;
        boolean success = notificationDAO.sendNotification(userCode, message.trim(), notificationType);

        if (success) {
            System.out.println("Notification (" + notificationType + ") sent to user " + userId + ": " + message);
        } else {
            System.err.println("Failed to store notification for user " + userId);
        }
    }

    public void broadcastWithType(List<Integer> userIds, String message, String notificationType) {
        if (userIds == null || userIds.isEmpty() || message == null || message.isBlank()) return;
        for (Integer id : userIds) {
            if (id != null) notifyWithType(id, message, notificationType);
        }
    }
}
