package DataBase;

import Core.Admin;
import Interfaces.IAdmin;
import Interfaces.IAdminQuery;


import java.sql.*;

public class AdminDAO implements IAdmin, IAdminQuery {

        private Connection connection;

        public AdminDAO() {
                DBconnection db = new DBconnection();
                connection = db.getConnection();
        }

        public AdminDAO(Connection conn) {
            this.connection = conn;
        }

        @Override
        public boolean insertAdmin(Admin admin) {
            String sqlQuery = "INSERT INTO admin (Name, UserName, Password) VALUES(?,?,?)";

            try( PreparedStatement ps = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)){
                ps.setString(1, admin.getName());
                ps.setString(2,admin.getUsername());
                ps.setString(3,admin.getPassword());

                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        admin.setId(rs.getInt(1));
                    }
                }
                return true;
            }
            catch (SQLException e){
                e.printStackTrace();
                return false;
            }
        }
    @Override
    public void updateAdmin(Admin oldAdmin, Admin newAdmin) {
        Admin existing = FindByUser(oldAdmin.getUsername());
        if (existing == null) {
            System.err.println("Update Admin Failed");
            return;
        }

        String sqlQuery = "UPDATE admin SET Name = ?, UserName = ?, Password = ? WHERE UserName = ?";
        try (PreparedStatement ps = connection.prepareStatement(sqlQuery)) {
            ps.setString(1, newAdmin.getName());
            ps.setString(2, newAdmin.getUsername());
            ps.setString(3, newAdmin.getPassword());
            ps.setString(4, oldAdmin.getUsername());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Admin Updated Successfully");
            } else {
                System.out.println("No rows updated. Check if username exists.");
            }
        } catch (SQLException e) {
            System.out.println("Admin Update Failed");
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAdmin(Admin admin) {
        Admin existingAdmin = FindByUser(admin.getUsername());
        if (existingAdmin == null) {
            System.err.println("Delete Admin Failed: Admin not found");
            return;
        }

        String sqlQuery = "DELETE FROM admin WHERE UserName = ?";
        try (PreparedStatement ps = connection.prepareStatement(sqlQuery)) {
            ps.setString(1, admin.getUsername());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Admin deleted successfully");
            } else {
                System.out.println("No admin deleted. Check if username exists.");
            }
        } catch (SQLException e) {
            System.out.println("Delete Admin Failed");
            e.printStackTrace();
        }
    }

        @Override
        public Admin FindByUser(String username) {
                String sqlQuery = "SELECT Id, Name, UserName, Password FROM admin WHERE UserName = ?";
                try (PreparedStatement ps = connection.prepareStatement(sqlQuery)){
                    ps.setString(1, username);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            return null;
                        }
                        Admin admin = new Admin();
                        admin.setId(rs.getInt("Id"));
                        admin.setName(rs.getString("Name"));
                        admin.setUsername(rs.getString("UserName"));
                        admin.setPassword(rs.getString("Password"));
                        return admin;
                    }
                }catch (SQLException e){
                    System.out.println("Admin Not Found");
                    e.printStackTrace();
                }
            return null;
        }

        @Override
        public boolean AdminExists() {
            String sqlQuery = "SELECT COUNT(*) FROM admin";
            try (Statement s = connection.createStatement() ; ResultSet rs = s.executeQuery(sqlQuery)) {
                    if (rs.next()) {
                            return rs.getInt(1) >0;
                    }
            }catch (SQLException e){
                e.printStackTrace();
            }
            return false;
        }

        @Override
        public java.util.List<Admin> getAllAdmins() {
            java.util.List<Admin> admins = new java.util.ArrayList<>();
            String sqlQuery = "SELECT Id, Name, UserName, Password FROM admin";
            try (Statement s = connection.createStatement(); ResultSet rs = s.executeQuery(sqlQuery)) {
                while (rs.next()) {
                    Admin admin = new Admin();
                    admin.setId(rs.getInt("Id"));
                    admin.setName(rs.getString("Name"));
                    admin.setUsername(rs.getString("UserName"));
                    admin.setPassword(rs.getString("Password"));
                    admins.add(admin);
                }
            } catch (SQLException e) {
                System.out.println("Failed to retrieve admins");
                e.printStackTrace();
            }
            return admins;
        }

    // Keeping for backward compatibility if referenced elsewhere
    public void createDefaultAdmin() {
        Admin defaultAdmin = new Admin("System Administrator",  "admin","admin123");
        insertAdmin(defaultAdmin);
    }

}
