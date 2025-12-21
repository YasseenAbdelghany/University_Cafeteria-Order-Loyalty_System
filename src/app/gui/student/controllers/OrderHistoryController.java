package app.gui.student.controllers;

import Core.OrderHistory;
import Core.Student;
import Services.OrderHistoryService;
import app.gui.shared.AlertHelper;
import app.gui.student.StudentNavigationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import terminal.ServiceContainer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

/**
 * Controller for the order history screen in the Student Application.
 * Displays a student's past orders with details including order code, date, amount, payment method, and status.
 */
public class OrderHistoryController {
    private static final Logger logger = Logger.getLogger(OrderHistoryController.class.getName());
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    @FXML private TableView<OrderHistory> orderHistoryTable;
    @FXML private TableColumn<OrderHistory, String> orderCodeColumn;
    @FXML private TableColumn<OrderHistory, String> orderDateColumn;
    @FXML private TableColumn<OrderHistory, String> totalAmountColumn;
    @FXML private TableColumn<OrderHistory, String> paymentMethodColumn;
    @FXML private TableColumn<OrderHistory, String> statusColumn;
    @FXML private Button backButton;
    
    private Student currentStudent;
    private ServiceContainer services;
    private OrderHistoryService orderHistoryService;
    
    /**
     * Initialize the controller.
     * Called automatically by JavaFX after FXML loading.
     * Sets up the table columns with property bindings.
     */
    @FXML
    public void initialize() {
        logger.info("Initializing OrderHistoryController (Student App)");
        services = StudentNavigationService.getServiceContainer();
        
        if (services != null) {
            orderHistoryService = services.getOrderHistoryService();
        }
        
        // Set up table columns
        setupTableColumns();
    }
    
    /**
     * Set up the table columns with property value factories and custom cell factories.
     */
    private void setupTableColumns() {
        // Order Code column
        orderCodeColumn.setCellValueFactory(new PropertyValueFactory<>("orderCode"));
        
        // Order Date column - format LocalDateTime to string
        orderDateColumn.setCellValueFactory(cellData -> {
            LocalDateTime date = cellData.getValue().getOrderDate();
            String formattedDate = date != null ? date.format(DATE_FORMATTER) : "";
            return new javafx.beans.property.SimpleStringProperty(formattedDate);
        });
        
        // Total Amount column - format as currency
        totalAmountColumn.setCellValueFactory(cellData -> {
            double amount = cellData.getValue().getTotalAmount();
            String formattedAmount = String.format("%.2f EGP", amount);
            return new javafx.beans.property.SimpleStringProperty(formattedAmount);
        });
        
        // Payment Method column
        paymentMethodColumn.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        
        // Status column
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("orderStatus"));
        
        logger.info("Table columns configured successfully");
    }
    
    /**
     * Set the student data and load their order history.
     * This method is called by StudentNavigationService when navigating to this screen.
     * 
     * @param student The student object whose order history to display
     */
    public void setStudent(Student student) {
        logger.info("Setting student data: " + (student != null ? student.getStudentCode() : "null"));
        this.currentStudent = student;
        loadOrderHistory();
    }
    
    /**
     * Load the order history for the current student from the database.
     * Populates the TableView with OrderHistory records.
     */
    private void loadOrderHistory() {
        if (currentStudent == null) {
            logger.warning("Cannot load order history: student is null");
            AlertHelper.showError("Error", "Student data not available");
            return;
        }
        
        try {
            logger.info("Loading order history for student: " + currentStudent.getStudentCode());
            
            if (orderHistoryService == null) {
                logger.severe("OrderHistoryService is not available");
                AlertHelper.showError("Error", "Order history service is not available");
                return;
            }
            
            // Get order history from service
            List<OrderHistory> orderHistoryList = orderHistoryService.getOrderHistoryForStudent(
                currentStudent.getStudentCode()
            );
            
            if (orderHistoryList == null || orderHistoryList.isEmpty()) {
                logger.info("No order history found for student: " + currentStudent.getStudentCode());
                AlertHelper.showInfo("No Orders", "You haven't placed any orders yet.");
                orderHistoryTable.setItems(FXCollections.observableArrayList());
                return;
            }
            
            // Convert to ObservableList and populate table
            ObservableList<OrderHistory> observableList = FXCollections.observableArrayList(orderHistoryList);
            orderHistoryTable.setItems(observableList);
            
            logger.info("Loaded " + orderHistoryList.size() + " order history records");
            
        } catch (Exception e) {
            logger.severe("Error loading order history: " + e.getMessage());
            e.printStackTrace();
            AlertHelper.showError("Error", "Failed to load order history: " + e.getMessage());
        }
    }
    
    /**
     * Handle Back button click.
     * Return to the menu dashboard (not student-dashboard).
     */
    @FXML
    private void handleBack() {
        logger.info("Back button clicked");
        
        if (currentStudent == null) {
            logger.warning("Student data not available, navigating to student login");
            StudentNavigationService.navigateTo("student-login");
            return;
        }
        
        try {
            StudentNavigationService.navigateToWithData("menu-dashboard", currentStudent);
        } catch (Exception e) {
            logger.severe("Error navigating back to menu dashboard: " + e.getMessage());
            AlertHelper.showError("Navigation Error", "Failed to return to menu dashboard: " + e.getMessage());
        }
    }
}
