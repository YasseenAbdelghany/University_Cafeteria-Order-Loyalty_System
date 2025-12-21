# Design Document

## Overview

The JavaFX GUI for the Cafeteria Management System will provide a modern, user-friendly graphical interface that replicates all functionality of the existing terminal-based system. The design follows the Model-View-Controller (MVC) pattern with FXML files defining views, Controller classes handling user interactions, and the existing service layer providing business logic.

The GUI will be structured as a multi-scene JavaFX application where each major user flow (Student Portal, Admin Dashboard, Service Manager Portal, IT Admin Portal) has its own FXML layout and controller. Navigation between scenes will be managed through a central navigation service.

## Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────┐
│                   JavaFX Application                     │
│  ┌───────────────────────────────────────────────────┐  │
│  │              Main Application Class                │  │
│  │         (CafeteriaGUIApp extends Application)     │  │
│  └───────────────────────────────────────────────────┘  │
│                          │                               │
│                          ▼                               │
│  ┌───────────────────────────────────────────────────┐  │
│  │            Navigation Service                      │  │
│  │      (Manages scene transitions & stage)          │  │
│  └───────────────────────────────────────────────────┘  │
│                          │                               │
│         ┌────────────────┼────────────────┐             │
│         ▼                ▼                ▼              │
│  ┌──────────┐    ┌──────────┐    ┌──────────┐          │
│  │  FXML    │    │  FXML    │    │  FXML    │          │
│  │  Views   │    │  Views   │    │  Views   │          │
│  └──────────┘    └──────────┘    └──────────┘          │
│         │                │                │              │
│         ▼                ▼                ▼              │
│  ┌──────────┐    ┌──────────┐    ┌──────────┐          │
│  │Controller│    │Controller│    │Controller│          │
│  │ Classes  │    │ Classes  │    │ Classes  │          │
│  └──────────┘    └──────────┘    └──────────┘          │
│         │                │                │              │
│         └────────────────┼────────────────┘             │
│                          ▼                               │
│  ┌───────────────────────────────────────────────────┐  │
│  │           ServiceContainer                         │  │
│  │    (Existing dependency injection container)      │  │
│  └───────────────────────────────────────────────────┘  │
│                          │                               │
│         ┌────────────────┼────────────────┐             │
│         ▼                ▼                ▼              │
│  ┌──────────┐    ┌──────────┐    ┌──────────┐          │
│  │ Student  │    │  Order   │    │  Menu    │          │
│  │ Manager  │    │Processor │    │ Manager  │          │
│  └──────────┘    └──────────┘    └──────────┘          │
│                                                          │
│              (All existing services...)                  │
└─────────────────────────────────────────────────────────┘
```

### Package Structure

```
src/
├── app/
│   ├── gui/
│   │   ├── CafeteriaGUIApp.java          # Main JavaFX application entry point
│   │   ├── NavigationService.java        # Scene navigation manager
│   │   ├── AlertHelper.java              # Utility for showing alerts/dialogs
│   │   └── controllers/
│   │       ├── MainMenuController.java
│   │       ├── StudentLoginController.java
│   │       ├── StudentDashboardController.java
│   │       ├── MenuBrowseController.java
│   │       ├── OrderCartController.java
│   │       ├── OrderHistoryController.java
│   │       ├── AdminLoginController.java
│   │       ├── AdminDashboardController.java
│   │       ├── ManagerManagementController.java
│   │       ├── ReportsController.java
│   │       ├── ServiceLoginController.java
│   │       ├── ServiceDashboardController.java
│   │       ├── ITAdminLoginController.java
│   │       └── ITAdminDashboardController.java
│   └── resources/
│       ├── fxml/
│       │   ├── main-menu.fxml
│       │   ├── student-login.fxml
│       │   ├── student-dashboard.fxml
│       │   ├── menu-browse.fxml
│       │   ├── order-cart.fxml
│       │   ├── order-history.fxml
│       │   ├── admin-login.fxml
│       │   ├── admin-dashboard.fxml
│       │   ├── manager-management.fxml
│       │   ├── reports.fxml
│       │   ├── service-login.fxml
│       │   ├── service-dashboard.fxml
│       │   ├── itadmin-login.fxml
│       │   └── itadmin-dashboard.fxml
│       ├── css/
│       │   └── cafeteria-theme.css
│       └── images/
│           ├── logo.png
│           └── icons/
│               ├── student-icon.png
│               ├── admin-icon.png
│               ├── service-icon.png
│               └── itadmin-icon.png
```

## Components and Interfaces

### 1. Main Application Component

**CafeteriaGUIApp.java**
- Extends `javafx.application.Application`
- Initializes the ServiceContainer
- Sets up the primary stage with initial scene (main menu)
- Configures application-wide settings (title, icon, minimum size)

```java
public class CafeteriaGUIApp extends Application {
    private static ServiceContainer serviceContainer;
    private static Stage primaryStage;
    
    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        serviceContainer = new ServiceContainer();
        NavigationService.initialize(stage, serviceContainer);
        NavigationService.navigateTo("main-menu");
        stage.setTitle("Cafeteria Management System");
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.show();
    }
}
```

### 2. Navigation Service

**NavigationService.java**
- Singleton service managing scene transitions
- Loads FXML files and initializes controllers
- Maintains reference to primary stage and service container
- Provides navigation methods for all screens

```java
public class NavigationService {
    private static Stage stage;
    private static ServiceContainer container;
    private static Map<String, Parent> sceneCache;
    
    public static void initialize(Stage primaryStage, ServiceContainer services);
    public static void navigateTo(String sceneName);
    public static void navigateToWithData(String sceneName, Object data);
    public static ServiceContainer getServiceContainer();
    public static Stage getStage();
}
```

### 3. Alert Helper

**AlertHelper.java**
- Utility class for displaying alerts, confirmations, and dialogs
- Provides styled alerts matching the cafeteria theme
- Methods for success, error, warning, info, and confirmation dialogs

```java
public class AlertHelper {
    public static void showSuccess(String title, String message);
    public static void showError(String title, String message);
    public static void showWarning(String title, String message);
    public static void showInfo(String title, String message);
    public static boolean showConfirmation(String title, String message);
}
```

### 4. Controller Base Pattern

All controllers follow a common pattern:
- Annotated `@FXML` fields for UI components
- `initialize()` method for setup logic
- Event handler methods for user interactions
- Access to ServiceContainer through NavigationService
- Navigation methods to other screens

Example controller structure:
```java
public class StudentDashboardController {
    @FXML private Label welcomeLabel;
    @FXML private Label pointsLabel;
    @FXML private Button viewMenuButton;
    @FXML private Button orderHistoryButton;
    
    private Student currentStudent;
    private ServiceContainer services;
    
    @FXML
    public void initialize() {
        services = NavigationService.getServiceContainer();
        // Setup logic
    }
    
    public void setStudent(Student student) {
        this.currentStudent = student;
        updateUI();
    }
    
    @FXML
    private void handleViewMenu() {
        NavigationService.navigateToWithData("menu-browse", currentStudent);
    }
    
    private void updateUI() {
        welcomeLabel.setText("Welcome, " + currentStudent.getName());
        // Update other UI elements
    }
}
```

## Data Models

The GUI will use all existing domain models without modification:
- `Student` - Student entity with account and loyalty points
- `MenuItem` - Menu item with name, description, price, category
- `Order` - Order entity with items, status, and payment
- `Payment` - Payment information
- `Admin` - Administrator entity
- `ServicesManager` - Service manager base class
- `OrderHistoryRecord` - Order history data
- `NotificationRecord` - Notification data

## Screen Designs

### Main Menu Screen (main-menu.fxml)

Layout:
- Large welcome banner at top with cafeteria logo
- Four large buttons in a 2x2 grid:
  - Student Portal (with student icon)
  - Admin Dashboard (with admin icon)
  - Service Manager Portal (with service icon)
  - IT Admin Portal (with IT icon)
- Exit button at bottom
- Color scheme: Cyan/Yellow/Magenta matching terminal theme

Components:
- `VBox` root container
- `Label` for title banner
- `GridPane` for button layout
- `Button` elements with icons and styled text

### Student Login Screen (student-login.fxml)

Layout:
- Title: "Student Portal"
- Two tabs: "Login" and "Register"
- Login tab:
  - TextField for student code
  - Login button
- Register tab:
  - TextField for name
  - TextField for phone number
  - Register button
- Back button to return to main menu

Components:
- `TabPane` with two tabs
- `TextField` for inputs
- `Button` elements
- `Label` for instructions

### Student Dashboard Screen (student-dashboard.fxml)

Layout:
- Header with welcome message and student code
- Loyalty points display with badge
- Notification badge showing unread count
- Four main action buttons:
  - View Menu & Place Order
  - Check Loyalty Points
  - View Order History
  - View Notifications
- Logout button

Components:
- `BorderPane` layout
- `HBox` for header
- `VBox` for button list
- `Label` elements for info display
- `Button` elements for actions

### Menu Browse & Order Screen (menu-browse.fxml)

Layout:
- Title: "Menu"
- TableView displaying menu items with columns:
  - ID
  - Name
  - Description
  - Price
  - Category
- For each row: quantity spinner and "Add to Cart" button
- Shopping cart panel on right side showing:
  - Selected items with quantities
  - Subtotal
  - Loyalty points available
  - Points redemption section
  - Total after discount
- Checkout button
- Back button

Components:
- `BorderPane` layout
- `TableView<MenuItem>` for menu display
- `Spinner<Integer>` for quantity selection
- `VBox` for cart display
- `ListView<CartItem>` for cart items
- `Label` elements for totals
- `TextField` for points redemption
- `Button` elements

### Order History Screen (order-history.fxml)

Layout:
- Title: "Order History"
- TableView displaying order history with columns:
  - Order Code
  - Date
  - Total Amount
  - Payment Method
  - Status
- Back button

Components:
- `VBox` layout
- `TableView<OrderHistoryRecord>`
- `Button` for back navigation

### Admin Login Screen (admin-login.fxml)

Layout:
- Title: "Admin Login"
- TextField for username
- PasswordField for password
- Login button
- Back button

Components:
- `VBox` layout
- `TextField` and `PasswordField`
- `Button` elements

### Admin Dashboard Screen (admin-dashboard.fxml)

Layout:
- Title: "Admin Dashboard"
- Welcome message with admin name
- Two main sections:
  - Manage Service Managers button
  - View System Reports button
- Logout button

Components:
- `VBox` layout
- `Label` for title and welcome
- `Button` elements

### Manager Management Screen (manager-management.fxml)

Layout:
- Title: "Service Manager Administration"
- ComboBox to select manager type
- TableView displaying managers of selected type with columns:
  - ID
  - Name
  - Phone Number
  - Username
- Action buttons:
  - Add Manager
  - Update Manager
  - Delete Manager
  - View Statistics
- Form panel (shown when adding/updating):
  - TextField for name
  - TextField for phone
  - TextField for username
  - PasswordField for password
  - Save/Cancel buttons
- Back button

Components:
- `BorderPane` layout
- `ComboBox<ManagerType>` for type selection
- `TableView<ServicesManager>`
- `VBox` for form panel
- Multiple `TextField` and `PasswordField`
- `Button` elements

### Reports Screen (reports.fxml)

Layout:
- Title: "System Reports"
- Dashboard cards displaying:
  - Total Revenue (large number with currency)
  - Total Students (with icon)
  - Total Menu Items (with icon)
  - Total Orders (with icon)
- Manager statistics section:
  - Bar chart or table showing manager counts by type
- Back button

Components:
- `GridPane` for dashboard cards
- `Label` elements for metrics
- `TableView` or `BarChart` for manager statistics
- `Button` for back navigation

### Service Manager Login Screen (service-login.fxml)

Layout:
- Title: "Service Manager Login"
- ComboBox to select manager type
- TextField for username
- PasswordField for password
- Login button
- Back button

Components:
- `VBox` layout
- `ComboBox<ManagerType>`
- `TextField` and `PasswordField`
- `Button` elements

### Service Manager Dashboard Screen (service-dashboard.fxml)

Layout:
- Title: "Service Manager Portal"
- Welcome message with manager name and type
- Dynamic button list based on manager type:
  - Menu Manager: Add Item, Update Item, Remove Item, View Menu
  - Order Manager: View Orders, Update Order Status, Process Orders
  - Payment Manager: View Payments, Payment Methods
  - Notification Manager: Send Notification, View Notifications
  - Report Manager: Generate Reports, View Analytics
  - Student Manager: View Students, Manage Students
- Logout button

Components:
- `VBox` layout
- `Label` for title and welcome
- Dynamic `Button` elements based on role
- Content area that changes based on selected action

### IT Admin Login Screen (itadmin-login.fxml)

Layout:
- Title: "IT Admin Login"
- TextField for username
- PasswordField for password
- Login button
- Back button

Components:
- `VBox` layout
- `TextField` and `PasswordField`
- `Button` elements

### IT Admin Dashboard Screen (itadmin-dashboard.fxml)

Layout:
- Title: "IT Admin Portal"
- Welcome message
- TableView displaying all administrators with columns:
  - ID
  - Username
  - Name
- Action buttons:
  - Add Administrator
  - Update Administrator
  - Delete Administrator
- Form panel for add/update operations
- Logout button

Components:
- `BorderPane` layout
- `TableView<Admin>`
- Form panel with `TextField` elements
- `Button` elements

## Error Handling

### User Input Validation
- All text fields validate input before processing
- Empty fields show error messages
- Invalid formats (e.g., non-numeric student codes) show specific error messages
- Validation occurs on button click, not real-time to avoid annoying users

### Service Layer Errors
- All service calls wrapped in try-catch blocks
- Exceptions caught and displayed as user-friendly error alerts
- Error messages extracted from exception messages
- Failed operations do not crash the application

### Navigation Errors
- FXML loading failures caught and logged
- Fallback to error screen if navigation fails
- User notified of technical issues with option to return to main menu

### Example Error Handling Pattern
```java
@FXML
private void handleLogin() {
    String code = studentCodeField.getText().trim();
    
    if (code.isEmpty()) {
        AlertHelper.showError("Validation Error", "Student code cannot be empty");
        return;
    }
    
    try {
        Student student = services.getStudentManager().login(code);
        if (student == null) {
            AlertHelper.showError("Login Failed", "Invalid student code");
            return;
        }
        NavigationService.navigateToWithData("student-dashboard", student);
    } catch (Exception e) {
        AlertHelper.showError("Error", "Login failed: " + e.getMessage());
    }
}
```

## Testing Strategy

### Manual Testing Approach
Since this is a GUI application, testing will primarily be manual:

1. **Navigation Testing**
   - Verify all navigation paths work correctly
   - Test back button functionality
   - Verify data passes correctly between screens

2. **Functional Testing**
   - Test each user flow end-to-end:
     - Student registration and login
     - Menu browsing and order placement
     - Payment processing with loyalty points
     - Order history viewing
     - Admin login and manager management
     - Service manager operations
     - IT admin operations
   - Verify all CRUD operations work correctly
   - Test error scenarios (invalid inputs, failed operations)

3. **UI Testing**
   - Verify all UI elements display correctly
   - Test responsive behavior when resizing window
   - Verify styling is consistent across all screens
   - Test with different screen resolutions

4. **Integration Testing**
   - Verify GUI correctly uses existing services
   - Test that database operations work through GUI
   - Verify loyalty points calculation and redemption
   - Test notification system integration

### Testing Checklist
- [ ] Main menu displays and all buttons navigate correctly
- [ ] Student registration creates new student in database
- [ ] Student login validates credentials correctly
- [ ] Menu displays all available items
- [ ] Shopping cart adds/removes items correctly
- [ ] Order placement creates order in database
- [ ] Payment processing works with loyalty points
- [ ] Order history displays past orders
- [ ] Notifications display and mark as read
- [ ] Admin login validates credentials
- [ ] Manager CRUD operations work correctly
- [ ] Reports display accurate metrics
- [ ] Service manager login and operations work
- [ ] IT admin operations work correctly
- [ ] Error messages display for invalid operations
- [ ] Confirmation dialogs prevent accidental deletions

## Styling and Theming

### CSS Theme (cafeteria-theme.css)

The application will use a custom CSS stylesheet matching the terminal's color scheme:

**Color Palette:**
- Primary: #00CED1 (Cyan) - for headers and primary buttons
- Secondary: #FFD700 (Yellow) - for highlights and badges
- Accent: #FF00FF (Magenta) - for prompts and interactive elements
- Success: #00FF00 (Green) - for success messages
- Error: #FF0000 (Red) - for error messages
- Background: #F5F5F5 (Light gray) - for main background
- Card Background: #FFFFFF (White) - for panels and cards
- Text: #333333 (Dark gray) - for primary text

**Component Styling:**
- Buttons: Rounded corners, gradient backgrounds, hover effects
- Text fields: Bordered with focus effects
- Tables: Alternating row colors, hover highlighting
- Headers: Bold text with cyan background
- Cards: White background with subtle shadows
- Badges: Yellow background with bold text

**Example CSS:**
```css
.root {
    -fx-background-color: #F5F5F5;
    -fx-font-family: "Segoe UI", Arial, sans-serif;
}

.primary-button {
    -fx-background-color: linear-gradient(to bottom, #00CED1, #008B8B);
    -fx-text-fill: white;
    -fx-font-weight: bold;
    -fx-padding: 10 20;
    -fx-background-radius: 5;
}

.primary-button:hover {
    -fx-background-color: linear-gradient(to bottom, #00FFFF, #00CED1);
    -fx-cursor: hand;
}

.header-label {
    -fx-font-size: 24px;
    -fx-font-weight: bold;
    -fx-text-fill: #00CED1;
}

.card {
    -fx-background-color: white;
    -fx-padding: 20;
    -fx-background-radius: 10;
    -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);
}

.success-message {
    -fx-text-fill: #00FF00;
    -fx-font-weight: bold;
}

.error-message {
    -fx-text-fill: #FF0000;
    -fx-font-weight: bold;
}

.badge {
    -fx-background-color: #FFD700;
    -fx-text-fill: #333333;
    -fx-font-weight: bold;
    -fx-padding: 2 8;
    -fx-background-radius: 10;
}
```

## Integration with Existing Code

### ServiceContainer Integration
- GUI application initializes ServiceContainer on startup
- NavigationService maintains reference to ServiceContainer
- All controllers access services through NavigationService
- No modifications needed to existing service classes

### Data Flow
1. User interacts with GUI (button click, text input)
2. Controller handles event
3. Controller calls appropriate service method
4. Service performs business logic and database operations
5. Service returns result
6. Controller updates UI based on result

### No Code Duplication
- All business logic remains in existing service classes
- Controllers are thin, only handling UI logic
- Validation rules from terminal implementation reused
- Database operations unchanged

### Launching the GUI
New main class for GUI mode:
```java
public class MainGUI {
    public static void main(String[] args) {
        Application.launch(CafeteriaGUIApp.class, args);
    }
}
```

Existing Main.java remains unchanged for terminal mode.

## Implementation Notes

### FXML Best Practices
- Use meaningful fx:id names (e.g., `studentCodeField`, not `textField1`)
- Group related components in containers (VBox, HBox, GridPane)
- Use layout constraints for responsive design
- Define button actions in FXML using onAction attribute
- Keep FXML files focused on layout, not logic

### Controller Best Practices
- Keep controllers focused on UI logic only
- Delegate business logic to service layer
- Use dependency injection through NavigationService
- Handle all exceptions gracefully
- Provide user feedback for all actions

### Scene Builder Compatibility
- Use standard JavaFX components (avoid custom components initially)
- Organize components in logical hierarchy
- Use descriptive IDs for all interactive elements
- Test FXML files in Scene Builder before implementation
- Document any custom components if added later

### Performance Considerations
- Cache loaded scenes in NavigationService to avoid reloading FXML
- Use lazy loading for heavy components (large tables)
- Limit table row counts with pagination if needed
- Optimize image loading and caching
- Use background threads for long-running operations (if any)
