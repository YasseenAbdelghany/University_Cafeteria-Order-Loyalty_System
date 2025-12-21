package DataBase;

import Core.MenuItem;
import Enums.Category;
import Enums.Currency;
import Values.Money;
import Interfaces.IMenuProvider;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class MenuDAO implements IMenuProvider {
    private static final Logger logger = Logger.getLogger(MenuDAO.class.getName());
    private final Connection connection;

    public MenuDAO() {
        DBconnection db = new DBconnection();
        this.connection = db.getConnection();
    }

    public MenuDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<MenuItem> listItems() {
        String sql = "SELECT Id, Name, Description, Price, Category, active FROM menu_item WHERE active = 1";
        List<MenuItem> items = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                items.add(mapMenuItem(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to list menu items", e);
        }
        return items;
    }

    /**
     * List all menu items including inactive ones (for admin use)
     */
    public List<MenuItem> listAllItems() {
        String sql = "SELECT Id, Name, Description, Price, Category, active FROM menu_item";
        List<MenuItem> items = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                items.add(mapMenuItem(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to list all menu items", e);
        }
        return items;
    }

    /**
     * Toggle the active status of a menu item
     */
    public void toggleActive(int itemId, boolean active) {
        String sql = "UPDATE menu_item SET active = ? WHERE Id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setBoolean(1, active);
            ps.setInt(2, itemId);
            ps.executeUpdate();
            logger.info("Toggled menu item " + itemId + " active status to: " + active);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to toggle active status for item: " + itemId, e);
        }
    }

    @Override
    public void add(MenuItem item) {
        String sql = "INSERT INTO menu_item (Name, Description, Price, Category, active) VALUES (?, ?, ?, ?, 1)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, item.getName());
            ps.setString(2, item.getDescription());
            ps.setDouble(3, item.getPrice().getAmount().doubleValue());
            ps.setString(4, item.getCategory().name());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    item.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to add menu item: " + item.getName(), e);
        }
    }

    @Override
    public void update(MenuItem item) {
        String sql = "UPDATE menu_item SET Name = ?, Description = ?, Price = ?, Category = ? WHERE Id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, item.getName());
            ps.setString(2, item.getDescription());
            ps.setDouble(3, item.getPrice().getAmount().doubleValue());
            ps.setString(4, item.getCategory().name());
            ps.setInt(5, item.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to update menu item: " + item.getId(), e);
        }
    }

    @Override
    public void remove(int itemId) {
        String sql = "UPDATE menu_item SET active = 0 WHERE Id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to remove menu item: " + itemId, e);
        }
    }

    @Override
    public MenuItem findById(int itemId) {
        String sql = "SELECT Id, Name, Description, Price, Category, active FROM menu_item WHERE Id = ? AND active = 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapMenuItem(rs);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to find menu item: " + itemId, e);
        }
        return null;
    }

    @Override
    public List<MenuItem> search(String text, Category category) {
        List<MenuItem> items = new ArrayList<>();
        String base = "SELECT Id, Name, Description, Price, Category, active FROM menu_item WHERE active = 1";
        StringBuilder sql = new StringBuilder(base);
        List<Object> params = new ArrayList<>();
        if (category != null) {
            sql.append(" AND Category = ?");
            params.add(category.name());
        }
        if (text != null && !text.trim().isEmpty()) {
            sql.append(" AND (Name LIKE ? OR Description LIKE ?)");
            String like = "%" + text.trim() + "%";
            params.add(like);
            params.add(like);
        }
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapMenuItem(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to search menu items", e);
        }
        return items;
    }

    private MenuItem mapMenuItem(ResultSet rs) throws SQLException {
        MenuItem item = new MenuItem();
        item.setId(rs.getInt("Id"));
        item.setName(rs.getString("Name"));
        item.setDescription(rs.getString("Description"));
        item.setPrice(new Money(rs.getDouble("Price"), Currency.getDefault()));
        item.setCategory(Category.valueOf(rs.getString("Category")));
        item.setActive(rs.getBoolean("active"));
        return item;
    }
}
