package terminal;

import Values.DateRange;
import Core.RedemptionReport;
import Core.SalesReport;
import Services.ReportService;
import Services.RoleAuthService;

import java.util.Scanner;

public class ReportManagerConsole {
    private final RoleAuthService auth; private final ReportService reportService; private final Scanner scanner;
    public ReportManagerConsole(RoleAuthService auth, ReportService reportService, Scanner scanner){ this.auth=auth; this.reportService=reportService; this.scanner=scanner; }
    public void run(){ if(!login("REPORT")) return; while(true){ System.out.println("\n=== REPORTS ==="); System.out.println("1. Sales Summary (All)\n2. Loyalty Redemptions (All)\n0. Logout"); int c=InputUtils.readInt(scanner); switch(c){ case 1->salesSummary(); case 2->redemptions(); case 0->{return;} default->System.out.println("Invalid"); } } }
    private boolean login(String role){ System.out.print("Username: "); String u=scanner.nextLine().trim(); System.out.print("Password: "); String p=scanner.nextLine().trim(); if(auth.login(role,u,p)){ System.out.println("✓ Logged in."); return true;} System.out.println("✗ Access denied."); return false; }
    private void salesSummary(){ DateRange r=new DateRange(); SalesReport sr=reportService.salesSummary(r); System.out.println("Total Sales: "+sr.getTotalSales()); System.out.println("Total Orders: "+sr.getTotalOrders()); if(sr.getItemsSold()!=null) sr.getItemsSold().forEach((k,v)-> System.out.println("  "+k+": "+v)); }
    private void redemptions(){ DateRange r=new DateRange(); RedemptionReport rr=reportService.loyaltyRedemptions(r); System.out.println("Total Redemptions: "+rr.getTotalRedemptions()+" | Points: "+rr.getTotalPointsRedeemed()+" | Discount: "+rr.getTotalDiscountValue()); }
}

