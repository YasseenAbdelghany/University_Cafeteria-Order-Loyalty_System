package Services;

import java.util.*;
import ServiceManagersDAO.*;
import ServiceManagers.*;

/**
 * Auth manager for service roles (MENU, ORDER, STUDENT, REPORT, PAYMENT, NOTIFICATION, USER).
 * Service roles authenticate against DB manager tables; USER stays in-memory by design.
 */

// responsible for authentication and user management for different roles
    // USER role is managed in-memory for demo purposes; service roles are managed via DAOs
    // Service roles: MENU, ORDER, STUDENT, REPORT, PAYMENT, NOTIFICATION
    // USER role is for demo/testing; service roles are persisted in DB and managed via AdminManagement_Services
    // This class does not handle role creation for service roles; use AdminManagement_Services instead
    // USER role can be created/updated/deleted here for demo purposes
    // Login checks role and verifies credentials accordingly
    // Listing users is only supported for USER role here; service roles are managed elsewhere
    // This class uses simple in-memory storage for USER role; service roles interact with their respective DAOs
    // Passwords are stored in plain text for simplicity; not secure for production use
    // This is a simplified example; real-world applications should use secure password handling and storage

public class RoleAuthService {
    private final Map<String, Map<String, String>> store = new HashMap<>();

    public RoleAuthService() {
        seedDefaults();
    }

    private void seedDefaults() {
        // Only seed USER demo account; service roles are persisted in DB and managed elsewhere
        store.computeIfAbsent("USER", k -> new HashMap<>()).putIfAbsent("user", "user123");
    }

    public boolean create(String role, String username, String password) {
        if (role == null || username == null || password == null) return false;
        role = role.toUpperCase(Locale.ROOT).trim();
        if ("USER".equals(role)) {
            store.computeIfAbsent(role, k -> new HashMap<>());
            Map<String, String> users = store.get(role);
            if (users.containsKey(username)) return false;
            users.put(username, password);
            return true;
        }
        // Service roles are not created here (use AdminManagement_Services)
        return false;
    }

    public boolean update(String role, String username, String newPassword) {
        if (role == null || username == null || newPassword == null) return false;
        role = role.toUpperCase(Locale.ROOT).trim();
        if ("USER".equals(role)) {
            Map<String, String> users = store.get(role);
            if (users == null || !users.containsKey(username)) return false;
            users.put(username, newPassword);
            return true;
        }
        return false;
    }

    public boolean delete(String role, String username) {
        if (role == null || username == null) return false;
        role = role.toUpperCase(Locale.ROOT).trim();
        if ("USER".equals(role)) {
            Map<String, String> users = store.get(role);
            if (users == null) return false;
            return users.remove(username) != null;
        }
        return false;
    }

    public boolean login(String role, String username, String password) {
        if (role == null || username == null || password == null) return false;
        String r = role.toUpperCase(Locale.ROOT).trim();
        switch (r) {
            case "STUDENT": {
                StudentManagementDAO dao = new StudentManagementDAO();
                StudentManagement m = dao.findByUsername(username);
                return m != null && password.equals(m.getPassword());
            }
            case "MENU": {
                MenuManagementDAO dao = new MenuManagementDAO();
                MenuManagement m = dao.findByUsername(username);
                return m != null && password.equals(m.getPassword());
            }
            case "ORDER": {
                OrderManagementDAO dao = new OrderManagementDAO();
                OrderManagement m = dao.findByUsername(username);
                return m != null && password.equals(m.getPassword());
            }
            case "REPORT": {
                ReportService_ManagerDAO dao = new ReportService_ManagerDAO();
                ReportService_Manager m = dao.findByUsername(username);
                return m != null && password.equals(m.getPassword());
            }
            case "PAYMENT": {
                PaymentService_ManagerDAO dao = new PaymentService_ManagerDAO();
                PaymentService_Manager m = dao.findByUsername(username);
                return m != null && password.equals(m.getPassword());
            }
            case "NOTIFICATION": {
                NotifcationService_ManagerDAO dao = new NotifcationService_ManagerDAO();
                NotifcationService_Manager m = dao.findByUsername(username);
                return m != null && password.equals(m.getPassword());
            }
            case "USER": {
                Map<String, String> users = store.get("USER");
                if (users == null) return false;
                String stored = users.get(username);
                return stored != null && stored.equals(password);
            }
            default:
                return false;
        }
    }

    public List<String> listUsers(String role) {
        String r = role == null ? null : role.toUpperCase(Locale.ROOT).trim();
        if ("USER".equals(r)) {
            Map<String, String> users = store.get(r);
            return users == null ? Collections.emptyList() : new ArrayList<>(users.keySet());
        }
        // For service roles, listing users is managed by AdminManagement_Services/DAOs
        return Collections.emptyList();
    }

}
