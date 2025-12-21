package terminal;

import Enums.OrderStatus;
import Services.OrderProcessor;
import Services.RoleAuthService;
import Core.Order;

import java.util.Scanner;

public class OrderManagerConsole {
    private final RoleAuthService auth; private final OrderProcessor orderProcessor; private final Scanner scanner;
    public OrderManagerConsole(RoleAuthService auth, OrderProcessor orderProcessor, Scanner scanner){ this.auth=auth; this.orderProcessor=orderProcessor; this.scanner=scanner; }
    public void run(){ if(!login("ORDER")) return; while(true){ System.out.println("\n=== ORDER MANAGEMENT ==="); System.out.println("1. View Pending Orders\n2. Mark PREPARING\n3. Mark READY\n0. Logout"); int c=InputUtils.readInt(scanner); switch(c){ case 1->listPending(); case 2->updateStatus(OrderStatus.PREPARING); case 3->updateStatus(OrderStatus.READY); case 0->{return;} default->System.out.println("Invalid"); } } }
    private boolean login(String role){ System.out.print("Username: "); String u=scanner.nextLine().trim(); System.out.print("Password: "); String p=scanner.nextLine().trim(); if(auth.login(role,u,p)){ System.out.println("✓ Logged in."); return true;} System.out.println("✗ Access denied."); return false; }
    private void listPending(){ var orders=orderProcessor.trackPendingOrders(); if(orders.isEmpty()){ System.out.println("No pending orders."); return;} for(Order o: orders) System.out.println("Order "+o.getId()+" | Code:"+o.getCode()+" | Student:"+o.getStudentCode()+" | Status:"+o.getStatus()+" | Total:"+o.total()); }
    private void updateStatus(OrderStatus st){ System.out.print("Order ID: "); int id=InputUtils.readInt(scanner); try{ orderProcessor.advanceStatus(id, st); System.out.println("✓ Updated to "+st);}catch(Exception e){ System.out.println("✗ "+e.getMessage()); } }
}
