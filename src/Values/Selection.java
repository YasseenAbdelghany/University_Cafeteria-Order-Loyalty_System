package Values;

public class Selection {
    private int itemId;
    private int qty;

    public Selection() {}

    public Selection(int itemId, int qty) {
        this.itemId = itemId;
        this.qty = Math.max(1, qty);
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = Math.max(1, qty);
    }

    @Override
    public String toString() {
        return "Selection{itemId=" + itemId + ", qty=" + qty + '}';
    }
}
