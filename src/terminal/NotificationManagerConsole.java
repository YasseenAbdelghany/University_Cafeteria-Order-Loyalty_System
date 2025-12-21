package terminal;

import Core.Student;
import Services.NotificationHistoryService;
import Services.NotificationService;
import Services.RoleAuthService;
import Services.StudentManager;

import java.util.List;
import java.util.Scanner;

    public class NotificationManagerConsole {
    private final RoleAuthService auth;
    private final NotificationHistoryService notifications;
    private final StudentManager studentManager;
    private final Scanner scanner;

    // Primary constructor: uses DB-backed NotificationHistoryService
    public NotificationManagerConsole(RoleAuthService auth,
                                      NotificationHistoryService notifications,
                                      StudentManager studentManager,
                                      Scanner scanner) {
        this.auth = auth;
        this.notifications = notifications;
        this.studentManager = studentManager;
        this.scanner = scanner;
    }

    // Backward-compatible overload: accept old NotificationService and delegate to a fresh NotificationHistoryService
    public NotificationManagerConsole(RoleAuthService auth,
                                      NotificationService oldNotifications,
                                      StudentManager studentManager,
                                      Scanner scanner) {
        this(auth, new NotificationHistoryService(), studentManager, scanner);
    }

    public void run() {
        if (!login("NOTIFICATION")) return;
        while (true) {
            System.out.println("\n=== NOTIFICATIONS ===");
            System.out.println("1. Notify Student\n2. Broadcast to All Students\n0. Logout");
            int c = InputUtils.readInt(scanner);
            switch (c) {
                case 1 -> notifyOne();
                case 2 -> broadcast();
                case 0 -> { return; }
                default -> System.out.println("Invalid");
            }
        }
    }

    private boolean login(String role) {
        System.out.print("Username: ");
        String u = scanner.nextLine().trim();
        System.out.print("Password: ");
        String p = scanner.nextLine().trim();
        if (auth.login(role, u, p)) {
            System.out.println("✓ Logged in.");
            return true;
        }
        System.out.println("✗ Access denied.");
        return false;
    }

    private void notifyOne() {
        System.out.print("Student ID: ");
        int id = InputUtils.readInt(scanner);
        Student st = findStudentById(id);
        if (st == null) { System.out.println("Student not found."); return; }
        System.out.print("Message: ");
        String msg = scanner.nextLine().trim();
        boolean ok = notifications.sendGeneralNotification(st, msg);
        System.out.println(ok ? "✓ Notification saved to history." : "✗ Failed to save notification.");
    }

    private void broadcast() {
        System.out.print("Announcement message: ");
        String msg = scanner.nextLine().trim();
        List<Student> all = studentManager.listAll();
        int okCnt = 0;
        for (Student st : all) {
            if (st != null && st.getStudentCode() != null) {
                if (notifications.sendGeneralNotification(st, msg)) okCnt++;
            }
        }
        System.out.println("✓ Saved to history for " + okCnt + " students.");
    }

    private Student findStudentById(int id) {
        for (Student s : studentManager.listAll()) { if (s.getId() == id) return s; }
        return null;
    }
}
