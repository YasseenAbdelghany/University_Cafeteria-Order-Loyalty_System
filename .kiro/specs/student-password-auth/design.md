# Student Password Authentication - Design

## Architecture Overview

This feature adds password authentication to the student login system, affecting:
- **Data Layer**: Database schema and StudentDAO
- **Business Layer**: Authentication logic and validation
- **Presentation Layer**: Console UI and JavaFX GUI

## Database Design

### Student Table Modification

```sql
ALTER TABLE Student ADD COLUMN password VARCHAR(255);
```

**Migration Strategy for Existing Data:**
- Option 1: Set default password (e.g., "password123") for existing students
- Option 2: Require existing students to set password on first login
- **Recommended**: Option 1 for simplicity, document in migration notes

### Updated Student Table Schema

```
Student Table:
- studentCode (VARCHAR, PRIMARY KEY) - Auto-generated
- name (VARCHAR)
- phoneNumber (VARCHAR)
- password (VARCHAR) - NEW FIELD
- balance (DECIMAL)
- createdAt (TIMESTAMP)
```

## Data Access Layer Design

### StudentDAO Updates

**Modified Methods:**

1. **registerStudent()**
```java
// OLD: registerStudent(String name, String phoneNumber)
// NEW: registerStudent(String name, String phoneNumber, String password)
```

2. **authenticateStudent()**
```java
// OLD: authenticateStudent(String studentCode)
// NEW: authenticateStudent(String studentCode, String password)
```

3. **New Helper Method:**
```java
private boolean validatePassword(String studentCode, String password)
```

## Business Logic Design

### Password Validation Rules

**Registration Validation:**
1. Password must not be empty
2. Password must match confirmation password
3. Minimum length: 4 characters (configurable)

**Login Validation:**
1. Student code must exist
2. Password must match stored password
3. Return Student object on success, null on failure

### Authentication Flow

```
Registration Flow:
1. User enters: Name, Phone, Password, Confirm Password
2. Validate: All fields filled
3. Validate: Password == Confirm Password
4. Generate: Student Code
5. Store: All data including password
6. Return: Success with student code

Login Flow:
1. User enters: Student Code, Password
2. Query: Student by code
3. Validate: Student exists
4. Validate: Password matches
5. Return: Student object or null
```

## Presentation Layer Design

### Console Application (Terminal)

**Location**: `src/app/Main.java` or student menu handler

**Student Login Updates:**
```
Current:
  Enter Student Code: [input]

New:
  Enter Student Code: [input]
  Enter Password: [input]
```

**Student Registration Updates:**
```
Current:
  Enter Name: [input]
  Enter Phone Number: [input]

New:
  Enter Name: [input]
  Enter Phone Number: [input]
  Enter Password: [input]
  Confirm Password: [input]
```

### GUI Application (StudentGUIApp)

#### 1. Login Screen Updates

**File**: `src/app/resources/fxml/student/student-login.fxml`

**Changes:**
- Add PasswordField for password input
- Update layout to accommodate new field
- Maintain current purple theme styling

**Layout Structure:**
```
[Logo/Title]
Student Code: [TextField]
Password:     [PasswordField]  <- NEW
[Login Button]
[Register Link]
```

#### 2. Registration Dialog Updates

**File**: `src/app/gui/student/controllers/StudentLoginController.java`

**Changes to Registration Dialog:**
- Add PasswordField for password
- Add PasswordField for confirm password
- Add validation logic
- Update success message to show generated code

**Dialog Structure:**
```
Register New Student
Name:             [TextField]
Phone Number:     [TextField]
Password:         [PasswordField]  <- NEW
Confirm Password: [PasswordField]  <- NEW
[Register Button] [Cancel Button]
```

#### 3. Controller Updates

**StudentLoginController.java:**

```java
// Add fields
@FXML private PasswordField passwordField;

// Update handleLogin()
private void handleLogin() {
    String code = studentCodeField.getText();
    String password = passwordField.getText();
    
    if (code.isEmpty() || password.isEmpty()) {
        showError("Please enter both student code and password");
        return;
    }
    
    Student student = studentDAO.authenticateStudent(code, password);
    if (student != null) {
        // Success - navigate to dashboard
    } else {
        showError("Invalid student code or password");
    }
}

// Update handleRegister()
private void handleRegister() {
    // Create dialog with password fields
    // Validate password match
    // Call studentDAO.registerStudent(name, phone, password)
}
```

## UI/UX Design

### Visual Design
- Maintain current purple theme for student app
- Use JavaFX PasswordField (shows dots/asterisks)
- Consistent spacing and alignment
- Clear error messages

### Error Messages
- "Please enter both student code and password"
- "Invalid student code or password"
- "Passwords do not match"
- "All fields are required"
- "Password must be at least 4 characters"

### Success Messages
- Registration: "Registration successful! Your student code is: [CODE]. Please save this code."
- Login: Navigate directly to dashboard

## Security Considerations

### Current Implementation (Phase 1)
- Store passwords as plain text in database
- Basic validation only
- Focus on functionality

### Future Enhancements (Phase 2)
- Password hashing (BCrypt, SHA-256)
- Salt generation
- Password strength requirements
- Password reset functionality
- Account lockout after failed attempts

## Testing Strategy

### Unit Tests
- StudentDAO.authenticateStudent() with valid/invalid credentials
- Password validation logic
- Registration with matching/non-matching passwords

### Integration Tests
- End-to-end registration flow
- End-to-end login flow
- Database persistence verification

### Manual Tests
- Console registration and login
- GUI registration and login
- Error handling scenarios
- Existing student data compatibility

## Migration Plan

### Step 1: Database Update
1. Backup current database
2. Run ALTER TABLE script
3. Update existing students with default password
4. Verify schema changes

### Step 2: DAO Layer
1. Update StudentDAO methods
2. Test with sample data
3. Verify backward compatibility

### Step 3: Console Application
1. Update login prompts
2. Update registration prompts
3. Test authentication flow

### Step 4: GUI Application
1. Update FXML layouts
2. Update controllers
3. Test UI interactions
4. Verify theme consistency

### Step 5: Integration Testing
1. Test all flows end-to-end
2. Verify existing functionality
3. Document any issues

## File Change Summary

### Database
- `Database.sql` - Add password column

### Data Access
- `src/DataBase/StudentDAO.java` - Update methods

### Console
- `src/app/Main.java` (or student menu handler) - Update prompts

### GUI
- `src/app/resources/fxml/student/student-login.fxml` - Add password field
- `src/app/gui/student/controllers/StudentLoginController.java` - Update logic
- `src/app/gui/student/StudentGUIApp.java` - Verify no changes needed

### Documentation
- Update README-GUI.md with new login requirements
- Update QUICK-LOGIN-REFERENCE.md with password info
