package Core;

import Enums.Category;
import Values.Money;

public class MenuItem {
    private int id;
    private String name;
    private String description;
    private Money price;
    private Category category;
    private boolean active;

    public MenuItem() {
        this.active = true; // Default to active
    }

    public MenuItem(String name, String description, Money price, Category category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.active = true; // Default to active
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Money getPrice() {
        return price;
    }

    public void setPrice(Money price) {
        this.price = price;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "MenuItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", category=" + category +
                ", active=" + active +
                '}';
    }
}
