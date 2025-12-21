package Services;


import Core.Admin;
import DataBase.AdminDAO;
import Interfaces.IAdmin;
import Interfaces.IAdminQuery;

public class AdminManager  {

    private IAdmin adminDAO;
    private IAdminQuery adminQuery;

    public AdminManager() {
        AdminDAO dao = new AdminDAO();
        this.adminDAO = dao;
        this.adminQuery = dao;
    }

    // CRUD operations only
    public boolean insertAdmin(Admin admin) {
        return adminDAO.insertAdmin(admin);
    }

    public void updateAdmin(Admin oldAdmin, Admin newAdmin) {
        adminDAO.updateAdmin(oldAdmin, newAdmin);
    }

    public void deleteAdmin(Admin admin) {
        adminDAO.deleteAdmin(admin);
    }

    public IAdmin getAdminDAO() {
        return adminDAO;
    }

    public IAdminQuery getAdminQueryDAO() {
        return adminQuery;
    }
}
