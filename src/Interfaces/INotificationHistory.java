package Interfaces;

import Core.NotificationHistory;
import java.util.List;

public interface INotificationHistory {
    boolean saveNotificationHistory(NotificationHistory notification);
    List<NotificationHistory> getNotificationHistoryForStudent(String studentCode);
    List<NotificationHistory> getUnreadNotificationsForStudent(String studentCode);
    boolean markNotificationAsRead(int notificationId);
    boolean markAllNotificationsAsRead(String studentCode);
    int getUnreadNotificationCount(String studentCode);
    List<NotificationHistory> getAllNotificationHistory();
}
