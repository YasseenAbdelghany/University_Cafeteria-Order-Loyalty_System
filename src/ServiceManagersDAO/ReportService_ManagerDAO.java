package ServiceManagersDAO;

import DataBase.DBconnection;
import ServiceManager_int.IReportService_Manager;
import ServiceManagers.ReportService_Manager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportService_ManagerDAO implements IReportService_Manager {
    private Connection connection;

    public ReportService_ManagerDAO() {
        DBconnection db = new DBconnection();
        connection = db.getConnection();
    }

    public ReportService_ManagerDAO(Connection conn) {
        this.connection = conn;
    }

    @Override
    public boolean add(ReportService_Manager manager) {
        String sqlQuery = "INSERT INTO ReportManager (Name, Phone_Number, username, Password) VALUES(?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, manager.getName());
            ps.setString(2, manager.getPhoneNumber());
            ps.setString(3, manager.getUsername());
            ps.setString(4, manager.getPassword());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    manager.setId(rs.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String username) {
        ReportService_Manager existing = findByUsername(username);
        if (existing == null) {
            System.err.println("Delete Manager Failed, Manager Not Found");
            return false;
        }
        String sqlQuery = "DELETE FROM ReportManager WHERE UserName = ?";
        try (PreparedStatement ps = connection.prepareStatement(sqlQuery)) {
            ps.setString(1, username);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(String username, ReportService_Manager newManager) {
        ReportService_Manager existing = findByUsername(username);
        if (existing == null) {
            System.err.println("Update Manager Failed, Manager Not Found");
            return false;
        }
        String sqlQuery = "UPDATE ReportManager SET Name = ?, Phone_Number = ?, UserName = ?, Password = ? WHERE UserName = ?";
        try (PreparedStatement ps = connection.prepareStatement(sqlQuery)) {
            ps.setString(1, newManager.getName());
            ps.setString(2, newManager.getPhoneNumber());
            ps.setString(3, newManager.getUsername());
            ps.setString(4, newManager.getPassword());
            ps.setString(5, username);

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Manager Update Failed");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ReportService_Manager findByUsername(String username) {
        String sqlQuery = "SELECT Id, Name, Phone_Number, UserName, Password FROM ReportManager WHERE UserName = ?";
        try (PreparedStatement ps = connection.prepareStatement(sqlQuery)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ReportService_Manager manager = new ReportService_Manager();
                    manager.setId(rs.getInt("Id"));
                    manager.setName(rs.getString("Name"));
                    manager.setPhoneNumber(rs.getString("Phone_Number"));
                    manager.setUsername(rs.getString("UserName"));
                    manager.setPassword(rs.getString("Password"));
                    return manager;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<ReportService_Manager> findAll() {
        String sqlQuery = "SELECT Id, Name, Phone_Number, UserName, Password FROM ReportManager";
        List<ReportService_Manager> list = new ArrayList<>();
        try (Statement s = connection.createStatement(); ResultSet rs = s.executeQuery(sqlQuery)) {
            while (rs.next()) {
                ReportService_Manager m = new ReportService_Manager();
                m.setId(rs.getInt("Id"));
                m.setName(rs.getString("Name"));
                m.setPhoneNumber(rs.getString("Phone_Number"));
                m.setUsername(rs.getString("UserName"));
                m.setPassword(rs.getString("Password"));
                list.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean exists(String username) {
        return findByUsername(username) != null;
    }

    @Override
    public void createDefaultManager() {
        if (!managerExists()) {
            ReportService_Manager defaultManager = new ReportService_Manager();
            add(defaultManager);
            System.out.println("Default Report Manager created successfully");
        }
    }

    @Override
    public int count() {
        String sqlQuery = "SELECT COUNT(*) FROM ReportManager";
        try (Statement s = connection.createStatement(); ResultSet rs = s.executeQuery(sqlQuery)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Helper methods
    private boolean managerExists() {
        return count() > 0;
    }

    public void viewAll() {
        String sqlQuery = "SELECT Id, Name, Phone_Number, UserName, Password FROM ReportManager";
        try (Statement s = connection.createStatement(); ResultSet rs = s.executeQuery(sqlQuery)) {
            System.out.printf("%-5s %-20s %-15s %-15s %-15s%n", "ID", "Name", "Phone Number", "Username", "Password");
            System.out.println("---------------------------------------------------------------");
            while (rs.next()) {
                int id = rs.getInt("Id");
                String name = rs.getString("Name");
                String phoneNumber = rs.getString("Phone_Number");
                String username = rs.getString("UserName");
                String password = rs.getString("Password");
                System.out.printf("%-5d %-20s %-15s %-15s %-15s%n", id, name, phoneNumber, username, password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
