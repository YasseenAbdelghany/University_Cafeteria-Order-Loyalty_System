package terminal;

import Services.*;
import Enums.ManagerType;
import ServiceManagers.*;
import Core.Admin;

import java.util.Map;
import java.util.Scanner;

public class AdminConsole {
    private final AdminLIN_Out adminAuthService;
    private final ReportService reportService;
    private final RoleAuthService roleAuthService;
    private final AdminManagement_Services adminManagementServices;
    private final Scanner scanner;

    public AdminConsole(AdminLIN_Out adminAuthService,
                        ReportService reportService,
                        RoleAuthService roleAuthService,
                        AdminManagement_Services adminManagementServices,
                        Scanner scanner) {
        this.adminAuthService = adminAuthService;
        this.reportService = reportService;
        this.roleAuthService = roleAuthService;
        this.adminManagementServices = adminManagementServices;
        this.scanner = scanner;
    }

    public void run() {
        ConsoleUI.title("ADMIN LOGIN \uD83D\uDD10");
        Admin admin = adminAuthService.performLogin(scanner);
        if (admin == null) return;

        while (true) {
            ConsoleUI.title("ADMIN DASHBOARD \uD83D\uDEE0\uFE0F");
            ConsoleUI.menuItem(1, "Manage Service Managers \uD83D\uDC65");
            ConsoleUI.menuItem(2, "System Reports \uD83D\uDCCA");
            ConsoleUI.menuItem(0, "Logout \uD83D\uDEAA");
            ConsoleUI.prompt("Choose:");
            int c = InputUtils.readInt(scanner);
            switch (c) {
                case 1 -> manageServiceManagers();
                case 2 -> showReports();
                case 0 -> {
                    adminAuthService.performLogout(admin);
                    return;
                }
                default -> ConsoleUI.error("Invalid option.");
            }
        }
    }

    private void manageServiceManagers() {
        while (true) {
            ConsoleUI.title("SERVICE MANAGER ADMINISTRATION \uD83D\uDCBC");
            ConsoleUI.menuItem(1, "Add Manager ➕");
            ConsoleUI.menuItem(2, "Update Manager \u270F\uFE0F");
            ConsoleUI.menuItem(3, "Delete Manager \u274C");
            ConsoleUI.menuItem(4, "View Managers by Type \uD83D\uDCCB");
            ConsoleUI.menuItem(5, "Find Manager \uD83D\uDD0D");
            ConsoleUI.menuItem(6, "Manager Statistics \uD83D\uDCCA");
            ConsoleUI.menuItem(0, "Back to Admin Dashboard \u21A9\uFE0F");
            ConsoleUI.prompt("Choose:");

            int choice = InputUtils.readInt(scanner);
            switch (choice) {
                case 1 -> addManager();
                case 2 -> updateManager();
                case 3 -> deleteManager();
                case 4 -> viewManagersByType();
                case 5 -> findManager();
                case 6 -> showManagerStatistics();
                case 0 -> { return; }
                default -> ConsoleUI.error("Invalid option.");
            }
        }
    }

    private void addManager() {
        ConsoleUI.title("ADD NEW MANAGER ➕");
        ManagerType type = selectManagerType();
        if (type == null) return;

        ServicesManager manager = createManagerFromInput(type);
        if (manager == null) return;

        try {
            boolean success = adminManagementServices.addManager(type, manager);
            if (success) {
                ConsoleUI.success("Manager added successfully!");
                System.out.println("Manager ID: " + manager.getId());
            } else {
                ConsoleUI.error("Failed to add manager. Please try again.");
            }
        } catch (Exception e) {
            ConsoleUI.error("Error: " + e.getMessage());
        }
    }

    private void updateManager() {
        ConsoleUI.title("UPDATE MANAGER \u270F\uFE0F");
        ManagerType type = selectManagerType();
        if (type == null) return;

        ConsoleUI.prompt("Enter username of manager to update:");
        String username = scanner.nextLine().trim();
        if (username.isEmpty()) { ConsoleUI.error("Username cannot be empty."); return; }

        ServicesManager existingManager = adminManagementServices.findManager(type, username);
        if (existingManager == null) { ConsoleUI.warn("Manager not found."); return; }

        System.out.println("Current manager details:");
        displayManager(existingManager);

        System.out.println("\nEnter new details:");
        ServicesManager newManager = createManagerFromInput(type);
        if (newManager == null) return;

        try {
            boolean success = adminManagementServices.updateManager(type, username, newManager);
            if (success) ConsoleUI.success("Manager updated successfully!");
            else ConsoleUI.error("Failed to update manager.");
        } catch (Exception e) {
            ConsoleUI.error("Error: " + e.getMessage());
        }
    }

    private void deleteManager() {
        ConsoleUI.title("DELETE MANAGER \u274C");
        ManagerType type = selectManagerType();
        if (type == null) return;

        ConsoleUI.prompt("Enter username of manager to delete:");
        String username = scanner.nextLine().trim();
        if (username.isEmpty()) { ConsoleUI.error("Username cannot be empty."); return; }

        ServicesManager existingManager = adminManagementServices.findManager(type, username);
        if (existingManager == null) { ConsoleUI.warn("Manager not found."); return; }

        System.out.println("Manager to delete:");
        displayManager(existingManager);

        ConsoleUI.prompt("Are you sure you want to delete this manager? (y/N):");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        if (!confirmation.equals("y") && !confirmation.equals("yes")) { ConsoleUI.info("Delete operation cancelled."); return; }

        try {
            boolean success = adminManagementServices.deleteManager(type, username);
            if (success) ConsoleUI.success("Manager deleted successfully!");
            else ConsoleUI.error("Failed to delete manager.");
        } catch (Exception e) {
            ConsoleUI.error("Error: " + e.getMessage());
        }
    }

    private void viewManagersByType() {
        ConsoleUI.title("VIEW MANAGERS BY TYPE \uD83D\uDCCB");
        ManagerType type = selectManagerType();
        if (type == null) return;

        try {
            adminManagementServices.displayManagers(type);
        } catch (Exception e) {
            ConsoleUI.error("Error: " + e.getMessage());
        }
    }

    private void findManager() {
        ConsoleUI.title("FIND MANAGER \uD83D\uDD0D");
        ManagerType type = selectManagerType();
        if (type == null) return;

        ConsoleUI.prompt("Enter username:");
        String username = scanner.nextLine().trim();
        if (username.isEmpty()) { ConsoleUI.error("Username cannot be empty."); return; }

        try {
            ServicesManager manager = adminManagementServices.findManager(type, username);
            if (manager != null) {
                ConsoleUI.title("MANAGER FOUND \u2705");
                displayManager(manager);
            } else {
                ConsoleUI.warn("Manager not found.");
            }
        } catch (Exception e) {
            ConsoleUI.error("Error: " + e.getMessage());
        }
    }

    private void showManagerStatistics() {
        ConsoleUI.title("MANAGER STATISTICS \uD83D\uDCCA");
        try {
            for (ManagerType type : ManagerType.values()) {
                int count = adminManagementServices.getManagerCount(type);
                System.out.printf("%-15s: %d manager(s)%n", type.name(), count);
            }
            System.out.println("\u2500".repeat(30));
            int totalCount = adminManagementServices.getTotalManagerCount();
            System.out.printf("%-15s: %d manager(s)%n", "TOTAL", totalCount);
        } catch (Exception e) {
            ConsoleUI.error("Error: " + e.getMessage());
        }
    }

    private ManagerType selectManagerType() {
        System.out.println("\nSelect manager type:");
        ManagerType[] types = ManagerType.values();
        for (int i = 0; i < types.length; i++) System.out.printf("%d. %s%n", i + 1, types[i].name());
        ConsoleUI.prompt("Choose option (1-" + types.length + "):");
        int choice = InputUtils.readInt(scanner);
        if (choice < 1 || choice > types.length) {
            ConsoleUI.error("Invalid choice."); return null; }
        return types[choice -1];
    }

    private ServicesManager createManagerFromInput(ManagerType type) {
        ConsoleUI.prompt("Enter manager name:");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) { ConsoleUI.error("Name cannot be empty."); return null; }

        ConsoleUI.prompt("Enter phone number:");
        String phoneNumber = scanner.nextLine().trim();

        ConsoleUI.prompt("Enter username:");
        String username = scanner.nextLine().trim();
        if (username.isEmpty()) { ConsoleUI.error("Username cannot be empty."); return null; }

        ConsoleUI.prompt("Enter password:");
        String password = scanner.nextLine().trim();
        if (password.isEmpty()) { ConsoleUI.error("Password cannot be empty."); return null; }

        // Create the specific manager type based on the ManagerType
        ServicesManager manager;
        switch (type) {
            case STUDENT:
                manager = new StudentManagement(name, phoneNumber, username, password);
                break;
            case MENU:
                manager = new MenuManagement(name, phoneNumber, username, password);
                break;
            case ORDER:
                manager = new OrderManagement(name, phoneNumber, username, password);
                break;
            case PAYMENT:
                manager = new PaymentService_Manager(name, phoneNumber, username, password);
                break;
            case REPORT:
                manager = new ReportService_Manager(name, phoneNumber, username, password);
                break;
            case NOTIFICATION:
                manager = new NotifcationService_Manager(name, phoneNumber, username, password);
                break;
            default:
                ConsoleUI.error("Unsupported manager type: " + type);
                return null;
        }

        return manager;
    }

    private void displayManager(ServicesManager manager) {
        System.out.printf("ID: %d%n", manager.getId());
        System.out.printf("Name: %s%n", manager.getName());
        System.out.printf("Phone: %s%n", manager.getPhoneNumber());
        System.out.printf("Username: %s%n", manager.getUsername());
        System.out.printf("Password: %s%n", manager.getPassword());
    }

    private void showReports() {
        Map<String, Object> m = reportService.summaryMetrics();
        System.out.println("Total Revenue: " + m.get("totalRevenue"));
        System.out.println("Total Students: " + m.get("totalStudents"));
        System.out.println("Total Menu Items: " + m.get("totalMenuItems"));
        System.out.println("Total Orders: " + m.get("totalOrders"));
    }
}
