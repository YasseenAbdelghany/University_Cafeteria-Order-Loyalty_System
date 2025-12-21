package Core;

public class TopItem {
    private int itemId;
    private String name;
    private long quantity;
    private double revenue; // monetary value in default currency

    public TopItem(int itemId, String name, long quantity, double revenue) {
        this.itemId = itemId;
        this.name = name;
        this.quantity = quantity;
        this.revenue = revenue;
    }

    public int getItemId() { return itemId; }
    public String getName() { return name; }
    public long getQuantity() { return quantity; }
    public double getRevenue() { return revenue; }

    @Override
    public String toString() {
        return "TopItem{" +
                "itemId=" + itemId +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", revenue=" + revenue +
                '}';
    }
}

