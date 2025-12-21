package app.gui.controllers;

import Core.MenuItem;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import app.gui.shared.AlertHelper;
import app.gui.NavigationService;
import terminal.ServiceContainer;
import Enums.ManagerType;
import Enums.Category;
import Enums.OrderStatus;
import ServiceManagers.ServicesManager;
import Services.*;
import Core.*;
import app.gui.controllers.ServiceLoginController.ServiceManagerData;

import java.util.List;
import java.util.Map;

/**
 * Controller for the Service Manager Dashboard screen.
 * Dynamically displays actions based on the manager type.
 */
public class ServiceDashboardController {
    
    @FXML
    private Label managerNameLabel;
    
    @FXML
    private Label managerTypeLabel;
    
    @FXML
    private VBox actionsVBox;
    
    @FXML
    private VBox contentArea;
    
    private ServiceContainer services;
    private ServicesManager currentManager;
    private ManagerType currentType;
    
    @FXML
    public void initialize() {
        services = NavigationService.getServiceContainer();
    }
    
    /**
     * Set manager data received from login screen
     */
    public void setManagerData(ServiceManagerData data) {
        this.currentManager = data.getManager();
        this.currentType = data.getType();
        updateUI();
    }
    
    /**
     * Update UI with manager information and load actions
     */
    private void updateUI() {
        managerNameLabel.setText(currentManager.getName());
        managerTypeLabel.setText(currentType.toString() + " Manager");
        loadActionsForManagerType();
    }
    
    /**
     * Dynamically create action buttons based on manager type
     */
    private void loadActionsForManagerType() {
        // Clear existing buttons (keep the label and separator)
        actionsVBox.getChildren().removeIf(node -> node instanceof Button);
        
        switch (currentType) {
            case MENU:
                addActionButton("View Menu", this::handleViewMenu);
                addActionButton("Add Menu Item", this::handleAddMenuItem);
                addActionButton("Update Menu Item", this::handleUpdateMenuItem);
                addActionButton("Remove Menu Item", this::handleRemoveMenuItem);
                break;
                
            case ORDER:
                addActionButton("View Orders", this::handleViewOrders);
                addActionButton("Update Order Status", this::handleUpdateOrderStatus);
                addActionButton("Process Orders", this::handleProcessOrders);
                break;
                
            case PAYMENT:
                addActionButton("View Payments", this::handleViewPayments);
                addActionButton("Manage Payment Methods", this::handleManagePaymentMethods);
                break;
                
            case NOTIFICATION:
                addActionButton("Send Notification", this::handleSendNotification);
                addActionButton("View Notifications", this::handleViewNotifications);
                break;
                
            case REPORT:
                addActionButton("Generate Reports", this::handleGenerateReports);
                addActionButton("View Analytics", this::handleViewAnalytics);
                break;
                
            case STUDENT:
                addActionButton("View Students", this::handleViewStudents);
                addActionButton("Manage Students", this::handleManageStudents);
                break;
        }
    }
    
    /**
     * Helper method to add action button
     */
    private void addActionButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(e -> action.run());
        button.getStyleClass().add("primary-button");
        actionsVBox.getChildren().add(button);
    }
    
    // ==================== MENU MANAGER ACTIONS ====================
    
    private void handleViewMenu() {
        contentArea.getChildren().clear();
        Label title = new Label("Menu Items");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        TableView<MenuItem> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<MenuItem, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getId()));
        
        TableColumn<MenuItem, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        
        TableColumn<MenuItem, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDescription()));
        
        TableColumn<MenuItem, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPrice().toString()));
        
        TableColumn<MenuItem, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCategory().toString()));
        
        table.getColumns().addAll(idCol, nameCol, descCol, priceCol, categoryCol);
        
        MenuManager menuManager = services.getMenuManager();
        ObservableList<MenuItem> items = FXCollections.observableArrayList(menuManager.getAvailableItems());
        table.setItems(items);
        
        contentArea.getChildren().addAll(title, table);
    }
    
    private void handleAddMenuItem() {
        contentArea.getChildren().clear();
        Label title = new Label("Add Menu Item");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(10));
        
        TextField nameField = new TextField();
        TextField descField = new TextField();
        TextField priceField = new TextField();
        ComboBox<Category> categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll(Category.values());
        
        form.add(new Label("Name:"), 0, 0);
        form.add(nameField, 1, 0);
        form.add(new Label("Description:"), 0, 1);
        form.add(descField, 1, 1);
        form.add(new Label("Price:"), 0, 2);
        form.add(priceField, 1, 2);
        form.add(new Label("Category:"), 0, 3);
        form.add(categoryBox, 1, 3);
        
        Button saveButton = new Button("Save");
        saveButton.getStyleClass().add("primary-button");
        saveButton.setOnAction(e -> {
            try {
                String name = nameField.getText().trim();
                String desc = descField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());
                Category category = categoryBox.getValue();
                
                if (name.isEmpty() || category == null) {
                    AlertHelper.showError("Validation Error", "Name and category are required");
                    return;
                }
                
                MenuItem item = new MenuItem(name, desc, new Values.Money(price), category);
                services.getMenuManager().addMenuItem(item);
                AlertHelper.showSuccess("Success", "Menu item added successfully");
                handleViewMenu();
            } catch (Exception ex) {
                AlertHelper.showError("Error", "Failed to add menu item: " + ex.getMessage());
            }
        });
        
        form.add(saveButton, 1, 4);
        contentArea.getChildren().addAll(title, form);
    }
    
    private void handleUpdateMenuItem() {
        contentArea.getChildren().clear();
        Label title = new Label("Update Menu Item");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(10));
        
        TextField idField = new TextField();
        TextField nameField = new TextField();
        TextField descField = new TextField();
        TextField priceField = new TextField();
        ComboBox<Category> categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll(Category.values());
        
        Button loadButton = new Button("Load");
        loadButton.setOnAction(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                MenuItem item = services.getMenuManager().findMenuItem(id);
                if (item != null) {
                    nameField.setText(item.getName());
                    descField.setText(item.getDescription());
                    priceField.setText(String.valueOf(item.getPrice().getAmount()));
                    categoryBox.setValue(item.getCategory());
                } else {
                    AlertHelper.showError("Not Found", "Menu item not found");
                }
            } catch (Exception ex) {
                AlertHelper.showError("Error", "Invalid ID");
            }
        });
        
        form.add(new Label("Item ID:"), 0, 0);
        form.add(idField, 1, 0);
        form.add(loadButton, 2, 0);
        form.add(new Label("Name:"), 0, 1);
        form.add(nameField, 1, 1);
        form.add(new Label("Description:"), 0, 2);
        form.add(descField, 1, 2);
        form.add(new Label("Price:"), 0, 3);
        form.add(priceField, 1, 3);
        form.add(new Label("Category:"), 0, 4);
        form.add(categoryBox, 1, 4);
        
        Button saveButton = new Button("Update");
        saveButton.getStyleClass().add("primary-button");
        saveButton.setOnAction(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                String name = nameField.getText().trim();
                String desc = descField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());
                Category category = categoryBox.getValue();
                
                if (name.isEmpty() || category == null) {
                    AlertHelper.showError("Validation Error", "Name and category are required");
                    return;
                }
                
                MenuItem item = new MenuItem(name, desc, new Values.Money(price), category);
                item.setId(id);
                services.getMenuManager().updateMenuItem(item);
                AlertHelper.showSuccess("Success", "Menu item updated successfully");
                handleViewMenu();
            } catch (Exception ex) {
                AlertHelper.showError("Error", "Failed to update menu item: " + ex.getMessage());
            }
        });
        
        form.add(saveButton, 1, 5);
        contentArea.getChildren().addAll(title, form);
    }
    
    private void handleRemoveMenuItem() {
        contentArea.getChildren().clear();
        Label title = new Label("Remove Menu Item");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        HBox form = new HBox(10);
        form.setPadding(new Insets(10));
        
        TextField idField = new TextField();
        idField.setPromptText("Enter Item ID");
        
        Button removeButton = new Button("Remove");
        removeButton.getStyleClass().add("primary-button");
        removeButton.setOnAction(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                if (AlertHelper.showConfirmation("Confirm", "Are you sure you want to remove this item?")) {
                    services.getMenuManager().removeMenuItem(id);
                    AlertHelper.showSuccess("Success", "Menu item removed successfully");
                    handleViewMenu();
                }
            } catch (Exception ex) {
                AlertHelper.showError("Error", "Failed to remove menu item: " + ex.getMessage());
            }
        });
        
        form.getChildren().addAll(new Label("Item ID:"), idField, removeButton);
        contentArea.getChildren().addAll(title, form);
    }
    
    // ==================== ORDER MANAGER ACTIONS ====================
    
    private void handleViewOrders() {
        contentArea.getChildren().clear();
        Label title = new Label("All Orders");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        TableView<Order> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Order, String> codeCol = new TableColumn<>("Order Code");
        codeCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCode()));
        
        TableColumn<Order, String> studentCol = new TableColumn<>("Student Code");
        studentCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStudentCode()));
        
        TableColumn<Order, String> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().total().toString()));
        
        TableColumn<Order, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus().toString()));
        
        table.getColumns().addAll(codeCol, studentCol, totalCol, statusCol);
        
        DataBase.OrderDAO orderDAO = new DataBase.OrderDAO();
        ObservableList<Order> orders = FXCollections.observableArrayList(orderDAO.findAll());
        table.setItems(orders);
        
        contentArea.getChildren().addAll(title, table);
    }
    
    private void handleUpdateOrderStatus() {
        contentArea.getChildren().clear();
        Label title = new Label("Update Order Status");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(10));
        
        TextField orderCodeField = new TextField();
        orderCodeField.setPromptText("Enter Order Code");
        
        ComboBox<OrderStatus> statusBox = new ComboBox<>();
        statusBox.getItems().addAll(OrderStatus.values());
        
        Button updateButton = new Button("Update Status");
        updateButton.getStyleClass().add("primary-button");
        updateButton.setOnAction(e -> {
            try {
                String orderCode = orderCodeField.getText().trim();
                OrderStatus newStatus = statusBox.getValue();
                
                if (orderCode.isEmpty() || newStatus == null) {
                    AlertHelper.showError("Validation Error", "Order code and status are required");
                    return;
                }
                
                services.getOrderProcessor().advanceStatusByCode(orderCode, newStatus);
                AlertHelper.showSuccess("Success", "Order status updated successfully");
                handleViewOrders();
            } catch (Exception ex) {
                AlertHelper.showError("Error", "Failed to update order status: " + ex.getMessage());
            }
        });
        
        form.add(new Label("Order Code:"), 0, 0);
        form.add(orderCodeField, 1, 0);
        form.add(new Label("New Status:"), 0, 1);
        form.add(statusBox, 1, 1);
        form.add(updateButton, 1, 2);
        
        contentArea.getChildren().addAll(title, form);
    }
    
    private void handleProcessOrders() {
        contentArea.getChildren().clear();
        Label title = new Label("Pending Orders");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        TableView<Order> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Order, String> codeCol = new TableColumn<>("Order Code");
        codeCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCode()));
        
        TableColumn<Order, String> studentCol = new TableColumn<>("Student Code");
        studentCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStudentCode()));
        
        TableColumn<Order, String> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().total().toString()));
        
        TableColumn<Order, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus().toString()));
        
        table.getColumns().addAll(codeCol, studentCol, totalCol, statusCol);
        
        List<Order> pendingOrders = services.getOrderProcessor().trackPendingOrders();
        ObservableList<Order> orders = FXCollections.observableArrayList(pendingOrders);
        table.setItems(orders);
        
        contentArea.getChildren().addAll(title, table);
    }
    
    // ==================== PAYMENT MANAGER ACTIONS ====================
    
    private void handleViewPayments() {
        contentArea.getChildren().clear();
        Label title = new Label("Payment Information");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        Label info = new Label("Payment tracking is integrated with order processing.\nView orders to see payment status.");
        info.setWrapText(true);
        
        contentArea.getChildren().addAll(title, info);
    }
    
    private void handleManagePaymentMethods() {
        contentArea.getChildren().clear();
        Label title = new Label("Payment Methods");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        ListView<String> listView = new ListView<>();
        PaymentRegistry registry = services.getPaymentRegistry();
        ObservableList<String> methods = FXCollections.observableArrayList(registry.listNames());
        listView.setItems(methods);
        
        contentArea.getChildren().addAll(title, listView);
    }
    
    // ==================== NOTIFICATION MANAGER ACTIONS ====================
    
    private void handleSendNotification() {
        contentArea.getChildren().clear();
        Label title = new Label("Send Notification");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(10));
        
        TextField studentCodeField = new TextField();
        studentCodeField.setPromptText("Enter Student Code");
        
        TextArea messageArea = new TextArea();
        messageArea.setPromptText("Enter notification message");
        messageArea.setPrefRowCount(4);
        
        Button sendButton = new Button("Send");
        sendButton.getStyleClass().add("primary-button");
        sendButton.setOnAction(e -> {
            try {
                String studentCode = studentCodeField.getText().trim();
                String message = messageArea.getText().trim();
                
                if (studentCode.isEmpty() || message.isEmpty()) {
                    AlertHelper.showError("Validation Error", "Student code and message are required");
                    return;
                }
                
                Student student = services.getStudentManager().findByCode(studentCode);
                if (student == null) {
                    AlertHelper.showError("Error", "Student not found");
                    return;
                }
                
                services.getNotificationHistoryService().sendGeneralNotification(student, message);
                AlertHelper.showSuccess("Success", "Notification sent successfully");
                studentCodeField.clear();
                messageArea.clear();
            } catch (Exception ex) {
                AlertHelper.showError("Error", "Failed to send notification: " + ex.getMessage());
            }
        });
        
        form.add(new Label("Student Code:"), 0, 0);
        form.add(studentCodeField, 1, 0);
        form.add(new Label("Message:"), 0, 1);
        form.add(messageArea, 1, 1);
        form.add(sendButton, 1, 2);
        
        contentArea.getChildren().addAll(title, form);
    }
    
    private void handleViewNotifications() {
        contentArea.getChildren().clear();
        Label title = new Label("All Notifications");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        TableView<NotificationHistory> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<NotificationHistory, String> studentCol = new TableColumn<>("Student");
        studentCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStudentCode()));
        
        TableColumn<NotificationHistory, String> messageCol = new TableColumn<>("Message");
        messageCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNotifyMessage()));
        
        TableColumn<NotificationHistory, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getMessageType()));
        
        TableColumn<NotificationHistory, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCreatedAt().toString()));
        
        table.getColumns().addAll(studentCol, messageCol, typeCol, dateCol);
        
        List<NotificationHistory> notifications = services.getNotificationHistoryService().getAllNotificationHistory();
        ObservableList<NotificationHistory> items = FXCollections.observableArrayList(notifications);
        table.setItems(items);
        
        contentArea.getChildren().addAll(title, table);
    }
    
    // ==================== REPORT MANAGER ACTIONS ====================
    
    private void handleGenerateReports() {
        contentArea.getChildren().clear();
        Label title = new Label("System Reports");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        ReportService reportService = services.getReportService();
        Map<String, Object> metrics = reportService.summaryMetrics();
        
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        
        int row = 0;
        for (Map.Entry<String, Object> entry : metrics.entrySet()) {
            Label keyLabel = new Label(entry.getKey() + ":");
            keyLabel.setStyle("-fx-font-weight: bold;");
            Label valueLabel = new Label(entry.getValue().toString());
            grid.add(keyLabel, 0, row);
            grid.add(valueLabel, 1, row);
            row++;
        }
        
        contentArea.getChildren().addAll(title, grid);
    }
    
    private void handleViewAnalytics() {
        contentArea.getChildren().clear();
        Label title = new Label("Analytics Dashboard");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        Label info = new Label("Detailed analytics and charts coming soon...");
        info.setStyle("-fx-text-fill: gray;");
        
        contentArea.getChildren().addAll(title, info);
    }
    
    // ==================== STUDENT MANAGER ACTIONS ====================
    
    private void handleViewStudents() {
        contentArea.getChildren().clear();
        Label title = new Label("All Students");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        TableView<Student> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Student, String> codeCol = new TableColumn<>("Student Code");
        codeCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStudentCode()));
        
        TableColumn<Student, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        
        TableColumn<Student, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPhoneNumber()));
        
        TableColumn<Student, Integer> pointsCol = new TableColumn<>("Loyalty Points");
        pointsCol.setCellValueFactory(data -> {
            Student s = data.getValue();
            int points = s.getAccount() != null ? s.getAccount().balance() : 0;
            return new javafx.beans.property.SimpleObjectProperty<>(points);
        });
        
        table.getColumns().addAll(codeCol, nameCol, phoneCol, pointsCol);
        
        List<Student> students = services.getStudentManager().listAll();
        ObservableList<Student> items = FXCollections.observableArrayList(students);
        table.setItems(items);
        
        contentArea.getChildren().addAll(title, table);
    }
    
    private void handleManageStudents() {
        contentArea.getChildren().clear();
        Label title = new Label("Manage Students");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        Label info = new Label("Student management operations:\n- View all students\n- Search by student code\n- Update student information");
        info.setWrapText(true);
        
        contentArea.getChildren().addAll(title, info);
    }
    
    @FXML
    private void handleLogout() {
        NavigationService.navigateTo("main-menu");
    }
}
