# Cafeteria Management System - JavaFX GUI

## Overview
This package contains the JavaFX-based graphical user interface for the Cafeteria Management System. The GUI provides a modern, user-friendly alternative to the terminal-based interface while maintaining all the same functionality.

## Project Structure

```
src/app/gui/
├── CafeteriaGUIApp.java       # Main JavaFX application class
├── MainGUI.java               # Entry point for launching the GUI
├── NavigationService.java     # Singleton service for scene navigation
├── AlertHelper.java           # Utility for displaying alerts and dialogs
└── controllers/               # Controller classes for each screen
    ├── MainMenuController.java
    ├── StudentLoginController.java
    ├── StudentDashboardController.java
    ├── MenuBrowseController.java
    ├── OrderCartController.java
    ├── OrderHistoryController.java
    ├── NotificationsController.java
    ├── AdminLoginController.java
    ├── AdminDashboardController.java
    ├── ManagerManagementController.java
    ├── ReportsController.java
    ├── ServiceLoginController.java
    ├── ServiceDashboardController.java
    ├── ITAdminLoginController.java
    └── ITAdminDashboardController.java

src/app/resources/
├── fxml/                      # FXML layout files (16 files)
│   ├── main-menu.fxml
│   ├── student-login.fxml
│   ├── student-dashboard.fxml
│   ├── menu-browse.fxml
│   ├── order-cart.fxml
│   ├── order-history.fxml
│   ├── notifications.fxml
│   ├── admin-login.fxml
│   ├── admin-dashboard.fxml
│   ├── manager-management.fxml
│   ├── reports.fxml
│   ├── service-login.fxml
│   ├── service-dashboard.fxml
│   ├── itadmin-login.fxml
│   └── itadmin-dashboard.fxml
├── css/
│   └── cafeteria-theme.css   # Application stylesheet
└── images/
    └── icons/                 # Icon images
```

## Core Components

### CafeteriaGUIApp
The main JavaFX application class that:
- Extends `javafx.application.Application`
- Initializes the ServiceContainer with all business services
- Sets up the primary stage
- Launches the main menu screen

### NavigationService
A singleton service that manages:
- Scene transitions between different screens
- Loading FXML files and initializing controllers
- Caching scenes for performance
- Passing data between controllers
- Access to the ServiceContainer

### AlertHelper
A utility class providing:
- Success, error, warning, and info dialogs
- Confirmation dialogs with custom buttons
- Consistent styling across all alerts

## Running the GUI

### Prerequisites
- JavaFX SDK must be installed and configured
- MySQL database must be running with the cafeteria schema
- All dependencies must be on the classpath

### Launch Command
```bash
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -cp . app.gui.MainGUI
```

Or use the provided batch file:
```bash
start-gui.bat
```

## Integration with Existing Code

The GUI seamlessly integrates with the existing codebase:
- Uses the same `ServiceContainer` for dependency injection
- Reuses all existing domain models, services, and managers
- No duplication of business logic
- Maintains the same data flow and validation rules

## Development Guidelines

### Adding New Screens
1. Create an FXML file in `src/app/resources/fxml/`
2. Create a controller class in `src/app/gui/controllers/`
3. Use `NavigationService.navigateTo("screen-name")` to navigate
4. Access services via `NavigationService.getServiceContainer()`

### Controller Pattern
All controllers should follow this pattern:
```java
public class MyController {
    @FXML private Label myLabel;
    private ServiceContainer services;
    
    @FXML
    public void initialize() {
        services = NavigationService.getServiceContainer();
        // Setup logic
    }
    
    @FXML
    private void handleAction() {
        // Event handler
    }
}
```

### Error Handling
Always wrap service calls in try-catch blocks:
```java
try {
    // Service call
} catch (Exception e) {
    AlertHelper.showError("Error", e.getMessage());
}
```

## Implementation Status

✓ **COMPLETE** - All screens have been implemented:
1. ✓ Main Menu
2. ✓ Student Portal (login, dashboard, menu browsing, order placement, history, notifications)
3. ✓ Admin Dashboard (manager management, reports)
4. ✓ Service Manager Portal (role-specific operations)
5. ✓ IT Admin Portal (administrator management)

### Implemented Screens (16 total)
- main-menu.fxml + MainMenuController.java
- student-login.fxml + StudentLoginController.java
- student-dashboard.fxml + StudentDashboardController.java
- menu-browse.fxml + MenuBrowseController.java
- order-cart.fxml + OrderCartController.java
- order-history.fxml + OrderHistoryController.java
- notifications.fxml + NotificationsController.java
- admin-login.fxml + AdminLoginController.java
- admin-dashboard.fxml + AdminDashboardController.java
- manager-management.fxml + ManagerManagementController.java
- reports.fxml + ReportsController.java
- service-login.fxml + ServiceLoginController.java
- service-dashboard.fxml + ServiceDashboardController.java
- itadmin-login.fxml + ITAdminLoginController.java
- itadmin-dashboard.fxml + ITAdminDashboardController.java

## Testing and Documentation

See the following guides in the project root:
- **GUI-QUICK-START.md**: Quick start guide for running the application
- **GUI-TESTING-GUIDE.md**: Comprehensive testing procedures
- **GUI-INTEGRATION-CHECKLIST.md**: Integration verification checklist
- **GUI-INTEGRATION-SUMMARY.md**: Complete integration summary
- **JAVAFX-SETUP.md**: Detailed JavaFX setup instructions
