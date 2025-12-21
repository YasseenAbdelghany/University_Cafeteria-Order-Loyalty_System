package terminal;

import Core.MenuItem;
import Values.Money;
import Enums.Category;
import Services.MenuManager;
import Services.RoleAuthService;

import java.util.Scanner;

public class MenuManagerConsole {
    private final RoleAuthService auth; private final MenuManager menuManager; private final Scanner scanner;
    public MenuManagerConsole(RoleAuthService auth, MenuManager menuManager, Scanner scanner){ this.auth=auth; this.menuManager=menuManager; this.scanner=scanner; }
    public void run(){ if(!login("MENU")) return; while(true){ System.out.println("\n=== MENU MANAGEMENT ==="); System.out.println("1. List Items\n2. Add Item\n3. Update Item\n4. Remove Item\n0. Logout"); int c=InputUtils.readInt(scanner); switch(c){ case 1->list(); case 2->add(); case 3->update(); case 4->remove(); case 0->{return;} default->System.out.println("Invalid"); } } }
    private boolean login(String role){ System.out.print("Username: "); String u=scanner.nextLine().trim(); System.out.print("Password: "); String p=scanner.nextLine().trim(); if(auth.login(role,u,p)){ System.out.println("✓ Logged in."); return true;} System.out.println("✗ Access denied."); return false; }
    private void list(){ var items=menuManager.getAvailableItems(); if(items.isEmpty()){ System.out.println("No items."); return;} for(var i: items) System.out.println(i.getId()+" | "+i.getName()+" | "+i.getDescription()+" | "+i.getPrice()+" | "+i.getCategory()); }
    private void add(){
        System.out.print("Name: ");
        String name=scanner.nextLine().trim();
        System.out.print("Description: ");
        String desc=scanner.nextLine().trim();
        System.out.print("Price: ");
        double price=InputUtils.readDouble(scanner);
        System.out.println("Category: 1-MAIN_COURSE 2-SNACK 3-DRINK");
        int c=InputUtils.readInt(scanner);
        Category cat=c==1?Category.MAIN_COURSE:c==2?Category.SNACK:Category.DRINK;
        menuManager.addMenuItem(new MenuItem(name, desc, new Money(price), cat));
        System.out.println("✓ Added.");
    }

    private void update(){
        System.out.print("Item ID: ");
        int id=InputUtils.readInt(scanner);
        MenuItem ex=menuManager.findMenuItem(id);
        if(ex==null){
            System.out.println("Not found.");
            return;
        }
        System.out.print("Name("+ex.getName()+"): ");
        String name=scanner.nextLine().trim();
        if(!name.isEmpty()) ex.setName(name);
        System.out.print("Description("+ex.getDescription()+"): ");
        String desc=scanner.nextLine().trim();
        if(!desc.isEmpty()) ex.setDescription(desc);
        System.out.print("Price("+ex.getPrice().getAmount()+"): ");
        String ps=scanner.nextLine().trim();
        if(!ps.isEmpty()) {
            try{
                ex.setPrice(new Money(Double.parseDouble(ps)));
            }catch(NumberFormatException ignored){}
        }
        System.out.println("Category: 1-MAIN_COURSE 2-SNACK 3-DRINK (blank keep)");
        String cs=scanner.nextLine().trim();
        if(!cs.isEmpty()) {
            try{
                int k=Integer.parseInt(cs);
                ex.setCategory(k==1?Category.MAIN_COURSE:k==2?Category.SNACK:Category.DRINK);
            }catch(NumberFormatException ignored){}
        }
        menuManager.updateMenuItem(ex);
        System.out.println("✓ Updated.");
    }
    private void remove(){ System.out.print("Item ID: "); int id=InputUtils.readInt(scanner); menuManager.removeMenuItem(id); System.out.println("✓ Removed."); }
}
