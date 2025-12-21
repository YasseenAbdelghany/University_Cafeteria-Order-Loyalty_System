package app.gui.admin.controllers;

import Services.ReportService;
import ServiceManagers.ReportService_Manager;
import app.gui.shared.AlertHelper;
import app.gui.admin.AdminNavigationService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import terminal.ServiceContainer;

import java.util.Map;
import java.util.logging.Logger;

/**
 * Controller for Report Manager Dashboard.
 * Shows sales summary including total revenue, orders breakdown, and loyalty redemptions.
 */
public class ReportManagerDashboardController {
    private static final Logger logger = Logger.getLogger(ReportManagerDashboardController.class.getName());
    
    @FXML private Label welcomeLabel;
    @FXML private Label totalRevenueLabel;
    @FXML private Label totalOrdersLabel;
    @FXML private Label totalRedemptionsLabel;
    @FXML private TextArea salesBreakdownArea;
    @FXML private TextArea loyaltyRedemptionArea;
    
    private ReportService_Manager manager;
    private ServiceContainer services;
    private ReportService reportService;
    
    @FXML
    public void initialize() {
        services = AdminNavigationService.getServiceContainer();
        reportService = services.getReportService();
        logger.info("ReportManagerDashboardController initialized");
    }
    
    public void setManagerData(ReportService_Manager manager) {
        this.manager = manager;
        welcomeLabel.setText("Welcome, " + manager.getName() + " - Report Manager");
        loadSalesReport();
    }
    
    @FXML
    private void handleRefresh() {
        loadSalesReport();
    }
    
    private void loadSalesReport() {
        try {
            // Get sales summary
            Map<String, Object> salesSummary = reportService.summaryMetrics();
            
            // Display total revenue (Money object)
            Values.Money totalRevenue = (Values.Money) salesSummary.getOrDefault("totalRevenue", Values.Money.zero());
            totalRevenueLabel.setText(String.format("%.2f EGP", totalRevenue.getAmount()));
            
            // Display total orders
            int totalOrders = (int) salesSummary.getOrDefault("totalOrders", 0);
            totalOrdersLabel.setText(String.valueOf(totalOrders));
            
            // Display summary information
            int totalStudents = (int) salesSummary.getOrDefault("totalStudents", 0);
            int totalMenuItems = (int) salesSummary.getOrDefault("totalMenuItems", 0);
            
            StringBuilder salesBreakdown = new StringBuilder();
            salesBreakdown.append("Sales Summary:\n");
            salesBreakdown.append("=".repeat(50)).append("\n\n");
            salesBreakdown.append(String.format("Total Revenue: %.2f EGP\n", totalRevenue.getAmount()));
            salesBreakdown.append(String.format("Total Orders: %d\n", totalOrders));
            salesBreakdown.append(String.format("Total Students: %d\n", totalStudents));
            salesBreakdown.append(String.format("Total Menu Items: %d\n", totalMenuItems));
            salesBreakdown.append("\n").append("=".repeat(50)).append("\n");
            
            if (totalOrders > 0) {
                double avgOrderValue = totalRevenue.getAmount().doubleValue() / totalOrders;
                salesBreakdown.append(String.format("Average Order Value: %.2f EGP\n", avgOrderValue));
            }
            
            salesBreakdownArea.setText(salesBreakdown.toString());
            
            // Display loyalty redemptions (placeholder data)
            int totalRedemptions = 0;
            double redemptionValue = 0.0;
            totalRedemptionsLabel.setText("0");
            
            StringBuilder redemptionInfo = new StringBuilder();
            redemptionInfo.append("Loyalty Program Summary:\n");
            redemptionInfo.append("=".repeat(50)).append("\n\n");
            redemptionInfo.append(String.format("Total Redemptions: %d\n", totalRedemptions));
            redemptionInfo.append(String.format("Total Value Redeemed: %.2f EGP\n", redemptionValue));
            redemptionInfo.append(String.format("Average Redemption: %.2f EGP\n", 
                totalRedemptions > 0 ? redemptionValue / totalRedemptions : 0.0));
            
            loyaltyRedemptionArea.setText(redemptionInfo.toString());
            
            logger.info("Sales report loaded successfully");
            
        } catch (Exception e) {
            logger.severe("Error loading sales report: " + e.getMessage());
            e.printStackTrace();
            AlertHelper.showError("Load Error", "Failed to load sales report: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleExportReport() {
        AlertHelper.showInfo("Export", "Export functionality coming soon!");
    }
    
    @FXML
    private void handleLogout() {
        logger.info("Report Manager logging out");
        AdminNavigationService.navigateTo("unified-login");
    }
}
