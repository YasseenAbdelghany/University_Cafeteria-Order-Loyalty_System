package Interfaces;

import Values.DateRange;
import Core.SalesReport;
import Core.RedemptionReport;

public interface IReportingService {
    SalesReport salesSummary(DateRange range);
    RedemptionReport loyaltyRedemptions(DateRange range);
}
