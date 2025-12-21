package Core;

import java.time.LocalDateTime;

public class NotificationHistory {
    private int id;
    private String studentName;
    private String studentCode;
    private String notifyMessage;
    private String messageType; // "SALE", "ORDER_READY", "GENERAL"
    private LocalDateTime createdAt;
    private boolean isRead;

    public NotificationHistory() {}

    public NotificationHistory(String studentName, String studentCode, String notifyMessage,
                               String messageType, LocalDateTime createdAt, boolean isRead) {
        this.studentName = studentName;
        this.studentCode = studentCode;
        this.notifyMessage = notifyMessage;
        this.messageType = messageType;
        this.createdAt = createdAt;
        this.isRead = isRead;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentCode() { return studentCode; }
    public void setStudentCode(String studentCode) { this.studentCode = studentCode; }

    public String getNotifyMessage() { return notifyMessage; }
    public void setNotifyMessage(String notifyMessage) { this.notifyMessage = notifyMessage; }

    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s | %s",
            messageType, createdAt.toLocalDate(), notifyMessage, isRead ? "READ" : "UNREAD");
    }
}
