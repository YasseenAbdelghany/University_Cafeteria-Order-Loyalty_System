package terminal;

import Services.*;

import java.util.Scanner;

public class ServicesConsole {
    private final RoleAuthService auth;
    private final Services.StudentManager studentManager;
    private final Services.MenuManager menuManager;
    private final Services.OrderProcessor orderProcessor;
    private final Services.LoyaltyProgramService loyaltyService;
    private final Services.ReportService reportService;
    private final Services.PaymentRegistry paymentRegistry;
    private final Services.NotificationHistoryService notificationHistoryService;
    private final Services.NotificationService notificationService; // keep for backward compatibility
    private final Scanner scanner;

    public ServicesConsole(RoleAuthService auth,
                           Services.StudentManager studentManager,
                           Services.MenuManager menuManager,
                           Services.OrderProcessor orderProcessor,
                           Services.LoyaltyProgramService loyaltyService,
                           Services.ReportService reportService,
                           Services.PaymentRegistry paymentRegistry,
                           Services.NotificationService notificationService,
                           Scanner scanner) {
        this.auth = auth;
        this.studentManager = studentManager;
        this.menuManager = menuManager;
        this.orderProcessor = orderProcessor;
        this.loyaltyService = loyaltyService;
        this.reportService = reportService;
        this.paymentRegistry = paymentRegistry;
        this.notificationHistoryService = new NotificationHistoryService();
        this.notificationService = notificationService;
        this.scanner = scanner;
    }

    // Overloaded constructor to allow explicit injection of NotificationHistoryService
    public ServicesConsole(RoleAuthService auth,
                           Services.StudentManager studentManager,
                           Services.MenuManager menuManager,
                           Services.OrderProcessor orderProcessor,
                           Services.LoyaltyProgramService loyaltyService,
                           Services.ReportService reportService,
                           Services.PaymentRegistry paymentRegistry,
                           Services.NotificationHistoryService notificationHistoryService,
                           Scanner scanner) {
        this.auth = auth;
        this.studentManager = studentManager;
        this.menuManager = menuManager;
        this.orderProcessor = orderProcessor;
        this.loyaltyService = loyaltyService;
        this.reportService = reportService;
        this.paymentRegistry = paymentRegistry;
        this.notificationHistoryService = notificationHistoryService;
        this.notificationService = new NotificationService();
        this.scanner = scanner;
    }

    public void run() {
        while (true) {
            ConsoleUI.title("SERVICES \uD83E\uDDE9");
            ConsoleUI.menuItem(1, "Student Management \uD83C\uDF93");
            ConsoleUI.menuItem(2, "Menu Management \uD83C\uDF7D\uFE0F");
            ConsoleUI.menuItem(3, "Order Management \uD83D\uDED2");
            ConsoleUI.menuItem(4, "Report Service Manager \uD83D\uDCCA");
            ConsoleUI.menuItem(5, "Notification Service Manager \uD83D\uDCE2");
            ConsoleUI.menuItem(6, "Payment Services Manager \uD83D\uDCB3");
            ConsoleUI.menuItem(0, "Back \u21A9\uFE0F");
            ConsoleUI.prompt("Choose:");
            int c = InputUtils.readInt(scanner);
            switch (c) {
                case 1 -> new terminal.StudentManagerConsole(auth, studentManager, loyaltyService, scanner).run();
                case 2 -> new terminal.MenuManagerConsole(auth, menuManager, scanner).run();
                case 3 -> new terminal.OrderManagerConsole(auth, orderProcessor, scanner).run();
                case 4 -> new terminal.ReportManagerConsole(auth, reportService, scanner).run();
                case 5 -> new terminal.NotificationManagerConsole(auth, notificationHistoryService, studentManager, scanner).run();
                case 6 -> new terminal.PaymentManagerConsole(auth, paymentRegistry, scanner).run();
                case 0 -> { return; }
                default -> ConsoleUI.error("Invalid option.");
            }
        }
    }
}
