package Core;

import Values.Money;
import Enums.Currency;

public class OrderItem {
    private int menuItemId;
    private String nameSnapshot;
    private Money unitPrice;
    private Currency unitCurrency;
    private int qty;

    public OrderItem() {
        this.unitCurrency = Currency.getDefault();
    }

    public OrderItem(int menuItemId, String nameSnapshot, Money unitPrice, int qty) {
        this.menuItemId = menuItemId;
        this.nameSnapshot = nameSnapshot;
        this.unitPrice = unitPrice;
        this.unitCurrency = unitPrice != null ? unitPrice.getCurrency() : Currency.getDefault();
        this.qty = Math.max(1, qty);
    }

    public int getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(int menuItemId) {
        this.menuItemId = menuItemId;
    }

    public String getNameSnapshot() {
        return nameSnapshot;
    }

    public void setNameSnapshot(String nameSnapshot) {
        this.nameSnapshot = nameSnapshot;
    }

    public Money getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Money unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Currency getUnitCurrency() {
        return unitCurrency;
    }

    public void setUnitCurrency(Currency unitCurrency) {
        this.unitCurrency = unitCurrency != null ? unitCurrency : Currency.getDefault();
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = Math.max(1, qty);
    }

    public Money lineTotal() {
        return unitPrice != null ? unitPrice.multiply(qty) : new Money(0);
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "menuItemId=" + menuItemId +
                ", nameSnapshot='" + nameSnapshot + '\'' +
                ", unitPrice=" + unitPrice +
                ", unitCurrency=" + unitCurrency +
                ", qty=" + qty +
                ", lineTotal=" + lineTotal() +
                '}';
    }
}
