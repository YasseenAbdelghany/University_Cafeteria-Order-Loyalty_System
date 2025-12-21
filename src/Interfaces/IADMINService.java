package Interfaces;

import Core.Admin;

import java.util.Scanner;

public interface IADMINService {
    Admin login(String username, String password);
    Admin performLogin(Scanner scanner);
    void performLogout(Admin admin);


}
