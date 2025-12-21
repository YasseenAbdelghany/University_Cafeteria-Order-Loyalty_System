package Services;

import Core.MenuItem;
import Enums.Category;
import Interfaces.IMenuProvider;

import java.util.List;

public class MenuManager {
    private final IMenuProvider menu;
    private final DataBase.MenuDAO menuDAO;

    public MenuManager(IMenuProvider menu) {
        this.menu = menu;
        this.menuDAO = (menu instanceof DataBase.MenuDAO) ? (DataBase.MenuDAO) menu : null;
    }

    public MenuManager() {
        this(new DataBase.MenuDAO());
    }

    public List<MenuItem> getAvailableItems() {
        return menu.listItems();
    }

    /**
     * Get all menu items including inactive ones (for admin use)
     */
    public List<MenuItem> getAllItems() {
        if (menuDAO != null) {
            return menuDAO.listAllItems();
        }
        return menu.listItems();
    }

    /**
     * Toggle the active status of a menu item
     */
    public void toggleActiveStatus(int itemId, boolean active) {
        if (menuDAO != null) {
            menuDAO.toggleActive(itemId, active);
        }
    }

    public void addMenuItem(MenuItem item) {
        if (item == null) {
            throw new IllegalArgumentException("MenuItem cannot be null");
        }
        menu.add(item);
    }

    public void updateMenuItem(MenuItem item) {
        if (item == null) {
            throw new IllegalArgumentException("MenuItem cannot be null");
        }
        menu.update(item);
    }

    public void removeMenuItem(int itemId) {
        menu.remove(itemId);
    }

    public MenuItem findMenuItem(int itemId) {
        return menu.findById(itemId);
    }

    public List<MenuItem> searchMenu(String text, Category category) {
        return menu.search(text, category);
    }
}
