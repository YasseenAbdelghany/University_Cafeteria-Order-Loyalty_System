# Design Document - Dual Application Enhancement

## Overview

This design document outlines the architecture and implementation approach for splitting the Cafeteria Management System into two separate JavaFX applications: a Student Application and an Administrative Application. The design focuses on code reuse, clear separation of concerns, and enhanced user experience.

## Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Cafeteria Management System               │
├─────────────────────────────┬───────────────────────────────┤
│     Student Application     │   Administrative Application   │
│                             │                                │
│  - StudentGUIApp            │  - AdminGUIApp                 │
│  - Student Login/Register   │  - Unified Login               │
│  - Menu Dashboard           │  - Role-Based Routing          │
│  - Order Placement          │  - Admin Dashboard             │
│  - Notifications            │  - Service Manager Dashboard   │
│  - Order History            │  - IT Admin Dashboard          │
└─────────────────────────────┴───────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    Shared Components                         │
│                                                              │
│  - ServiceContainer (Dependency Injection)                   │
│  - Business Services (MenuManager, OrderProcessor, etc.)     │
│  - Data Access Objects (DAOs)                                │
│  - Domain Models (Student, Order, MenuItem, etc.)            │
│  - Database Connection                                       │
└─────────────────────────────────────────────────────────────┘
```

### Application Separation Strategy

**Two Independent JavaFX Applications:**
1. **StudentGUIApp** - Dedicated student interface
2. **AdminGUIApp** - Unified administrative interface

**Shared Infrastructure:**
- Both applications share the same business logic layer
- Both applications use the same ServiceContainer
- Both applications connect to the same database
- Common utility classes (AlertHelper) are shared

## Components and Interfaces

### Student Application Components

#### 1. StudentGUIApp (Main Application Class)
```java
package app.gui.student;

public class StudentGUIApp extends Application {
    - ServiceContainer services
    - Stage primaryStage
    
    + void start(Stage stage)
    + void stop()
    + static void main(String[] args)
}
```

**Responsibilities:**
- Initialize ServiceContainer
- Set up primary stage
- Navigate to student login screen
- Manage application lifecycle

#### 2. StudentNavigationService
```java
package app.gui.student;

public class StudentNavigationService {
    - static Stage stage
    - static ServiceContainer serviceContainer
    - static Map<String, Scene> sceneCache
    
    + static void initialize(Stage, ServiceContainer)
    + static void navigateTo(String sceneName)
    + static void navigateToWithData(String sceneName, Object data)
}
```

**Responsibilities:**
- Manage navigation between student screens
- Cache scenes for performance
- Pass data between controllers

#### 3. StudentLoginController
```java
package app.gui.student.controllers;

public class StudentLoginController {
    - TextField studentCodeField (Login tab)
    - TextField nameField (Register tab)
    - TextField phoneField (Register tab)
    - TabPane loginRegisterTabPane
    
    + void handleLogin()
    + void handleRegister()
}
```

**Responsibilities:**
- Handle student login authentication
- Handle new student registration
- Navigate to menu dashboard after successful auth

#### 4. MenuDashboardController (Enhanced)
```java
package app.gui.student.controllers;

public class MenuDashboardController {
    - TableView<MenuItem> menuTable
    - ListView<String> cartList
    - Button notificationButton
    - Label notificationBadge
    - Button viewOrderHistoryButton
    - Button checkoutButton
    - Button logoutButton
    
    + void initialize()
    + void setStudent(Student)
    + void loadMenuItems()
    + void updateNotificationBadge()
    + void handleAddToCart(MenuItem)
    + void handleCheckout()
    + void handleViewNotifications()
    + void handleViewOrderHistory()
    + void handleLogout()
}
```

**Responsibilities:**
- Display menu items
- Manage shopping cart
- Show notification badge with unread count
- Provide navigation to notifications and order history
- Handle order placement workflow

#### 5. Student Screens
- **student-login.fxml** - Login and registration tabs
- **menu-dashboard.fxml** - Main menu with cart and navigation
- **order-payment.fxml** - Payment processing
- **notifications.fxml** - Notification list
- **order-history.fxml** - Past orders

### Administrative Application Components

#### 1. AdminGUIApp (Main Application Class)
```java
package app.gui.admin;

public class AdminGUIApp extends Application {
    - ServiceContainer services
    - Stage primaryStage
    
    + void start(Stage stage)
    + void stop()
    + static void main(String[] args)
}
```

**Responsibilities:**
- Initialize ServiceContainer
- Set up primary stage
- Navigate to unified login screen
- Manage application lifecycle

#### 2. AdminNavigationService
```java
package app.gui.admin;

public class AdminNavigationService {
    - static Stage stage
    - static ServiceContainer serviceContainer
    - static Map<String, Scene> sceneCache
    
    + static void initialize(Stage, ServiceContainer)
    + static void navigateTo(String sceneName)
    + static void navigateToWithData(String sceneName, Object data)
}
```

**Responsibilities:**
- Manage navigation between administrative screens
- Cache scenes for performance
- Pass data between controllers

#### 3. UnifiedLoginController
```java
package app.gui.admin.controllers;

public class UnifiedLoginController {
    - TextField usernameField
    - PasswordField passwordField
    - Button loginButton
    
    + void handleLogin()
    - UserRole detectUserRole(String username, String password)
    - void navigateToRoleDashboard(UserRole role, Object user)
}
```

**Responsibilities:**
- Authenticate users against all administrative types
- Detect user role (Admin, Service Manager, IT Admin)
- Route to appropriate dashboard based on role
- Handle IT Admin root account (username: "root", password: "root")

#### 4. Role Detection Logic
```java
public enum UserRole {
    ADMIN,
    SERVICE_MANAGER,
    IT_ADMIN,
    UNKNOWN
}

private UserRole detectUserRole(String username, String password) {
    // Check IT Admin first (root account)
    if ("root".equals(username) && "root".equals(password)) {
        return UserRole.IT_ADMIN;
    }
    
    // Try IT Admin authentication
    if (roleAuthService.authenticateITAdmin(username, password)) {
        return UserRole.IT_ADMIN;
    }
    
    // Try Admin authentication
    if (roleAuthService.authenticateAdmin(username, password)) {
        return UserRole.ADMIN;
    }
    
    // Try Service Manager authentication
    for (ManagerType type : ManagerType.values()) {
        if (roleAuthService.login(type.toString(), username, password)) {
            return UserRole.SERVICE_MANAGER;
        }
    }
    
    return UserRole.UNKNOWN;
}
```

#### 5. Administrative Screens
- **unified-login.fxml** - Single login for all admin users
- **admin-dashboard.fxml** - Admin management interface
- **service-dashboard.fxml** - Service manager interface
- **itadmin-dashboard.fxml** - IT admin interface
- **manager-management.fxml** - Manager CRUD
- **reports.fxml** - System reports

## Data Models

### Existing Models (Reused)
- **Student** - Student entity with account and loyalty points
- **Admin** - Administrator entity
- **ServicesManager** - Service manager entity
- **MenuItem** - Menu item entity
- **Order** - Order entity
- **NotificationHistory** - Notification entity

### New Data Transfer Objects

#### UserAuthResult
```java
public class UserAuthResult {
    private UserRole role;
    private Object userData; // Can be Admin, ServicesManager, or ITAdmin
    private ManagerType managerType; // Only for Service Managers
    
    // Getters and setters
}
```

## User Interface Design

### Student Application UI Flow

```
┌─────────────────────┐
│  Student Login      │
│  ┌───────────────┐  │
│  │ Login    │ Register │
│  └───────────────┘  │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────────────────────┐
│  Menu Dashboard                     │
│  ┌─────────────────────────────┐   │
│  │ 🔔 Notifications (3)        │   │  ← Notification badge
│  │                             │   │
│  │  Menu Items Table           │   │
│  │  ┌─────────────────────┐   │   │
│  │  │ Item | Price | Add  │   │   │
│  │  └─────────────────────┘   │   │
│  │                             │   │
│  │  Shopping Cart              │   │
│  │  ┌─────────────────────┐   │   │
│  │  │ Items in cart       │   │   │
│  │  └─────────────────────┘   │   │
│  │                             │   │
│  │  [Checkout]                 │   │
│  │                             │   │
│  │  [View Order History]       │   │  ← Bottom button
│  │  [Logout]                   │   │
│  └─────────────────────────────┘   │
└─────────────────────────────────────┘
```

### Administrative Application UI Flow

```
┌─────────────────────┐
│  Unified Login      │
│  ┌───────────────┐  │
│  │ Username      │  │
│  │ Password      │  │
│  │ [Login]       │  │
│  └───────────────┘  │
└──────────┬──────────┘
           │
           ├─────────────────┬─────────────────┐
           ▼                 ▼                 ▼
    ┌──────────┐      ┌──────────┐     ┌──────────┐
    │  Admin   │      │ Service  │     │ IT Admin │
    │Dashboard │      │Dashboard │     │Dashboard │
    └──────────┘      └──────────┘     └──────────┘
```

## Navigation Flow

### Student Application Navigation

```
student-login
    │
    ├─ [Login Success] → menu-dashboard
    │                        │
    │                        ├─ [Checkout] → order-payment
    │                        │                   │
    │                        │                   └─ [Complete] → menu-dashboard
    │                        │
    │                        ├─ [Notifications] → notifications
    │                        │                        │
    │                        │                        └─ [Back] → menu-dashboard
    │                        │
    │                        ├─ [Order History] → order-history
    │                        │                        │
    │                        │                        └─ [Back] → menu-dashboard
    │                        │
    │                        └─ [Logout] → student-login
    │
    └─ [Register Success] → menu-dashboard
```

### Administrative Application Navigation

```
unified-login
    │
    ├─ [Admin Login] → admin-dashboard
    │                      │
    │                      ├─ [Manage Managers] → manager-management
    │                      │                           │
    │                      │                           └─ [Back] → admin-dashboard
    │                      │
    │                      ├─ [View Reports] → reports
    │                      │                      │
    │                      │                      └─ [Back] → admin-dashboard
    │                      │
    │                      └─ [Logout] → unified-login
    │
    ├─ [Service Manager Login] → service-dashboard
    │                                 │
    │                                 └─ [Logout] → unified-login
    │
    └─ [IT Admin Login] → itadmin-dashboard
                              │
                              └─ [Logout] → unified-login
```

## Error Handling

### Authentication Errors
- Invalid credentials → Display error message, remain on login screen
- Role detection failure → Display error message, remain on login screen
- Database connection error → Display error dialog with retry option

### Navigation Errors
- Missing FXML file → Display error dialog, log error, attempt fallback
- Controller initialization error → Display error dialog, return to previous screen
- Data passing error → Log warning, continue with null data handling

### Business Logic Errors
- Order placement failure → Display error message, allow retry
- Menu loading failure → Display error message, show empty state
- Notification loading failure → Log error, show zero badge

## Testing Strategy

### Unit Testing
- Test role detection logic with various credentials
- Test notification badge count calculation
- Test navigation routing logic
- Test authentication against all user types

### Integration Testing
- Test student login → menu dashboard flow
- Test admin login → role-based dashboard routing
- Test IT Admin root account authentication
- Test notification badge updates after reading notifications
- Test order placement from menu dashboard

### UI Testing
- Verify notification badge displays correct count
- Verify menu dashboard shows all required elements
- Verify unified login routes to correct dashboards
- Verify order history button placement
- Verify absence of loyalty points button

### End-to-End Testing
- Complete student journey: register → browse → order → view history
- Complete admin journey: login → manage → logout
- Complete service manager journey: login → perform tasks → logout
- Complete IT admin journey: login with root → manage → logout

## Performance Considerations

### Scene Caching
- Cache frequently accessed scenes (menu dashboard, dashboards)
- Clear cache on logout to free memory
- Reload scenes when data needs to be refreshed

### Lazy Loading
- Load menu items on demand
- Load notifications only when badge is displayed
- Load order history only when screen is accessed

### Database Optimization
- Use connection pooling for both applications
- Cache user session data to minimize queries
- Batch notification count queries

## Security Considerations

### Authentication
- Validate all credentials against database
- Use secure password comparison
- Implement session timeout for inactive users
- Clear sensitive data on logout

### Authorization
- Verify user role before displaying dashboards
- Prevent direct navigation to unauthorized screens
- Validate user permissions for all operations

### IT Admin Root Account
- Document root account credentials securely
- Consider allowing root password change in production
- Log all root account access for auditing

## Deployment

### Build Configuration
- Create separate JAR files for each application
- Include shared libraries in both JARs
- Provide separate launch scripts:
  - `start-student-gui.bat`
  - `start-admin-gui.bat`

### Installation
- Deploy both applications to separate directories (optional)
- Share database configuration between applications
- Provide separate desktop shortcuts for each application

### Configuration
- Use shared database connection configuration
- Allow separate JavaFX SDK paths if needed
- Support separate logging configurations

## Migration Strategy

### Phase 1: Create New Application Structure
- Create student and admin package structures
- Duplicate and modify main application classes
- Create separate navigation services

### Phase 2: Refactor Controllers
- Move student controllers to student package
- Move admin controllers to admin package
- Update imports and references

### Phase 3: Update FXML Files
- Create menu-dashboard.fxml from menu-browse.fxml
- Create unified-login.fxml
- Update controller references in FXML files

### Phase 4: Implement New Features
- Add notification badge to menu dashboard
- Implement unified login with role detection
- Remove loyalty points button
- Add order history button to menu dashboard

### Phase 5: Testing and Validation
- Test both applications independently
- Verify shared services work correctly
- Validate all navigation flows
- Perform end-to-end testing

### Phase 6: Documentation and Deployment
- Update user documentation
- Create deployment guides
- Provide training materials
- Deploy to production

## Future Enhancements

- Add real-time notification updates using WebSocket
- Implement push notifications for order status
- Add biometric authentication for students
- Implement single sign-on (SSO) for administrators
- Add mobile application support
- Implement offline mode for students

