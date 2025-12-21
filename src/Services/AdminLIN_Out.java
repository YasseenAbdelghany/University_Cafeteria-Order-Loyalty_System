package Services;

import Core.Admin;
import Interfaces.IADMINService;
import Interfaces.IAdmin;
import Interfaces.IAdminQuery;

import java.util.Scanner;

public class AdminLIN_Out implements IADMINService {
    private final IAdmin adminCommands;
    private final IAdminQuery adminQueries;

    public AdminLIN_Out(AdminManager manage) {
        if (manage == null) throw new IllegalArgumentException("AdminManager cannot be null");
        this.adminCommands = manage.getAdminDAO();
        this.adminQueries = manage.getAdminQueryDAO();
        initializeAdminSystem();
    }

    private void initializeAdminSystem() {
        try {
            if (!adminQueries.AdminExists()) {
                Admin defaultAdmin = new Admin("System Administrator", "admin", "admin123");
                adminCommands.insertAdmin(defaultAdmin);
                System.out.println("Default admin created: username='admin', password='admin123'");
            }
        } catch (Exception e) {
            System.err.println("Error initializing admin system: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Admin login(String username, String password) {
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            return null;
        }
        Admin admin = adminQueries.FindByUser(username.trim());
        if (admin == null) return null;
        String stored = admin.getPassword();
        // Plain-text comparison only
        if (stored != null && stored.equals(password)) {
            return admin;
        }
        return null;
    }

    @Override
    public Admin performLogin(Scanner scanner) {
        if (scanner == null) throw new IllegalArgumentException("Scanner cannot be null");
        for (int attempts = 1; attempts <= 3; attempts++) {
            System.out.print("Username: ");
            String username = scanner.nextLine().trim();
            System.out.print("Password: ");
            String password = scanner.nextLine().trim();
            Admin admin = login(username, password);
            if (admin != null) {
                System.out.println("Login successful. Welcome, " + admin.getName() + ".");
                return admin;
            }
            System.out.println("Invalid credentials (" + attempts + "/3).");
        }
        System.out.println("Too many failed attempts.");
        return null;
    }

    @Override
    public void performLogout(Admin admin) {
        if (admin == null) return;
        System.out.println("Logout successful. Goodbye, " + admin.getName() + ".");
    }
}
