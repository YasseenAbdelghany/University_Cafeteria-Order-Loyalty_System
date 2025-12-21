# Student Password Authentication - Implementation Tasks

## Task Breakdown

### Phase 1: Database Layer (Foundation)

#### Task 1.1: Update Database Schema ⚠️ CRITICAL
**Priority**: HIGH  
**Estimated Time**: 15 minutes  
**Dependencies**: None

**Steps:**
1. Backup current `Database.sql` file
2. Add password column to Student table:
   ```sql
   ALTER TABLE Student ADD COLUMN password VARCHAR(255);
   ```
3. Update existing student records with default password:
   ```sql
   UPDATE Student SET password = 'student123' WHERE password IS NULL;
   ```
4. Update the `Database.sql` file with the new schema
5. Test database connection and verify column exists

**Files to Modify:**
- `Database.sql`

**Verification:**
- Run query: `DESCRIBE Student;` to confirm password column exists
- Verify existing student data is intact

---

### Phase 2: Data Access Layer

#### Task 2.1: Update StudentDAO - Authentication Method
**Priority**: HIGH  
**Estimated Time**: 20 minutes  
**Dependencies**: Task 1.1

**Steps:**
1. Locate `StudentDAO.java` in `src/DataBase/`
2. Find the current `authenticateStudent()` method
3. Update method signature to accept password parameter:
   ```java
   public Student authenticateStudent(String studentCode, String password)
   ```
4. Update SQL query to check both code and password:
   ```sql
   SELECT * FROM Student WHERE studentCode = ? AND password = ?
   ```
5. Update method implementation to validate both fields
6. Handle null/empty password cases

**Files to Modify:**
- `src/DataBase/StudentDAO.java`

**Verification:**
- Test with valid code + password (should return Student)
- Test with valid code + wrong password (should return null)
- Test with invalid code (should return null)

---

#### Task 2.2: Update StudentDAO - Registration Method
**Priority**: HIGH  
**Estimated Time**: 20 minutes  
**Dependencies**: Task 1.1

**Steps:**
1. Find the current `registerStudent()` method in StudentDAO
2. Update method signature to accept password parameter:
   ```java
   public String registerStudent(String name, String phoneNumber, String password)
   ```
3. Update SQL INSERT statement to include password:
   ```sql
   INSERT INTO Student (studentCode, name, phoneNumber, password, balance) VALUES (?, ?, ?, ?, 0.0)
   ```
4. Update PreparedStatement to set password parameter
5. Ensure student code generation remains unchanged

**Files to Modify:**
- `src/DataBase/StudentDAO.java`

**Verification:**
- Register new student with password
- Verify password is stored in database
- Verify student code is generated correctly

---

### Phase 3: Console Application

#### Task 3.1: Update Console Student Login
**Priority**: MEDIUM  
**Estimated Time**: 25 minutes  
**Dependencies**: Task 2.1

**Steps:**
1. Locate student login logic in console application (likely `src/app/Main.java` or student menu handler)
2. Find the login prompt section
3. Add password input prompt after student code:
   ```java
   System.out.print("Enter Student Code: ");
   String code = scanner.nextLine();
   System.out.print("Enter Password: ");
   String password = scanner.nextLine();
   ```
4. Update authentication call to pass both parameters:
   ```java
   Student student = studentDAO.authenticateStudent(code, password);
   ```
5. Update error messages to reflect new authentication
6. Test login flow

**Files to Modify:**
- `src/app/Main.java` (or relevant student menu handler)

**Verification:**
- Login with correct code + password (success)
- Login with correct code + wrong password (failure)
- Login with wrong code (failure)
- Error messages display correctly

---

#### Task 3.2: Update Console Student Registration
**Priority**: MEDIUM  
**Estimated Time**: 30 minutes  
**Dependencies**: Task 2.2

**Steps:**
1. Locate student registration logic in console application
2. Add password input prompts after phone number:
   ```java
   System.out.print("Enter Password: ");
   String password = scanner.nextLine();
   System.out.print("Confirm Password: ");
   String confirmPassword = scanner.nextLine();
   ```
3. Add password validation logic:
   ```java
   if (!password.equals(confirmPassword)) {
       System.out.println("Passwords do not match!");
       return;
   }
   if (password.length() < 4) {
       System.out.println("Password must be at least 4 characters!");
       return;
   }
   ```
4. Update registration call to pass password:
   ```java
   String studentCode = studentDAO.registerStudent(name, phone, password);
   ```
5. Update success message to emphasize saving the code

**Files to Modify:**
- `src/app/Main.java` (or relevant student menu handler)

**Verification:**
- Register with matching passwords (success)
- Register with non-matching passwords (error shown)
- Register with short password (error shown)
- Verify student code is displayed
- Verify can login with new credentials

---

### Phase 4: GUI Application - Login Screen

#### Task 4.1: Update Student Login FXML
**Priority**: HIGH  
**Estimated Time**: 20 minutes  
**Dependencies**: None

**Steps:**
1. Open `src/app/resources/fxml/student/student-login.fxml`
2. Locate the student code TextField
3. Add PasswordField below it:
   ```xml
   <PasswordField fx:id="passwordField" 
                  promptText="Enter Password"
                  styleClass="login-input"
                  GridPane.rowIndex="2"/>
   ```
4. Update GridPane row indices if needed
5. Ensure styling matches existing theme (purple theme)
6. Add appropriate labels if needed
7. Test layout in Scene Builder or by running app

**Files to Modify:**
- `src/app/resources/fxml/student/student-login.fxml`

**Verification:**
- Password field displays correctly
- Layout is not broken
- Theme styling is consistent
- Prompt text is visible

---

#### Task 4.2: Update StudentLoginController - Login Logic
**Priority**: HIGH  
**Estimated Time**: 25 minutes  
**Dependencies**: Task 2.1, Task 4.1

**Steps:**
1. Open `src/app/gui/student/controllers/StudentLoginController.java`
2. Add PasswordField declaration:
   ```java
   @FXML
   private PasswordField passwordField;
   ```
3. Locate the `handleLogin()` method
4. Update to get password value:
   ```java
   String password = passwordField.getText().trim();
   ```
5. Add validation for empty password:
   ```java
   if (code.isEmpty() || password.isEmpty()) {
       AlertHelper.showError("Login Error", "Please enter both student code and password");
       return;
   }
   ```
6. Update authentication call:
   ```java
   Student student = studentDAO.authenticateStudent(code, password);
   ```
7. Update error message for failed login:
   ```java
   AlertHelper.showError("Login Failed", "Invalid student code or password");
   ```

**Files to Modify:**
- `src/app/gui/student/controllers/StudentLoginController.java`

**Verification:**
- Login with valid credentials (success)
- Login with invalid password (error shown)
- Login with empty fields (validation error shown)
- Navigation to dashboard works

---

### Phase 5: GUI Application - Registration

#### Task 5.1: Update StudentLoginController - Registration Dialog
**Priority**: HIGH  
**Estimated Time**: 40 minutes  
**Dependencies**: Task 2.2, Task 4.2

**Steps:**
1. Open `src/app/gui/student/controllers/StudentLoginController.java`
2. Locate the registration dialog creation method (likely `handleRegister()` or `showRegistrationDialog()`)
3. Add PasswordField controls to the dialog:
   ```java
   PasswordField passwordField = new PasswordField();
   passwordField.setPromptText("Enter Password");
   
   PasswordField confirmPasswordField = new PasswordField();
   confirmPasswordField.setPromptText("Confirm Password");
   ```
4. Add fields to dialog layout (after phone number field)
5. Update dialog validation logic:
   ```java
   String password = passwordField.getText().trim();
   String confirmPassword = confirmPasswordField.getText().trim();
   
   if (password.isEmpty() || confirmPassword.isEmpty()) {
       AlertHelper.showError("Validation Error", "All fields are required");
       return;
   }
   
   if (!password.equals(confirmPassword)) {
       AlertHelper.showError("Validation Error", "Passwords do not match");
       return;
   }
   
   if (password.length() < 4) {
       AlertHelper.showError("Validation Error", "Password must be at least 4 characters");
       return;
   }
   ```
6. Update registration call:
   ```java
   String studentCode = studentDAO.registerStudent(name, phone, password);
   ```
7. Update success message to emphasize saving the code:
   ```java
   AlertHelper.showInfo("Registration Successful", 
       "Your student code is: " + studentCode + "\n\nPlease save this code for login!");
   ```

**Files to Modify:**
- `src/app/gui/student/controllers/StudentLoginController.java`

**Verification:**
- Registration dialog displays password fields
- Validation works for all cases
- Successful registration shows student code
- Can login with newly created credentials
- Dialog styling matches theme

---

### Phase 6: Testing & Verification

#### Task 6.1: Integration Testing - Console
**Priority**: MEDIUM  
**Estimated Time**: 20 minutes  
**Dependencies**: Tasks 3.1, 3.2

**Steps:**
1. Start console application
2. Test student registration flow:
   - Register new student with valid data
   - Try registering with non-matching passwords
   - Try registering with short password
3. Test student login flow:
   - Login with newly created credentials
   - Try login with wrong password
   - Try login with non-existent code
4. Verify student can access menu after login
5. Document any issues found

**Verification Checklist:**
- [ ] Can register new student with password
- [ ] Password validation works
- [ ] Can login with correct credentials
- [ ] Cannot login with wrong password
- [ ] Error messages are clear
- [ ] Student menu accessible after login

---

#### Task 6.2: Integration Testing - GUI
**Priority**: MEDIUM  
**Estimated Time**: 25 minutes  
**Dependencies**: Tasks 4.1, 4.2, 5.1

**Steps:**
1. Start StudentGUIApp
2. Test registration:
   - Click register link/button
   - Fill all fields with valid data
   - Verify password confirmation works
   - Complete registration
   - Note the generated student code
3. Test login:
   - Enter student code and password
   - Verify successful login
   - Test with wrong password
   - Test with empty fields
4. Verify dashboard loads correctly
5. Test logout and re-login
6. Document any issues

**Verification Checklist:**
- [ ] Registration dialog displays correctly
- [ ] Password fields are masked
- [ ] Password validation works
- [ ] Student code is displayed after registration
- [ ] Can login with new credentials
- [ ] Cannot login with wrong password
- [ ] Dashboard loads after successful login
- [ ] Theme styling is consistent

---

#### Task 6.3: Existing Data Migration Test
**Priority**: MEDIUM  
**Estimated Time**: 15 minutes  
**Dependencies**: Task 1.1

**Steps:**
1. Verify existing students have default password set
2. Test login with existing student code + default password
3. Document default password for existing users
4. Consider if password change feature is needed

**Verification Checklist:**
- [ ] Existing students can login with default password
- [ ] Default password is documented
- [ ] No data loss occurred

---

### Phase 7: Documentation & Cleanup

#### Task 7.1: Update Documentation
**Priority**: LOW  
**Estimated Time**: 20 minutes  
**Dependencies**: All previous tasks

**Steps:**
1. Update `QUICK-LOGIN-REFERENCE.md`:
   - Add password requirement for students
   - Document default password for existing students
2. Update `README-GUI.md`:
   - Update student login instructions
   - Add registration instructions with password
3. Update `GUI-TESTING-GUIDE.md`:
   - Add password authentication test cases
4. Create migration notes document if needed

**Files to Modify:**
- `QUICK-LOGIN-REFERENCE.md`
- `README-GUI.md`
- `GUI-TESTING-GUIDE.md`

---

#### Task 7.2: Create Implementation Summary
**Priority**: LOW  
**Estimated Time**: 15 minutes  
**Dependencies**: All previous tasks

**Steps:**
1. Create `STUDENT-PASSWORD-AUTH-SUMMARY.md`
2. Document:
   - Changes made
   - Files modified
   - Default password for existing students
   - Testing results
   - Known issues or limitations
   - Future enhancements (password hashing, reset, etc.)

**Files to Create:**
- `STUDENT-PASSWORD-AUTH-SUMMARY.md`

---

## Task Execution Order

### Critical Path (Must be done in order):
1. Task 1.1 (Database Schema) ⚠️
2. Task 2.1 (DAO Authentication)
3. Task 2.2 (DAO Registration)
4. Task 4.1 (GUI FXML)
5. Task 4.2 (GUI Login Controller)
6. Task 5.1 (GUI Registration)

### Parallel Tasks (Can be done simultaneously):
- Task 3.1 and Task 3.2 (Console) - After Task 2.1 and 2.2
- Task 6.1 and Task 6.2 (Testing) - After implementation complete
- Task 7.1 and Task 7.2 (Documentation) - After testing

## Estimated Total Time
- Phase 1: 15 minutes
- Phase 2: 40 minutes
- Phase 3: 55 minutes
- Phase 4: 45 minutes
- Phase 5: 40 minutes
- Phase 6: 60 minutes
- Phase 7: 35 minutes

**Total: ~4.5 hours**

## Risk Mitigation

### High Risk Items:
1. **Database migration** - Backup before changes
2. **Breaking existing functionality** - Test thoroughly
3. **User confusion** - Clear documentation and error messages

### Rollback Plan:
1. Keep backup of Database.sql
2. Keep backup of modified DAO files
3. Document all changes for easy reversal

## Success Criteria
- [ ] Database has password column
- [ ] Students can register with password
- [ ] Students can login with code + password (Console)
- [ ] Students can login with code + password (GUI)
- [ ] Password validation works correctly
- [ ] Existing students can login with default password
- [ ] All tests pass
- [ ] Documentation updated
- [ ] No breaking changes to other user types
