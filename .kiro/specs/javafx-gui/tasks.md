  # Implementation Plan

- [x] 1. Set up JavaFX project structure and core infrastructure





  - Create the `src/app/gui` package structure with subdirectories for controllers
  - Create the `src/app/resources` directory structure with subdirectories for fxml, css, and images
  - Add JavaFX dependencies to the project if not already present
  - Create the main `CafeteriaGUIApp.java` class extending `Application` with basic stage setup
  - Create the `NavigationService.java` singleton class with scene management methods
  - Create the `AlertHelper.java` utility class with methods for success, error, warning, info, and confirmation dialogs
  - _Requirements: 1.4, 2.3, 8.1, 8.5_

- [x] 2. Implement main menu screen





  - [x] 2.1 Create main-menu.fxml with layout


    - Design the FXML layout with VBox root container
    - Add welcome banner Label at top with cafeteria title
    - Add GridPane with 2x2 layout for four main portal buttons (Student, Admin, Services, IT Admin)
    - Add Exit button at bottom
    - Apply fx:id attributes to all interactive components
    - Set onAction attributes for all buttons
    - _Requirements: 1.1, 2.1, 2.4, 2.5_
  - [x] 2.2 Create MainMenuController.java


    - Implement event handlers for all four portal buttons (handleStudentPortal, handleAdminPortal, handleServicesPortal, handleITAdminPortal)
    - Implement handleExit method to close the application
    - Add navigation calls to appropriate login screens
    - _Requirements: 1.2, 8.1_

- [x] 3. Implement CSS theme and styling





  - Create cafeteria-theme.css with color palette definitions (cyan, yellow, magenta, green, red)
  - Define styles for primary-button, secondary-button, header-label, card, success-message, error-message, badge
  - Add hover effects and transitions for buttons
  - Define table view styles with alternating row colors
  - Define text field and form control styles
  - Link CSS file to all FXML files
  - _Requirements: 7.1, 7.2, 7.3, 7.4, 7.5_

- [x] 4. Implement student authentication screens






  - [x] 4.1 Create student-login.fxml with TabPane

    - Design FXML with TabPane containing Login and Register tabs
    - Add Login tab with TextField for student code and Login button
    - Add Register tab with TextField for name, TextField for phone, and Register button
    - Add Back button to return to main menu
    - Apply fx:id and onAction attributes
    - _Requirements: 2.1, 2.4, 2.5, 3.1_

  - [x] 4.2 Create StudentLoginController.java

    - Implement handleLogin method that validates student code and calls StudentManager.login()
    - Implement handleRegister method that validates inputs and calls StudentManager.register()
    - Implement handleBack method to navigate to main menu
    - Add error handling with AlertHelper for invalid inputs and failed operations
    - Navigate to student dashboard on successful login/registration
    - _Requirements: 1.5, 3.1, 8.1, 8.2, 8.4_

- [x] 5. Implement student dashboard screen




  - [x] 5.1 Create student-dashboard.fxml


    - Design FXML with BorderPane layout
    - Add header HBox with welcome Label and student code Label
    - Add loyalty points display Label with badge styling
    - Add notification badge Label showing unread count
    - Add VBox with four action buttons (View Menu, Check Points, Order History, Notifications)
    - Add Logout button
    - Apply fx:id and onAction attributes
    - _Requirements: 2.1, 2.4, 2.5, 3.1, 3.5_

  - [x] 5.2 Create StudentDashboardController.java

    - Implement setStudent method to receive Student object from navigation
    - Implement initialize method to load student data and update UI
    - Implement handleViewMenu to navigate to menu browse screen
    - Implement handleCheckPoints to display loyalty points in a dialog
    - Implement handleOrderHistory to navigate to order history screen
    - Implement handleNotifications to navigate to notifications screen
    - Implement handleLogout to return to main menu
    - Load unread notification count from NotificationHistoryService
    - _Requirements: 1.2, 3.1, 3.5, 8.1, 8.2_

- [x] 6. Implement menu browsing and order placement screens





  - [x] 6.1 Create menu-browse.fxml


    - Design FXML with BorderPane layout
    - Add TableView in center with columns for ID, Name, Description, Price, Category
    - Add TableColumn with Spinner for quantity selection
    - Add TableColumn with "Add to Cart" button for each row
    - Add VBox on right side for shopping cart display
    - Add ListView for cart items with quantity and remove button
    - Add Labels for subtotal, loyalty points available, and total
    - Add TextField for points redemption input
    - Add Checkout button and Back button
    - Apply fx:id and onAction attributes
    - _Requirements: 2.1, 2.4, 2.5, 3.2, 3.3_
  - [x] 6.2 Create MenuBrowseController.java


    - Implement setStudent method to receive Student object
    - Implement initialize method to load menu items from MenuManager into TableView
    - Implement handleAddToCart to add selected item with quantity to cart
    - Implement handleRemoveFromCart to remove item from cart
    - Implement updateCartDisplay to refresh cart ListView and totals
    - Implement handleRedeemPoints to calculate discount based on points input
    - Implement handleCheckout to navigate to payment screen with order data
    - Implement handleBack to return to student dashboard
    - _Requirements: 1.2, 3.2, 3.3, 8.1, 8.2, 8.4_
  - [x] 6.3 Create order-cart.fxml for payment processing


    - Design FXML with VBox layout showing order summary
    - Add ListView displaying cart items with quantities and prices
    - Add Labels for subtotal, discount, and final total
    - Add ComboBox for payment method selection
    - Add Confirm Payment button and Cancel button
    - Apply fx:id and onAction attributes
    - _Requirements: 2.1, 2.4, 2.5, 3.3_
  - [x] 6.4 Create OrderCartController.java


    - Implement setOrderData method to receive cart items, student, and discount
    - Implement initialize method to populate order summary and payment methods from PaymentRegistry
    - Implement handleConfirmPayment to create Order via OrderProcessor.placeOrder()
    - Call OrderProcessor.completeOrderWithLoyalty() with Payment object
    - Display success or error message based on PaymentResult
    - Navigate back to student dashboard on success
    - Implement handleCancel to return to menu browse screen
    - _Requirements: 1.2, 1.5, 3.3, 8.1, 8.2, 8.4_

- [x] 7. Implement order history screen






  - [x] 7.1 Create order-history.fxml

    - Design FXML with VBox layout
    - Add title Label "Order History"
    - Add TableView with columns for Order Code, Date, Total Amount, Payment Method, Status
    - Add Back button
    - Apply fx:id and onAction attributes
    - _Requirements: 2.1, 2.4, 2.5, 3.4_

  - [x] 7.2 Create OrderHistoryController.java

    - Implement setStudent method to receive Student object
    - Implement initialize method to load order history from OrderHistoryService
    - Populate TableView with OrderHistoryRecord objects
    - Implement handleBack to return to student dashboard
    - _Requirements: 1.2, 3.4, 8.1, 8.2_

- [x] 8. Implement notifications screen




  - [x] 8.1 Create notifications.fxml


    - Design FXML with VBox layout
    - Add title Label "Your Notifications"
    - Add ListView displaying notifications with message type, date, message, and read status
    - Add "Mark All as Read" button
    - Add Back button
    - Apply fx:id and onAction attributes
    - _Requirements: 2.1, 2.4, 2.5_

  - [x] 8.2 Create NotificationsController.java

    - Implement setStudent method to receive Student object
    - Implement initialize method to load notifications from NotificationHistoryService
    - Populate ListView with NotificationRecord objects
    - Implement handleMarkAllRead to call markAllNotificationsAsRead()
    - Implement handleBack to return to student dashboard
    - _Requirements: 1.2, 8.1, 8.2_

- [x] 9. Implement admin authentication and dashboard screens





  - [x] 9.1 Create admin-login.fxml


    - Design FXML with VBox layout
    - Add title Label "Admin Login"
    - Add TextField for username
    - Add PasswordField for password
    - Add Login button and Back button
    - Apply fx:id and onAction attributes
    - _Requirements: 2.1, 2.4, 2.5, 4.1_
  - [x] 9.2 Create AdminLoginController.java


    - Implement handleLogin to validate inputs and call AdminLIN_Out.performLogin()
    - Navigate to admin dashboard on successful login with Admin object
    - Display error message for invalid credentials
    - Implement handleBack to return to main menu
    - _Requirements: 1.2, 1.5, 4.1, 8.1, 8.2_
  - [x] 9.3 Create admin-dashboard.fxml


    - Design FXML with VBox layout
    - Add title Label "Admin Dashboard"
    - Add welcome Label with admin name
    - Add two main action buttons (Manage Service Managers, View System Reports)
    - Add Logout button
    - Apply fx:id and onAction attributes
    - _Requirements: 2.1, 2.4, 2.5, 4.1_
  - [x] 9.4 Create AdminDashboardController.java


    - Implement setAdmin method to receive Admin object
    - Implement initialize method to display admin name
    - Implement handleManageManagers to navigate to manager management screen
    - Implement handleViewReports to navigate to reports screen
    - Implement handleLogout to call AdminLIN_Out.performLogout() and return to main menu
    - _Requirements: 1.2, 4.1, 8.1, 8.2_

- [x] 10. Implement service manager management screen












  - [x] 10.1 Create manager-management.fxml


    - Design FXML with BorderPane layout
    - Add title Label "Service Manager Administration"
    - Add ComboBox for ManagerType selection at top
    - Add TableView in center with columns for ID, Name, Phone Number, Username
    - Add action buttons on right side (Add Manager, Update Manager, Delete Manager, View Statistics)
    - Add form panel at bottom (initially hidden) with TextFields for name, phone, username, and PasswordField for password
    - Add Save and Cancel buttons in form panel
    - Add Back button
    - Apply fx:id and onAction attributes
    - _Requirements: 2.1, 2.4, 2.5, 4.2_
  - [x] 10.2 Create ManagerManagementController.java


    - Implement initialize method to populate ManagerType ComboBox
    - Implement handleTypeSelection to load managers of selected type from AdminManagement_Services
    - Implement handleAddManager to show form panel for new manager
    - Implement handleUpdateManager to show form panel with selected manager data
    - Implement handleDeleteManager to show confirmation dialog and call AdminManagement_Services.deleteManager()
    - Implement handleViewStatistics to display manager counts by type in a dialog
    - Implement handleSave to validate form and call addManager() or updateManager()
    - Implement handleCancel to hide form panel
    - Implement handleBack to return to admin dashboard
    - _Requirements: 1.2, 1.5, 4.2, 4.4, 4.5, 8.1, 8.2, 8.4_

- [x] 11. Implement system reports screen






  - [x] 11.1 Create reports.fxml

    - Design FXML with VBox layout
    - Add title Label "System Reports"
    - Add GridPane with four dashboard cards displaying Total Revenue, Total Students, Total Menu Items, Total Orders
    - Add TableView for manager statistics with columns for Manager Type and Count
    - Add Back button
    - Apply fx:id and onAction attributes
    - _Requirements: 2.1, 2.4, 2.5, 4.3_
  - [x] 11.2 Create ReportsController.java


    - Implement initialize method to load summary metrics from ReportService
    - Display metrics in dashboard card Labels
    - Load manager statistics from AdminManagement_Services
    - Populate TableView with manager counts by type
    - Implement handleBack to return to admin dashboard
    - _Requirements: 1.2, 4.3, 4.4, 8.1, 8.2_

- [x] 12. Implement service manager authentication and portal screens





  - [x] 12.1 Create service-login.fxml


    - Design FXML with VBox layout
    - Add title Label "Service Manager Login"
    - Add ComboBox for ManagerType selection
    - Add TextField for username
    - Add PasswordField for password
    - Add Login button and Back button
    - Apply fx:id and onAction attributes
    - _Requirements: 2.1, 2.4, 2.5, 5.1_

  - [x] 12.2 Create ServiceLoginController.java

    - Implement handleLogin to validate inputs and authenticate via RoleAuthService
    - Navigate to service dashboard on successful login with ServicesManager object and ManagerType
    - Display error message for invalid credentials
    - Implement handleBack to return to main menu
    - _Requirements: 1.2, 1.5, 5.1, 8.1, 8.2_

  - [x] 12.3 Create service-dashboard.fxml

    - Design FXML with BorderPane layout
    - Add title Label "Service Manager Portal"
    - Add welcome Label with manager name and type
    - Add VBox for dynamic action buttons (content changes based on manager type)
    - Add content area (center) that displays different forms/tables based on selected action
    - Add Logout button
    - Apply fx:id and onAction attributes
    - _Requirements: 2.1, 2.4, 2.5, 5.1, 5.2, 5.3, 5.4, 5.5_


  - [x] 12.4 Create ServiceDashboardController.java
    - Implement setManagerData method to receive ServicesManager and ManagerType
    - Implement initialize method to display manager name and type
    - Implement loadActionsForManagerType to dynamically create buttons based on ManagerType
    - Implement action handlers for Menu Manager (add/update/remove menu items, view menu)
    - Implement action handlers for Order Manager (view orders, update status, process orders)
    - Implement action handlers for Payment Manager (view payments, manage payment methods)
    - Implement action handlers for Notification Manager (send notifications, view notifications)
    - Implement action handlers for Report Manager (generate reports, view analytics)
    - Implement action handlers for Student Manager (view students, manage students)
    - Implement handleLogout to return to main menu
    - _Requirements: 1.2, 5.1, 5.2, 5.3, 5.4, 5.5, 8.1, 8.2_

- [x] 13. Implement IT admin authentication and portal screens



  - [x] 13.1 Create itadmin-login.fxml
    - Design FXML with VBox layout
    - Add title Label "IT Admin Login"
    - Add TextField for username
    - Add PasswordField for password
    - Add Login button and Back button
    - Apply fx:id and onAction attributes
    - _Requirements: 2.1, 2.4, 2.5, 6.1_
  - [x] 13.2 Create ITAdminLoginController.java

    - Implement handleLogin to validate inputs and authenticate via RoleAuthService
    - Navigate to IT admin dashboard on successful login
    - Display error message for invalid credentials
    - Implement handleBack to return to main menu
    - _Requirements: 1.2, 1.5, 6.1, 8.1, 8.2_

  - [x] 13.3 Create itadmin-dashboard.fxml

    - Design FXML with BorderPane layout
    - Add title Label "IT Admin Portal"
    - Add welcome Label
    - Add TableView displaying administrators with columns for ID, Username, Name
    - Add action buttons (Add Administrator, Update Administrator, Delete Administrator)
    - Add form panel at bottom (initially hidden) with TextFields for username, name, and PasswordField for password
    - Add Save and Cancel buttons in form panel
    - Add Logout button
    - Apply fx:id and onAction attributes
    - _Requirements: 2.1, 2.4, 2.5, 6.1, 6.2, 6.3_

  - [x] 13.4 Create ITAdminDashboardController.java

    - Implement initialize method to load all administrators from AdminManager
    - Populate TableView with Admin objects
    - Implement handleAddAdmin to show form panel for new administrator
    - Implement handleUpdateAdmin to show form panel with selected admin data
    - Implement handleDeleteAdmin to show confirmation dialog and call AdminManager delete method
    - Implement handleSave to validate form and call add or update method
    - Implement handleCancel to hide form panel
    - Implement handleLogout to return to main menu
    - _Requirements: 1.2, 1.5, 6.1, 6.2, 6.3, 6.4, 6.5, 8.1, 8.2_

- [ ] 14. Integrate and test complete application flow





  - Update Main.java or create MainGUI.java to launch CafeteriaGUIApp
  - Test navigation between all screens
  - Verify all FXML files load correctly in Scene Builder
  - Test student registration, login, menu browsing, order placement, and payment flow
  - Test admin login, manager management, and reports viewing
  - Test service manager login and role-specific operations
  - Test IT admin login and administrator management
  - Verify error handling displays appropriate messages
  - Verify styling is consistent across all screens
  - Test with actual database to ensure data persistence
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 8.1, 8.2, 8.3, 8.4, 8.5_
