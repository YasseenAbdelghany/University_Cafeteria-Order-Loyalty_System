package Services;

import Core.*;
import Interfaces.*;
import Values.DateRange;
import Values.Money;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportService implements IReportingService {
    private final IOrderRepository orders;
    private final IMenuProvider menu;
    private final IStudentRepository students;

    public ReportService(IOrderRepository orders, IMenuProvider menu, IStudentRepository students) {
        this.orders = orders;
        this.menu = menu;
        this.students = students;
    }

    @Override
    public SalesReport salesSummary(DateRange range) {
        if (range == null) {
            throw new IllegalArgumentException("DateRange cannot be null");
        }

        SalesReport report = new SalesReport(range.getFrom(), range.getTo());

        // Use all orders since we don't have date filtering in our current DAO
        List<Order> allOrders = orders.findAll();

        Money totalSales = Money.zero();
        Map<String, Integer> itemsSold = new HashMap<>();
        Map<String, Money> categoryBreakdown = new HashMap<>();

        for (Order order : allOrders) {
            totalSales = totalSales.add(order.total());

            for (OrderItem item : order.getItems()) {
                // Count items sold
                String itemName = item.getNameSnapshot();
                itemsSold.merge(itemName, item.getQty(), Integer::sum);

                // Aggregate by item name as category breakdown placeholder
                Money lineTotal = item.lineTotal();
                categoryBreakdown.merge(itemName, lineTotal, (existing, newValue) -> existing.add(newValue));
            }
        }

        report.setTotalSales(totalSales);
        report.setTotalOrders(allOrders.size());
        report.setItemsSold(itemsSold);
        report.setCategoryBreakdown(categoryBreakdown);

        return report;
    }

    @Override
    public RedemptionReport loyaltyRedemptions(DateRange range) {
        if (range == null) {
            throw new IllegalArgumentException("DateRange cannot be null");
        }

        RedemptionReport report = new RedemptionReport(range.getFrom(), range.getTo());
        // Placeholder since no redemption table exists
        report.setTotalRedemptions(0);
        report.setTotalPointsRedeemed(0);
        report.setTotalDiscountValue(Money.zero());
        report.setStudentRedemptions(new HashMap<>());
        return report;
    }

    // Convenience metrics for admin dashboard
    public Map<String, Object> summaryMetrics() {
        Map<String, Object> map = new HashMap<>();
        List<Order> allOrders = orders.findAll();
        Money total = Money.zero();
        for (Order o : allOrders) total = total.add(o.total());
        map.put("totalRevenue", total);
        map.put("totalStudents", students.getAllStudents().size());
        map.put("totalMenuItems", menu.listItems().size());
        map.put("totalOrders", allOrders.size());
        return map;
    }
}
