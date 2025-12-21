# Requirements Document - Dual Application Enhancement

## Introduction

This document specifies the requirements for splitting the Cafeteria Management System into two separate JavaFX applications and enhancing the user experience for both student and administrative users. The system will be divided into a Student Application and an Administrative Application, each with streamlined workflows and improved usability.

## Glossary

- **Student Application**: A standalone JavaFX application dedicated to student users for browsing menus, placing orders, and viewing notifications
- **Administrative Application**: A standalone JavaFX application for Admin, Service Managers, and IT Admin users
- **Unified Login**: A single login screen that authenticates users and routes them to the appropriate dashboard based on credentials
- **Menu Dashboard**: The primary screen students see after login, displaying menu items and order placement functionality
- **Notification Badge**: A visual indicator showing the count of unread notifications
- **IT Admin Root Account**: A special administrator account with username "root" and password "root"
- **Role Detection**: The system's ability to determine user type (Admin, Service Manager type, or IT Admin) based on login credentials

## Requirements

### Requirement 1: Separate Student Application

**User Story:** As a student, I want a dedicated application for ordering food, so that I have a focused and streamlined experience without administrative features.

#### Acceptance Criteria

1. WHEN THE Student Application launches, THE System SHALL display a login/register screen as the initial view
2. WHEN a student successfully logs in or registers, THE System SHALL navigate directly to the menu browsing and order placement screen
3. WHEN the menu screen is displayed, THE System SHALL show a notification icon with an unread count badge at the top of the screen
4. WHEN the menu screen is displayed, THE System SHALL show a "View Order History" button at the bottom of the screen
5. THE Student Application SHALL NOT include the "Check Loyalty Points" button on any screen
6. THE Student Application SHALL NOT include any administrative features or dashboards
7. THE Student Application SHALL launch independently from the Administrative Application

### Requirement 2: Separate Administrative Application

**User Story:** As an administrator or service manager, I want a dedicated application for management tasks, so that I can access administrative functions without student-facing features.

#### Acceptance Criteria

1. WHEN THE Administrative Application launches, THE System SHALL display a unified login screen for all administrative users
2. THE Administrative Application SHALL NOT include student registration functionality
3. THE Administrative Application SHALL NOT include student menu browsing or ordering features
4. THE Administrative Application SHALL launch independently from the Student Application
5. THE Administrative Application SHALL support authentication for Admin, Service Managers, and IT Admin users

### Requirement 3: Unified Administrative Login

**User Story:** As an administrative user, I want to log in through a single screen regardless of my role, so that I don't need to select my user type before authentication.

#### Acceptance Criteria

1. WHEN THE unified login screen is displayed, THE System SHALL show username and password fields without role selection
2. WHEN a user enters credentials and clicks login, THE System SHALL authenticate against all administrative user types
3. WHEN authentication succeeds, THE System SHALL determine the user's role from the database
4. WHEN the user role is determined, THE System SHALL navigate to the appropriate dashboard for that role
5. WHEN authentication fails, THE System SHALL display an error message indicating invalid credentials

### Requirement 4: Role-Based Dashboard Navigation

**User Story:** As the system, I need to route authenticated users to the correct dashboard, so that each user sees only the features relevant to their role.

#### Acceptance Criteria

1. WHEN a user with Admin role authenticates, THE System SHALL navigate to the Admin Dashboard
2. WHEN a user with Service Manager role authenticates, THE System SHALL navigate to the Service Manager Dashboard for their specific manager type
3. WHEN a user with IT Admin role authenticates, THE System SHALL navigate to the IT Admin Dashboard
4. WHEN role detection fails, THE System SHALL display an error message and remain on the login screen
5. THE System SHALL maintain user session data throughout the application lifecycle

### Requirement 5: IT Admin Root Account

**User Story:** As an IT administrator, I want to use a special root account with known credentials, so that I can always access the system for administrative tasks.

#### Acceptance Criteria

1. THE System SHALL recognize username "root" as an IT Admin account
2. WHEN username "root" and password "root" are entered, THE System SHALL authenticate as IT Admin
3. WHEN the root account authenticates, THE System SHALL navigate to the IT Admin Dashboard
4. THE System SHALL distinguish the root account from regular Admin accounts during authentication
5. THE root account SHALL have full IT Admin privileges

### Requirement 6: Enhanced Student Menu Dashboard

**User Story:** As a student, I want to see the menu immediately after login, so that I can quickly browse and order food without extra navigation steps.

#### Acceptance Criteria

1. WHEN a student logs in successfully, THE System SHALL display the menu browsing screen as the primary dashboard
2. WHEN a student registers successfully, THE System SHALL display the menu browsing screen as the primary dashboard
3. THE menu screen SHALL display all available menu items in a table or grid format
4. THE menu screen SHALL include shopping cart functionality for order placement
5. THE menu screen SHALL include a checkout button to proceed to payment

### Requirement 7: Notification Badge Display

**User Story:** As a student, I want to see how many unread notifications I have at a glance, so that I can stay informed without navigating away from the menu.

#### Acceptance Criteria

1. WHEN the menu screen is displayed, THE System SHALL show a notification icon at the top of the screen
2. WHEN unread notifications exist, THE System SHALL display the count as a badge on the notification icon
3. WHEN the notification count is zero, THE System SHALL display the icon without a badge or show "0"
4. WHEN the notification icon is clicked, THE System SHALL navigate to the notifications screen
5. WHEN the user returns from notifications, THE System SHALL update the badge count to reflect read notifications

### Requirement 8: Streamlined Student Navigation

**User Story:** As a student, I want simplified navigation options, so that I can focus on ordering food without unnecessary features.

#### Acceptance Criteria

1. THE menu screen SHALL include a "View Order History" button at the bottom
2. THE menu screen SHALL NOT include a "Check Loyalty Points" button
3. WHEN the "View Order History" button is clicked, THE System SHALL navigate to the order history screen
4. THE System SHALL display loyalty points information within the order placement flow where relevant
5. THE menu screen SHALL include a logout button to return to the login screen

### Requirement 9: Application Separation

**User Story:** As a system administrator, I want two separate executable applications, so that I can deploy student and administrative interfaces independently.

#### Acceptance Criteria

1. THE System SHALL provide a StudentGUIApp main class for the Student Application
2. THE System SHALL provide an AdminGUIApp main class for the Administrative Application
3. THE System SHALL provide separate launch scripts for each application
4. WHEN either application launches, THE System SHALL initialize only the components needed for that application
5. THE System SHALL share common business logic and database access between both applications

### Requirement 10: Backward Compatibility

**User Story:** As a developer, I want to maintain existing business logic and database structure, so that the enhancement doesn't require database migrations or service rewrites.

#### Acceptance Criteria

1. THE System SHALL reuse all existing service classes without modification
2. THE System SHALL reuse all existing DAO classes without modification
3. THE System SHALL reuse all existing domain model classes without modification
4. THE System SHALL use the existing ServiceContainer for dependency injection
5. THE System SHALL maintain compatibility with the existing database schema

