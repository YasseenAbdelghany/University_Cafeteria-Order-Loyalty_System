package Interfaces;

import Core.Admin;
import java.util.List;

public interface IAdminQuery {
    Admin FindByUser(String username);
    boolean AdminExists();
    List<Admin> getAllAdmins();
}

