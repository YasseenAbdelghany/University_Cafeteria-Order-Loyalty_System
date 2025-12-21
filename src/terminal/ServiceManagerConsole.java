package terminal;

import Services.AdminManagement_Services;
import Enums.ManagerType;
import ServiceManagers.ServicesManager;

import java.util.Scanner;

/**
 * ServiceManagerConsole provides a unified interface for managing all service managers
 * Following SOLID principles - specifically Single Responsibility and Interface Segregation
 */
public class ServiceManagerConsole {
    private final AdminManagement_Services adminManagementServices;
    private final Scanner scanner;

    public ServiceManagerConsole(AdminManagement_Services adminManagementServices, Scanner scanner) {
        this.adminManagementServices = adminManagementServices;
        this.scanner = scanner;
    }

    public void run() {
        while (true) {
            System.out.println("\n=== SERVICE MANAGER ADMINISTRATION ===");
            System.out.println("1. Add Manager");
            System.out.println("2. Update Manager");
            System.out.println("3. Delete Manager");
            System.out.println("4. View All Managers by Type");
            System.out.println("5. Find Manager");
            System.out.println("6. Create Default Managers");
            System.out.println("7. Manager Statistics");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose option: ");

            int choice = InputUtils.readInt(scanner);

            switch (choice) {
                case 1 -> addManager();
                case 2 -> updateManager();
                case 3 -> deleteManager();
                case 4 -> viewManagersByType();
                case 5 -> findManager();
                case 6 -> createDefaultManagers();
                case 7 -> showStatistics();
                case 0 -> { return; }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void addManager() {
        System.out.println("\n=== ADD NEW MANAGER ===");
        ManagerType type = selectManagerType();
        if (type == null) return;

        ServicesManager manager = createManagerFromInput();
        if (manager == null) return;

        try {
            boolean success = adminManagementServices.addManager(type, manager);
            if (success) {
                System.out.println("✓ Manager added successfully!");
                System.out.println("Manager ID: " + manager.getId());
            } else {
                System.out.println("✗ Failed to add manager. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    private void updateManager() {
        System.out.println("\n=== UPDATE MANAGER ===");
        ManagerType type = selectManagerType();
        if (type == null) return;

        System.out.print("Enter username of manager to update: ");
        String username = scanner.nextLine().trim();

        if (username.isEmpty()) {
            System.out.println("Username cannot be empty.");
            return;
        }

        // Check if manager exists
        ServicesManager existingManager = adminManagementServices.findManager(type, username);
        if (existingManager == null) {
            System.out.println("Manager not found.");
            return;
        }

        System.out.println("Current manager details:");
        displayManager(existingManager);

        System.out.println("\nEnter new details:");
        ServicesManager newManager = createManagerFromInput();
        if (newManager == null) return;

        try {
            boolean success = adminManagementServices.updateManager(type, username, newManager);
            if (success) {
                System.out.println("✓ Manager updated successfully!");
            } else {
                System.out.println("✗ Failed to update manager.");
            }
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    private void deleteManager() {
        System.out.println("\n=== DELETE MANAGER ===");
        ManagerType type = selectManagerType();
        if (type == null) return;

        System.out.print("Enter username of manager to delete: ");
        String username = scanner.nextLine().trim();

        if (username.isEmpty()) {
            System.out.println("Username cannot be empty.");
            return;
        }

        // Check if manager exists
        ServicesManager existingManager = adminManagementServices.findManager(type, username);
        if (existingManager == null) {
            System.out.println("Manager not found.");
            return;
        }

        System.out.println("Manager to delete:");
        displayManager(existingManager);

        System.out.print("Are you sure you want to delete this manager? (y/N): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (!confirmation.equals("y") && !confirmation.equals("yes")) {
            System.out.println("Delete operation cancelled.");
            return;
        }

        try {
            boolean success = adminManagementServices.deleteManager(type, username);
            if (success) {
                System.out.println("✓ Manager deleted successfully!");
            } else {
                System.out.println("✗ Failed to delete manager.");
            }
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    private void viewManagersByType() {
        System.out.println("\n=== VIEW MANAGERS BY TYPE ===");
        ManagerType type = selectManagerType();
        if (type == null) return;

        try {
            adminManagementServices.displayManagers(type);
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    private void findManager() {
        System.out.println("\n=== FIND MANAGER ===");
        ManagerType type = selectManagerType();
        if (type == null) return;

        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        if (username.isEmpty()) {
            System.out.println("Username cannot be empty.");
            return;
        }

        try {
            ServicesManager manager = adminManagementServices.findManager(type, username);
            if (manager != null) {
                System.out.println("\n=== MANAGER FOUND ===");
                displayManager(manager);
            } else {
                System.out.println("Manager not found.");
            }
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    private void createDefaultManagers() {
        System.out.println("\n=== CREATE DEFAULT MANAGERS ===");
        System.out.print("This will create default managers for all types. Continue? (y/N): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (!confirmation.equals("y") && !confirmation.equals("yes")) {
            System.out.println("Operation cancelled.");
            return;
        }

        try {
            adminManagementServices.createDefaultManagers();
            System.out.println("✓ Default managers created successfully!");
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    private void showStatistics() {
        System.out.println("\n=== MANAGER STATISTICS ===");

        try {
            for (ManagerType type : ManagerType.values()) {
                int count = adminManagementServices.getManagerCount(type);
                System.out.printf("%-15s: %d manager(s)%n", type.name(), count);
            }

            int totalCount = adminManagementServices.getTotalManagerCount();
            System.out.println("─".repeat(30));
            System.out.printf("%-15s: %d manager(s)%n", "TOTAL", totalCount);
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    private ManagerType selectManagerType() {
        System.out.println("\nSelect manager type:");
        ManagerType[] types = ManagerType.values();

        for (int i = 0; i < types.length; i++) {
            System.out.printf("%d. %s%n", i + 1, types[i].name());
        }

        System.out.print("Choose option (1-" + types.length + "): ");
        int choice = InputUtils.readInt(scanner);

        if (choice < 1 || choice > types.length) {
            System.out.println("Invalid choice.");
            return null;
        }

        return types[choice - 1];
    }

    private ServicesManager createManagerFromInput() {
        System.out.print("Enter manager name: ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("Name cannot be empty.");
            return null;
        }

        System.out.print("Enter phone number: ");
        String phoneNumber = scanner.nextLine().trim();

        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        if (username.isEmpty()) {
            System.out.println("Username cannot be empty.");
            return null;
        }

        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        if (password.isEmpty()) {
            System.out.println("Password cannot be empty.");
            return null;
        }

        ServicesManager manager = new ServicesManager();
        manager.setName(name);
        manager.setPhoneNumber(phoneNumber);
        manager.setUsername(username);
        manager.setPassword(password);

        return manager;
    }

    private void displayManager(ServicesManager manager) {
        System.out.printf("ID: %d%n", manager.getId());
        System.out.printf("Name: %s%n", manager.getName());
        System.out.printf("Phone: %s%n", manager.getPhoneNumber());
        System.out.printf("Username: %s%n", manager.getUsername());
        System.out.printf("Password: %s%n", manager.getPassword());
    }
}
