# Requirements Document

## Introduction

This document specifies the requirements for building a JavaFX-based graphical user interface (GUI) for the Cafeteria Management System. The GUI will replicate the functionality of the existing terminal-based interface while providing a modern, user-friendly visual experience. The interface will be built using FXML files to enable easy modification through Scene Builder, maintaining separation between UI design and business logic.

## Glossary

- **CafeteriaGUI**: The JavaFX application that provides the graphical user interface for the cafeteria system
- **FXML**: JavaFX Markup Language, an XML-based language for defining user interfaces
- **Scene Builder**: A visual layout tool for designing JavaFX application user interfaces
- **Controller**: A Java class that handles user interactions and connects the FXML view to business logic
- **ServiceContainer**: The existing dependency injection container that provides access to all system services
- **MainConsole**: The existing terminal-based main menu orchestrator
- **StudentPortal**: The interface for student users to browse menu, place orders, and view history
- **AdminDashboard**: The interface for administrators to manage service managers and view reports
- **ServiceManagerPortal**: The interface for service managers to perform their specific duties
- **ITAdminPortal**: The interface for IT administrators to manage system users

## Requirements

### Requirement 1

**User Story:** As a cafeteria system user, I want a graphical interface instead of a terminal interface, so that I can interact with the system more intuitively and efficiently

#### Acceptance Criteria

1. WHEN the CafeteriaGUI application starts, THE CafeteriaGUI SHALL display a main menu window with options for Student, Admin, Services, and IT Admin access
2. WHEN a user selects a menu option, THE CafeteriaGUI SHALL navigate to the corresponding interface screen
3. THE CafeteriaGUI SHALL maintain the same functional capabilities as the existing terminal interface
4. THE CafeteriaGUI SHALL use the existing ServiceContainer to access all business logic and services
5. THE CafeteriaGUI SHALL display visual feedback for all user actions including success messages, error messages, and warnings

### Requirement 2

**User Story:** As a developer, I want the GUI to be built with FXML files, so that I can easily modify the interface design using Scene Builder without changing Java code

#### Acceptance Criteria

1. THE CafeteriaGUI SHALL define all user interface layouts using FXML files
2. WHEN an FXML file is opened in Scene Builder, THE CafeteriaGUI SHALL allow visual editing of all UI components
3. THE CafeteriaGUI SHALL separate UI layout (FXML) from business logic (Controller classes)
4. THE CafeteriaGUI SHALL use fx:id attributes in FXML to bind UI components to Controller fields
5. THE CafeteriaGUI SHALL use onAction attributes in FXML to bind user interactions to Controller methods

### Requirement 3

**User Story:** As a student, I want to access my student portal through the GUI, so that I can browse the menu, place orders, and view my order history visually

#### Acceptance Criteria

1. WHEN a student logs in through the StudentPortal, THE CafeteriaGUI SHALL display a dashboard with menu browsing, order placement, and history viewing options
2. WHEN a student browses the menu, THE CafeteriaGUI SHALL display menu items with names, descriptions, prices, and categories in a visual format
3. WHEN a student places an order, THE CafeteriaGUI SHALL provide a shopping cart interface with item selection and quantity controls
4. WHEN a student views order history, THE CafeteriaGUI SHALL display past orders with dates, items, and payment status
5. THE StudentPortal SHALL display the student's loyalty points and available balance

### Requirement 4

**User Story:** As an administrator, I want to access the admin dashboard through the GUI, so that I can manage service managers and view system reports visually

#### Acceptance Criteria

1. WHEN an administrator logs in through the AdminDashboard, THE CafeteriaGUI SHALL display options for managing service managers and viewing reports
2. WHEN an administrator manages service managers, THE CafeteriaGUI SHALL provide forms for adding, updating, deleting, and searching managers
3. WHEN an administrator views reports, THE CafeteriaGUI SHALL display system metrics including total revenue, students, menu items, and orders
4. THE AdminDashboard SHALL display manager statistics by type with visual indicators
5. THE AdminDashboard SHALL provide confirmation dialogs before performing destructive operations like deleting managers

### Requirement 5

**User Story:** As a service manager, I want to access my service portal through the GUI, so that I can perform my specific management duties visually

#### Acceptance Criteria

1. WHEN a service manager logs in through the ServiceManagerPortal, THE CafeteriaGUI SHALL authenticate the manager and display role-specific options
2. WHERE the manager is a Menu Manager, THE ServiceManagerPortal SHALL display options for adding, updating, and removing menu items
3. WHERE the manager is an Order Manager, THE ServiceManagerPortal SHALL display options for viewing and processing orders
4. WHERE the manager is a Payment Manager, THE ServiceManagerPortal SHALL display options for managing payment methods and transactions
5. WHERE the manager is a Notification Manager, THE ServiceManagerPortal SHALL display options for sending and viewing notifications

### Requirement 6

**User Story:** As an IT administrator, I want to access the IT admin portal through the GUI, so that I can manage system administrators and user roles visually

#### Acceptance Criteria

1. WHEN an IT administrator logs in through the ITAdminPortal, THE CafeteriaGUI SHALL display options for managing administrators and roles
2. WHEN an IT administrator manages administrators, THE CafeteriaGUI SHALL provide forms for adding, updating, and deleting admin accounts
3. THE ITAdminPortal SHALL display a list of all administrators with their details
4. THE ITAdminPortal SHALL provide role-based access control configuration options
5. THE ITAdminPortal SHALL require confirmation before performing critical operations

### Requirement 7

**User Story:** As a user, I want consistent visual styling across all GUI screens, so that the interface feels cohesive and professional

#### Acceptance Criteria

1. THE CafeteriaGUI SHALL apply a consistent color scheme across all screens matching the terminal's cyan, yellow, magenta, and green theme
2. THE CafeteriaGUI SHALL use consistent fonts, spacing, and component sizes throughout the application
3. THE CafeteriaGUI SHALL display icons and emojis similar to the terminal interface for visual recognition
4. THE CafeteriaGUI SHALL use CSS styling for all visual customization to enable easy theme modifications
5. THE CafeteriaGUI SHALL maintain consistent button styles, input field styles, and layout patterns across all screens

### Requirement 8

**User Story:** As a developer, I want the GUI to integrate seamlessly with the existing codebase, so that I can reuse all existing business logic without duplication

#### Acceptance Criteria

1. THE CafeteriaGUI SHALL use the existing ServiceContainer to access all services
2. THE CafeteriaGUI SHALL reuse existing domain models, services, and managers without modification
3. THE CafeteriaGUI SHALL not duplicate business logic that already exists in the terminal implementation
4. THE CafeteriaGUI SHALL maintain the same data flow and validation rules as the terminal interface
5. THE CafeteriaGUI SHALL be launchable independently from the terminal interface through a separate entry point
