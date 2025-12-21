package Interfaces;

import Core.Admin;

public interface IAdmin {
    public boolean insertAdmin(Admin admin);
    public void deleteAdmin(Admin admin);
    public void updateAdmin(Admin oldAdmin, Admin newAdmin);
}
