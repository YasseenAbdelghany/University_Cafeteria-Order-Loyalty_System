# Student Password Authentication - Requirements

## Overview
Enhance student authentication security by implementing password-based login instead of student code-only authentication. Students will now need both their student code and password to access the system.

## Current State
- Students login using only their student code
- No password protection for student accounts
- Security vulnerability: anyone with a student code can access that account

## Target State
- Students login with student code + password
- Password required during registration/signup
- Password confirmation during registration
- Secure password storage in database
- Updated authentication in both Console and GUI applications

## Scope

### In Scope
1. Database schema modification (add password column to Student table)
2. Student registration with password creation and confirmation
3. Student login with code + password validation
4. Console application updates (terminal-based login/registration)
5. StudentGUIApp updates (GUI-based login/registration)
6. StudentDAO modifications for password handling
7. Password validation logic

### Out of Scope
- Password encryption/hashing (can be added later)
- Password reset functionality
- Password strength requirements
- Admin/Manager password changes (separate feature)

## Functional Requirements

### FR1: Database Schema
- Add `password` column to Student table (VARCHAR)
- Ensure backward compatibility or migration strategy

### FR2: Student Registration
- Collect: Name, Phone Number, Password, Confirm Password
- Validate password match (password == confirm password)
- Generate student code automatically
- Store password in database

### FR3: Student Login
- Require both student code AND password
- Validate credentials against database
- Deny access if either is incorrect

### FR4: Console Application
- Update terminal prompts for code + password input
- Update registration flow with password fields
- Maintain existing console UI patterns

### FR5: GUI Application
- Add password field to StudentLoginController
- Add password + confirm password fields to registration dialog
- Update FXML layouts
- Maintain current design theme

## Non-Functional Requirements

### NFR1: Security
- Passwords stored securely (plain text initially, encryption later)
- No password display in console (masked input if possible)
- Password fields use PasswordField in JavaFX

### NFR2: Usability
- Clear error messages for authentication failures
- Password confirmation validation with helpful feedback
- Consistent UX across console and GUI

### NFR3: Compatibility
- No breaking changes to other user types (Admin, Manager, Service)
- Existing student data migration path

## Success Criteria
1. Database successfully updated with password column
2. New students can register with password
3. Students can login with code + password in console
4. Students can login with code + password in GUI
5. Invalid credentials are properly rejected
6. All existing functionality remains intact

## Dependencies
- Database access and modification rights
- StudentDAO class
- Console application (Main.java or similar)
- StudentGUIApp and StudentLoginController
- Student registration dialogs/forms

## Risks
- Existing student data may need password initialization
- Breaking changes if not carefully implemented
- User confusion during transition
