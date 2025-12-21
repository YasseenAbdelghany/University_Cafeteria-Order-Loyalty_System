package terminal;

import Services.PaymentRegistry;
import Services.RoleAuthService;
import Services.SimpleNamedPaymentProcessor;

import java.util.Scanner;

public class PaymentManagerConsole {
    private final RoleAuthService auth; private final PaymentRegistry payments; private final Scanner scanner;
    public PaymentManagerConsole(RoleAuthService auth, PaymentRegistry payments, Scanner scanner){ this.auth=auth; this.payments=payments; this.scanner=scanner; }
    public void run(){ if(!login("PAYMENT")) return; while(true){ System.out.println("\n=== PAYMENT SERVICES ==="); System.out.println("1. List Methods\n2. Add Method\n3. Remove Method\n0. Logout"); int c=InputUtils.readInt(scanner); switch(c){ case 1->System.out.println("Methods: "+payments.listNames()); case 2->add(); case 3->remove(); case 0->{return;} default->System.out.println("Invalid"); } } }
    private boolean login(String role){ System.out.print("Username: "); String u=scanner.nextLine().trim(); System.out.print("Password: "); String p=scanner.nextLine().trim(); if(auth.login(role,u,p)){ System.out.println("✓ Logged in."); return true;} System.out.println("✗ Access denied."); return false; }
    private void add(){ System.out.print("Method name: "); String n=scanner.nextLine().trim(); payments.register(n, new SimpleNamedPaymentProcessor(n)); System.out.println("✓ Added."); }
    private void remove(){ System.out.print("Method name: "); String n=scanner.nextLine().trim(); System.out.println(payments.remove(n)?"✓ Removed.":"✗ Not found."); }
}

