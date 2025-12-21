package Core;

import Values.Money;

import java.time.LocalDate;
import java.util.Map;

public class SalesReport {
    private LocalDate startDate;
    private LocalDate endDate;
    private Money totalSales;
    private int totalOrders;
    private Map<String, Integer> itemsSold;
    private Map<String, Money> categoryBreakdown;

    public SalesReport() {}

    public SalesReport(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalSales = Money.zero();
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Money getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(Money totalSales) {
        this.totalSales = totalSales;
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Map<String, Integer> getItemsSold() {
        return itemsSold;
    }

    public void setItemsSold(Map<String, Integer> itemsSold) {
        this.itemsSold = itemsSold;
    }

    public Map<String, Money> getCategoryBreakdown() {
        return categoryBreakdown;
    }

    public void setCategoryBreakdown(Map<String, Money> categoryBreakdown) {
        this.categoryBreakdown = categoryBreakdown;
    }

    @Override
    public String toString() {
        return "SalesReport{" +
                "period=" + startDate + " to " + endDate +
                ", totalSales=" + totalSales +
                ", totalOrders=" + totalOrders +
                '}';
    }
}
