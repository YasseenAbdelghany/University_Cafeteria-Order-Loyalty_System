package Interfaces;

import Core.MenuItem;
import Enums.Category;
import java.util.List;

public interface IMenuProvider {
    List<MenuItem> listItems();
    void add(MenuItem item);
    void update(MenuItem item);
    void remove(int itemId);
    MenuItem findById(int itemId);
    // New: DB-backed search to replace Stream filtering in GUI
    List<MenuItem> search(String text, Category category);
}
