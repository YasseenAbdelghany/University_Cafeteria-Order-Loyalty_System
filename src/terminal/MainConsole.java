package terminal;

import java.util.Scanner;

/**
 * Simple orchestrator for the CLI menus.
 */
public class MainConsole {
    private final Scanner scanner;

    // Services
    private final ServiceContainer container;

    public MainConsole(ServiceContainer container, Scanner scanner) {
        this.container = container;
        this.scanner = scanner;
    }

    public void run() {
        while (true) {
            ConsoleUI.title("MAIN MENU ✨");
            ConsoleUI.menuItem(1, "Student \uD83C\uDF93");
            ConsoleUI.menuItem(2, "Admin \uD83D\uDEE0\uFE0F");
            ConsoleUI.menuItem(3, "Services \uD83E\uDDE9");
            ConsoleUI.menuItem(4, "IT ADMIN \uD83D\uDC64");
            ConsoleUI.menuItem(0, "Exit \uD83D\uDEAA");
            ConsoleUI.prompt("Choose option:");

            int choice = InputUtils.readInt(scanner);
            switch (choice) {
                case 1 -> new terminal.StudentPortalConsole(
                        container.getStudentManager(),
                        container.getMenuManager(),
                        container.getOrderProcessor(),
                        container.getLoyaltyService(),
                        container.getOrderHistoryService(),
                        container.getPaymentRegistry(),
                        container.getNotificationHistoryService(),
                        scanner
                ).run();
                case 2 -> new terminal.AdminConsole(
                        container.getAdminAuthService(),
                        container.getReportService(),
                        container.getRoleAuthService(),
                        container.getAdminManagementServices(),
                        scanner
                ).run();
                case 3 -> new terminal.ServicesConsole(
                        container.getRoleAuthService(),
                        container.getStudentManager(),
                        container.getMenuManager(),
                        container.getOrderProcessor(),
                        container.getLoyaltyService(),
                        container.getReportService(),
                        container.getPaymentRegistry(),
                        container.getNotificationHistoryService(),
                        scanner
                ).run();
                case 4 -> new terminal.UserAdminConsole(
                        container.getRoleAuthService(),
                        container.getAdminManager(),
                        scanner
                ).run();
                case 0 -> { ConsoleUI.info("Goodbye! \uD83D\uDC4B"); return; }
                default -> ConsoleUI.error("Invalid option.");
            }
        }
    }
}
