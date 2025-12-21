# Implementation Plan - Dual Application Enhancement

## Overview
This implementation plan breaks down the dual application enhancement into discrete, manageable coding tasks. Each task builds incrementally on previous work to split the system into Student and Administrative applications with enhanced user experience.

---

## Phase 1: Project Structure and Shared Components

- [x] 1. Create new package structure for dual applications





  - Create `app.gui.student` package for Student Application
  - Create `app.gui.admin` package for Administrative Application
  - Create `app.gui.shared` package for shared utilities
  - _Requirements: 9.1, 9.4, 10.1_

- [x] 2. Move shared utilities to common package





  - Move `AlertHelper.java` to `app.gui.shared` package
  - Update all imports in existing controllers
  - Verify no compilation errors
  - _Requirements: 9.5, 10.1_

---

## Phase 2: Student Application Core

- [x] 3. Create Student Application main class and navigation






  - [x] 3.1 Create `StudentGUIApp.java` in `app.gui.student` package

    - Extend JavaFX Application class
    - Initialize ServiceContainer
    - Set up primary stage with title "Cafeteria - Student Portal"
    - Navigate to student-login screen on startup
    - _Requirements: 1.7, 9.1, 9.4_
  

  - [x] 3.2 Create `StudentNavigationService.java` in `app.gui.student` package

    - Implement scene caching with Map<String, Scene>
    - Implement navigateTo() and navigateToWithData() methods
    - Load FXML files from `/app/resources/fxml/student/` directory
    - _Requirements: 1.7, 9.4_
  

  - [x] 3.3 Create `start-student-gui.bat` launch script

    - Copy and modify existing start-gui.bat
    - Update main class to `app.gui.student.StudentGUIApp`
    - Add appropriate comments and error handling
    - _Requirements: 9.3_

- [x] 4. Create enhanced student login screen






  - [x] 4.1 Create `student-login.fxml` in `/app/resources/fxml/student/`

    - Design TabPane with "Login" and "Register" tabs
    - Login tab: student code field, login button
    - Register tab: name field, phone field, register button
    - Apply cafeteria-theme.css styling
    - _Requirements: 1.1, 6.2_
  
  - [x] 4.2 Create `StudentLoginController.java` in `app.gui.student.controllers`


    - Implement handleLogin() method using StudentManager
    - Implement handleRegister() method using StudentManager
    - Navigate to menu-dashboard on successful login/register
    - Display error messages for failed authentication
    - _Requirements: 1.1, 6.1, 6.2_

---

## Phase 3: Enhanced Menu Dashboard

- [x] 5. Create menu dashboard with integrated features





  - [x] 5.1 Create `menu-dashboard.fxml` in `/app/resources/fxml/student/`


    - Add notification button with badge label at top
    - Add TableView for menu items (ID, Name, Description, Price, Category, Quantity, Add button)
    - Add ListView for shopping cart on right side
    - Add subtotal, discount, total labels
    - Add checkout button
    - Add "View Order History" button at bottom
    - Add logout button
    - Do NOT include "Check Loyalty Points" button
    - Apply responsive layout with BorderPane or VBox
    - _Requirements: 6.3, 6.4, 6.5, 7.1, 8.1, 8.2, 8.5_
  
  - [x] 5.2 Create `MenuDashboardController.java` in `app.gui.student.controllers`


    - Implement initialize() method to set up table columns
    - Implement setStudent() method to receive student data
    - Implement loadMenuItems() using MenuManager
    - Implement updateNotificationBadge() using NotificationHistoryService
    - Implement handleAddToCart() for adding items to cart
    - Implement handleCheckout() to navigate to payment screen
    - Implement handleViewNotifications() to navigate to notifications
    - Implement handleViewOrderHistory() to navigate to order history
    - Implement handleLogout() to return to login screen
    - _Requirements: 6.1, 6.2, 6.3, 6.4, 6.5, 7.2, 7.3, 7.4, 7.5, 8.1, 8.3, 8.4, 8.5_

- [x] 6. Implement notification badge functionality





  - [x] 6.1 Add getUnreadNotificationCount() method to MenuDashboardController


    - Query NotificationHistoryService for unread count
    - Return integer count of unread notifications
    - _Requirements: 7.2, 7.3_
  
  - [x] 6.2 Update notification badge display logic


    - Show badge with count when unread notifications > 0
    - Hide badge or show "0" when no unread notifications
    - Style badge with circular background and contrasting color
    - _Requirements: 7.2, 7.3_
  
  - [x] 6.3 Refresh badge count after viewing notifications


    - Update badge when returning from notifications screen
    - Reload count from database
    - _Requirements: 7.5_

---

## Phase 4: Student Application Screens

- [x] 7. Create order payment screen for students






  - [x] 7.1 Create `order-payment.fxml` in `/app/resources/fxml/student/`

    - Display order summary with items and totals
    - Add payment method ComboBox
    - Add loyalty points redemption field
    - Add confirm payment button
    - Add back button to return to menu dashboard
    - _Requirements: 6.5, 8.4_
  
  - [x] 7.2 Create `OrderPaymentController.java` in `app.gui.student.controllers`


    - Receive order data from menu dashboard
    - Display order summary
    - Implement handleConfirmPayment() using OrderProcessor
    - Apply loyalty points discount if specified
    - Navigate back to menu dashboard on success
    - _Requirements: 6.5, 8.4_

- [x] 8. Update notifications screen for student app





  - [x] 8.1 Copy `notifications.fxml` to `/app/resources/fxml/student/`


    - Update controller reference to student package
    - Verify styling is consistent
    - _Requirements: 7.4_
  
  - [x] 8.2 Create `NotificationsController.java` in `app.gui.student.controllers`


    - Copy logic from existing NotificationsController
    - Update navigation to use StudentNavigationService
    - Navigate back to menu-dashboard instead of student-dashboard
    - _Requirements: 7.4, 7.5_

- [x] 9. Update order history screen for student app





  - [x] 9.1 Copy `order-history.fxml` to `/app/resources/fxml/student/`



    - Update controller reference to student package
    - Verify styling is consistent
    - _Requirements: 8.1_
  
  - [x] 9.2 Create `OrderHistoryController.java` in `app.gui.student.controllers`


    - Copy logic from existing OrderHistoryController
    - Update navigation to use StudentNavigationService
    - Navigate back to menu-dashboard instead of student-dashboard
    - _Requirements: 8.1, 8.3_

---

## Phase 5: Administrative Application Core

- [x] 10. Create Administrative Application main class and navigation






  - [x] 10.1 Create `AdminGUIApp.java` in `app.gui.admin` package

    - Extend JavaFX Application class
    - Initialize ServiceContainer
    - Set up primary stage with title "Cafeteria - Administration"
    - Navigate to unified-login screen on startup
    - _Requirements: 2.4, 9.2_
  
  - [x] 10.2 Create `AdminNavigationService.java` in `app.gui.admin` package


    - Implement scene caching with Map<String, Scene>
    - Implement navigateTo() and navigateToWithData() methods
    - Load FXML files from `/app/resources/fxml/admin/` directory
    - _Requirements: 2.4, 9.4_
  
  - [x] 10.3 Create `start-admin-gui.bat` launch script


    - Copy and modify existing start-gui.bat
    - Update main class to `app.gui.admin.AdminGUIApp`
    - Add appropriate comments and error handling
    - _Requirements: 9.3_

---

## Phase 6: Unified Login and Role Detection

- [x] 11. Create unified login screen





  - [x] 11.1 Create `unified-login.fxml` in `/app/resources/fxml/admin/`


    - Add username TextField
    - Add password PasswordField
    - Add login Button
    - Add application title label
    - Do NOT include role selection ComboBox
    - Apply cafeteria-theme.css styling
    - _Requirements: 2.2, 3.1, 3.2_
  

  - [x] 11.2 Create `UnifiedLoginController.java` in `app.gui.admin.controllers`

    - Implement handleLogin() method
    - Call detectUserRole() to determine user type
    - Navigate to appropriate dashboard based on role
    - Display error for invalid credentials
    - _Requirements: 3.2, 3.3, 3.4, 3.5_

- [x] 12. Implement role detection logic






  - [x] 12.1 Create UserRole enum in `app.gui.admin` package

    - Define ADMIN, SERVICE_MANAGER, IT_ADMIN, UNKNOWN values
    - _Requirements: 4.1, 4.2, 4.3_
  
  - [x] 12.2 Implement detectUserRole() method in UnifiedLoginController


    - Check for IT Admin root account first (username: "root", password: "root")
    - Try IT Admin authentication using RoleAuthService
    - Try Admin authentication using RoleAuthService
    - Try Service Manager authentication for each ManagerType
    - Return appropriate UserRole enum value
    - _Requirements: 4.1, 4.2, 4.3, 4.4, 5.1, 5.2, 5.3, 5.4_
  


  - [x] 12.3 Implement navigateToRoleDashboard() method

    - Navigate to admin-dashboard for ADMIN role
    - Navigate to service-dashboard for SERVICE_MANAGER role
    - Navigate to itadmin-dashboard for IT_ADMIN role
    - Display error message for UNKNOWN role
    - Pass user data to dashboard controller
    - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.5, 5.3_

---

## Phase 7: Administrative Dashboards

- [x] 13. Update Admin Dashboard for admin app



  - [x] 13.1 Copy `admin-dashboard.fxml` to `/app/resources/fxml/admin/`

    - Update controller reference to admin package
    - Verify all buttons and labels are present
    - _Requirements: 2.3_
  

  - [x] 13.2 Create `AdminDashboardController.java` in `app.gui.admin.controllers`

    - Copy logic from existing AdminDashboardController
    - Update navigation to use AdminNavigationService
    - Navigate back to unified-login on logout
    - _Requirements: 2.3, 4.1_

- [x] 14. Update Service Manager Dashboard for admin app



  - [x] 14.1 Copy `service-dashboard.fxml` to `/app/resources/fxml/admin/`

    - Update controller reference to admin package
    - Verify dynamic action buttons are configured
    - _Requirements: 2.3_
  
  - [x] 14.2 Create `ServiceDashboardController.java` in `app.gui.admin.controllers`


    - Copy logic from existing ServiceDashboardController
    - Update navigation to use AdminNavigationService
    - Navigate back to unified-login on logout
    - _Requirements: 2.3, 4.2_

- [x] 15. Update IT Admin Dashboard for admin app


  - [x] 15.1 Copy `itadmin-dashboard.fxml` to `/app/resources/fxml/admin/`


    - Update controller reference to admin package
    - Verify administrator management table is configured
    - _Requirements: 2.3, 5.5_
  

  - [x] 15.2 Create `ITAdminDashboardController.java` in `app.gui.admin.controllers`

    - Copy logic from existing ITAdminDashboardController
    - Update navigation to use AdminNavigationService
    - Navigate back to unified-login on logout
    - _Requirements: 2.3, 4.3, 5.5_

---

## Phase 8: Administrative Sub-Screens

- [x] 16. Update Manager Management screen for admin app


  - [x] 16.1 Copy `manager-management.fxml` to `/app/resources/fxml/admin/`


    - Update controller reference to admin package
    - _Requirements: 2.3_
  
  - [x] 16.2 Create `ManagerManagementController.java` in `app.gui.admin.controllers`


    - Copy logic from existing ManagerManagementController
    - Update navigation to use AdminNavigationService
    - Navigate back to admin-dashboard
    - _Requirements: 2.3_

- [x] 17. Update Reports screen for admin app




  - [x] 17.1 Copy `reports.fxml` to `/app/resources/fxml/admin/`

    - Update controller reference to admin package
    - _Requirements: 2.3_
  
  - [x] 17.2 Create `ReportsController.java` in `app.gui.admin.controllers`


    - Copy logic from existing ReportsController
    - Update navigation to use AdminNavigationService
    - Navigate back to admin-dashboard
    - _Requirements: 2.3_

---

## Phase 9: Integration and Testing

- [x] 18. Test Student Application end-to-end





  - Launch Student Application
  - Test student registration flow
  - Test student login flow
  - Verify menu dashboard displays correctly
  - Verify notification badge shows correct count
  - Test adding items to cart and checkout
  - Test viewing notifications
  - Test viewing order history
  - Verify "Check Loyalty Points" button is absent
  - Test logout functionality
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 6.1, 6.2, 6.3, 6.4, 6.5, 7.1, 7.2, 7.3, 7.4, 7.5, 8.1, 8.2, 8.3, 8.4, 8.5_

- [x] 19. Test Administrative Application end-to-end





  - Launch Administrative Application
  - Test unified login with Admin credentials
  - Verify navigation to Admin Dashboard
  - Test unified login with Service Manager credentials
  - Verify navigation to Service Manager Dashboard
  - Test unified login with IT Admin root account (root/root)
  - Verify navigation to IT Admin Dashboard
  - Test invalid credentials error handling
  - Test logout from each dashboard type
  - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5, 3.1, 3.2, 3.3, 3.4, 3.5, 4.1, 4.2, 4.3, 4.4, 4.5, 5.1, 5.2, 5.3, 5.4, 5.5_

- [x] 20. Verify application separation





  - Launch both applications simultaneously
  - Verify they operate independently
  - Verify both can access database concurrently
  - Verify shared services work correctly in both apps
  - Test that changes in one app reflect in the other (e.g., new order)
  - _Requirements: 9.1, 9.2, 9.3, 9.4, 9.5_

- [ ] 21. Verify backward compatibility
  - Verify all existing services work without modification
  - Verify all existing DAOs work without modification
  - Verify all existing domain models work without modification
  - Verify ServiceContainer works in both applications
  - Verify database schema remains unchanged
  - _Requirements: 10.1, 10.2, 10.3, 10.4, 10.5_

---

## Phase 10: Documentation and Deployment

- [ ] 22. Update documentation
  - Update README-GUI.md with dual application information
  - Create STUDENT-APP-GUIDE.md with student application usage
  - Create ADMIN-APP-GUIDE.md with administrative application usage
  - Update JAVAFX-SETUP.md for both applications
  - Document IT Admin root account credentials
  - _Requirements: 5.5, 9.3_

- [ ] 23. Create deployment package
  - Build Student Application JAR
  - Build Administrative Application JAR
  - Create installation directory structure
  - Package launch scripts for both applications
  - Create desktop shortcuts (optional)
  - Test deployment on clean system
  - _Requirements: 9.1, 9.2, 9.3_

---

## Summary

**Total Tasks**: 23 main tasks with 47 sub-tasks
**Estimated Complexity**: High
**Dependencies**: Requires existing JavaFX GUI implementation
**Testing**: Comprehensive end-to-end testing required for both applications

**Key Deliverables**:
1. Student Application (StudentGUIApp) with enhanced menu dashboard
2. Administrative Application (AdminGUIApp) with unified login
3. Notification badge with unread count
4. Role-based authentication and routing
5. IT Admin root account support
6. Separate launch scripts for both applications
7. Updated documentation

