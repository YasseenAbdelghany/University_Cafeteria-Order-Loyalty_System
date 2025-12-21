package terminal;

import Services.AdminManager;
import Services.RoleAuthService;
import Core.Admin;

import java.util.Scanner;

public class UserAdminConsole {
    private final RoleAuthService auth; private final AdminManager adminManager; private final Scanner scanner;
    public UserAdminConsole(RoleAuthService auth, AdminManager adminManager, Scanner scanner){ this.auth=auth; this.adminManager=adminManager; this.scanner=scanner; }
    public void run(){
        if (!login("USER")) return;
        while (true) {
            System.out.println("\n=== IT (ADMIN MANAGEMENT) ===");
            System.out.println("1. Create Admin\n2. Update Admin\n3. Delete Admin\n0. Logout");
            int c = InputUtils.readInt(scanner);
            switch (c) {
                case 1 -> create();
                case 2 -> update();
                case 3 -> delete();
                case 0 -> { return; }
                default -> System.out.println("Invalid");
            }
        }
    }
    private boolean login(String role){ System.out.print("Username: "); String u=scanner.nextLine().trim(); System.out.print("Password: "); String p=scanner.nextLine().trim(); if(auth.login(role,u,p)){System.out.println("✓ Logged in."); return true;} System.out.println("✗ Access denied."); return false; }
    private void create(){ System.out.print("Name: "); String n=scanner.nextLine().trim(); System.out.print("Username: "); String u=scanner.nextLine().trim(); System.out.print("Password: "); String p=scanner.nextLine().trim(); boolean ok = adminManager.insertAdmin(new Admin(n,u,p)); System.out.println(ok?"✓ Created":"✗ Failed"); }
    private void update(){ System.out.print("Old Username: "); String ou=scanner.nextLine().trim(); System.out.print("New Name: "); String n=scanner.nextLine().trim(); System.out.print("New Username: "); String nu=scanner.nextLine().trim(); System.out.print("New Password: "); String p=scanner.nextLine().trim(); adminManager.updateAdmin(new Admin(null, ou, null), new Admin(n, nu, p)); }
    private void delete(){ System.out.print("Username: "); String u=scanner.nextLine().trim(); adminManager.deleteAdmin(new Admin(null, u, null)); }
}

