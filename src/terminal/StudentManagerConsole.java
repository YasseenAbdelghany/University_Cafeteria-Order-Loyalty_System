package terminal;

import Services.LoyaltyProgramService;
import Services.RoleAuthService;
import Services.StudentManager;
import Core.Student;

import java.util.List;
import java.util.Scanner;

public class StudentManagerConsole {
    private final RoleAuthService auth; private final StudentManager studentManager; private final LoyaltyProgramService loyaltyService; private final Scanner scanner;
    public StudentManagerConsole(RoleAuthService auth, StudentManager studentManager, LoyaltyProgramService loyaltyService, Scanner scanner){ this.auth=auth; this.studentManager=studentManager; this.loyaltyService=loyaltyService; this.scanner=scanner; }
    public void run(){ if(!login("STUDENT")) return; while(true){ System.out.println("\n=== STUDENT MANAGEMENT ==="); System.out.println("1. View All Students\n2. Update Student Name\n3. Delete Student\n4. Add Points to Student\n5. Add Points to ALL Students\n0. Logout"); int c=InputUtils.readInt(scanner); switch(c){ case 1->viewAll(); case 2->updateName(); case 3->delete(); case 4->addPointsOne(); case 5->addPointsAll(); case 0->{return;} default->System.out.println("Invalid"); } } }
    private boolean login(String role){ System.out.print("Username: "); String u=scanner.nextLine().trim(); System.out.print("Password: "); String p=scanner.nextLine().trim(); if(auth.login(role,u,p)){ System.out.println("✓ Logged in."); return true;} System.out.println("✗ Access denied."); return false; }
    private void viewAll(){ List<Student> s=studentManager.listAll(); if(s.isEmpty()){ System.out.println("No students."); return;} for(Student st: s) System.out.println(st); }
    private void updateName(){ System.out.print("Student code: "); String code=scanner.nextLine().trim(); Student s=studentManager.findByCode(code); if(s==null){ System.out.println("Not found."); return;} System.out.print("New name: "); String n=scanner.nextLine().trim(); s.setName(n); studentManager.update(s); System.out.println("✓ Updated."); }
    private void delete(){ System.out.print("Student code: "); String code=scanner.nextLine().trim(); System.out.println(studentManager.delete(code)?"✓ Deleted.":"✗ Failed."); }
    private void addPointsOne(){ System.out.print("Student code: "); String code=scanner.nextLine().trim(); Student s=studentManager.findByCode(code); if(s==null){ System.out.println("Not found."); return;} System.out.print("Points to add: "); int pts=InputUtils.readInt(scanner); if(pts<=0){ System.out.println("Invalid points."); return;} loyaltyService.addPoints(s, pts); System.out.println("New balance: "+s.getAccount().balance()); }
    private void addPointsAll(){ System.out.print("Points to add to all: "); int pts=InputUtils.readInt(scanner); if(pts<=0){ System.out.println("Invalid points."); return;} List<Student> all=studentManager.listAll(); for(Student s: all) loyaltyService.addPoints(s, pts); System.out.println("✓ Added to "+all.size()+" students."); }
}

