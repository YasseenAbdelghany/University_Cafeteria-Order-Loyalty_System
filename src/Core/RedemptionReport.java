package Core;

import Values.Money;

import java.time.LocalDate;
import java.util.Map;

public class RedemptionReport {
    private LocalDate startDate;
    private LocalDate endDate;
    private int totalRedemptions;
    private int totalPointsRedeemed;
    private Money totalDiscountValue;
    private Map<Integer, Integer> studentRedemptions; // studentId -> points redeemed

    public RedemptionReport() {}

    public RedemptionReport(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalRedemptions = 0;
        this.totalPointsRedeemed = 0;
        this.totalDiscountValue = Money.zero();
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

    public int getTotalRedemptions() {
        return totalRedemptions;
    }

    public void setTotalRedemptions(int totalRedemptions) {
        this.totalRedemptions = totalRedemptions;
    }

    public int getTotalPointsRedeemed() {
        return totalPointsRedeemed;
    }

    public void setTotalPointsRedeemed(int totalPointsRedeemed) {
        this.totalPointsRedeemed = totalPointsRedeemed;
    }

    public Money getTotalDiscountValue() {
        return totalDiscountValue;
    }

    public void setTotalDiscountValue(Money totalDiscountValue) {
        this.totalDiscountValue = totalDiscountValue;
    }

    public Map<Integer, Integer> getStudentRedemptions() {
        return studentRedemptions;
    }

    public void setStudentRedemptions(Map<Integer, Integer> studentRedemptions) {
        this.studentRedemptions = studentRedemptions;
    }

    @Override
    public String toString() {
        return "RedemptionReport{" +
                "period=" + startDate + " to " + endDate +
                ", totalRedemptions=" + totalRedemptions +
                ", totalPointsRedeemed=" + totalPointsRedeemed +
                ", totalDiscountValue=" + totalDiscountValue +
                '}';
    }
}
