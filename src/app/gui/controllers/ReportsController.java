package app.gui.controllers;

import Core.Admin;
import Enums.ManagerType;
import Services.AdminManagement_Services;
import Services.ReportService;
import Values.Money;
import app.gui.NavigationService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import terminal.ServiceContainer;

import java.util.Map;
import java.util.logging.Logger;

/**
 * Controller for the system reports screen.
 * Displays summary metrics and manager statistics for administrators.
 */
public class ReportsController {
    private static final Logger logger = Logger.getLogger(ReportsController.class.getName());
    
    @FXML
    private Label totalRevenueLabel;
    
    @FXML
    private Label totalStudentsLabel;
    
    @FXML
    private Label totalMenuItemsLabel;
    
    @FXML
    private Label totalOrdersLabel;
    
    @FXML
    private TableView<ManagerStatistic> managerStatsTable;
    
    @FXML
    private TableColumn<ManagerStatistic, String> managerTypeColumn;
    
    @FXML
    private TableColumn<ManagerStatistic, Number> managerCountColumn;
    
    @FXML
    private Button backButton;
    
    private ServiceContainer services;
    private ReportService reportService;
    private AdminManagement_Services adminManagementServices;
    private Admin currentAdmin;
    
    /**
     * Initialize the controller.
     * Called automatically by JavaFX after FXML loading.
     */
    @FXML
    public void initialize() {
        services = NavigationService.getServiceContainer();
        reportService = services.getReportService();
        adminManagementServices = services.getAdminManagementServices();
        
        // Set up table columns
        managerTypeColumn.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        managerCountColumn.setCellValueFactory(cellData -> cellData.getValue().countProperty());
        
        // Load data
        loadSummaryMetrics();
        loadManagerStatistics();
        
        logger.info("ReportsController initialized.");
    }
    
    /**
     * Set the current admin.
     * Called by NavigationService when navigating to this screen with admin data.
     * 
     * @param admin The logged-in admin
     */
    public void setAdmin(Admin admin) {
        this.currentAdmin = admin;
        logger.info("Reports screen loaded for admin: " + admin.getName());
    }
    
    /**
     * Load summary metrics from ReportService and display in dashboard cards.
     */
    private void loadSummaryMetrics() {
        try {
            Map<String, Object> metrics = reportService.summaryMetrics();
            
            // Display total revenue
            Money totalRevenue = (Money) metrics.get("totalRevenue");
            totalRevenueLabel.setText(totalRevenue != null ? totalRevenue.toString() : "$0.00");
            
            // Display total students
            Integer totalStudents = (Integer) metrics.get("totalStudents");
            totalStudentsLabel.setText(totalStudents != null ? totalStudents.toString() : "0");
            
            // Display total menu items
            Integer totalMenuItems = (Integer) metrics.get("totalMenuItems");
            totalMenuItemsLabel.setText(totalMenuItems != null ? totalMenuItems.toString() : "0");
            
            // Display total orders
            Integer totalOrders = (Integer) metrics.get("totalOrders");
            totalOrdersLabel.setText(totalOrders != null ? totalOrders.toString() : "0");
            
            logger.info("Summary metrics loaded successfully.");
        } catch (Exception e) {
            logger.severe("Failed to load summary metrics: " + e.getMessage());
            totalRevenueLabel.setText("Error");
            totalStudentsLabel.setText("Error");
            totalMenuItemsLabel.setText("Error");
            totalOrdersLabel.setText("Error");
        }
    }
    
    /**
     * Load manager statistics from AdminManagement_Services and populate the table.
     */
    private void loadManagerStatistics() {
        try {
            ObservableList<ManagerStatistic> statistics = FXCollections.observableArrayList();
            
            // Get count for each manager type
            for (ManagerType type : ManagerType.values()) {
                int count = adminManagementServices.getManagerCount(type);
                String typeName = formatManagerType(type);
                statistics.add(new ManagerStatistic(typeName, count));
            }
            
            managerStatsTable.setItems(statistics);
            logger.info("Manager statistics loaded successfully.");
        } catch (Exception e) {
            logger.severe("Failed to load manager statistics: " + e.getMessage());
        }
    }
    
    /**
     * Format manager type enum to display-friendly string.
     * 
     * @param type The manager type enum
     * @return Formatted string
     */
    private String formatManagerType(ManagerType type) {
        switch (type) {
            case STUDENT:
                return "👨‍🎓 Student Manager";
            case MENU:
                return "🍔 Menu Manager";
            case ORDER:
                return "📦 Order Manager";
            case PAYMENT:
                return "💳 Payment Manager";
            case REPORT:
                return "📊 Report Manager";
            case NOTIFICATION:
                return "🔔 Notification Manager";
            default:
                return type.name();
        }
    }
    
    /**
     * Handle Back button click.
     * Returns to the admin dashboard.
     */
    @FXML
    private void handleBack() {
        logger.info("Returning to admin dashboard...");
        NavigationService.navigateToWithData("admin-dashboard", currentAdmin);
    }
    
    /**
     * Inner class to represent manager statistics for the table.
     */
    public static class ManagerStatistic {
        private final SimpleStringProperty type;
        private final SimpleIntegerProperty count;
        
        public ManagerStatistic(String type, int count) {
            this.type = new SimpleStringProperty(type);
            this.count = new SimpleIntegerProperty(count);
        }
        
        public SimpleStringProperty typeProperty() {
            return type;
        }
        
        public SimpleIntegerProperty countProperty() {
            return count;
        }
        
        public String getType() {
            return type.get();
        }
        
        public int getCount() {
            return count.get();
        }
    }
}
