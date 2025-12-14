# University_Cafeteria-Order-Loyalty_System
# Cafeteria System - Applications User Guide

## 📱 Three Applications Overview

This system consists of three separate applications:
1. **StudentGUIApp** - For students to order food
2. **AdminGUIApp** - For administrators and managers
3. **Terminal Console** - Command-line interface for all users

---

## 🎓 StudentGUIApp

### Purpose
Student-facing application for browsing menu, placing orders, and managing loyalty points.

### How to Start
```bash
start-student-gui.bat
```

### Prerequisites
- ✅ Java 11 or higher installed
- ✅ JavaFX SDK configured
- ✅ MySQL database running
- ✅ Database schema created (run `Database.sql`)
- ✅ Database migration applied (run `add-order-preparing-type.sql`)

### Default Login Credentials
**For Testing:**
- Student Code: `st001` (or any registered student code)
- Password: `password123` (or the password set during registration)

**New Students:**
- Click "Register" button
- Enter name and phone number
- System generates unique student code
- Set your password

### Features Available

#### 1. Menu Dashboard
- Browse available menu items
- View item details (name, description, price, category)
- Add items to cart with quantity
- View current loyalty points
- Check notifications

#### 2. Shopping Cart
- Review selected items
- Adjust quantities
- Remove items
- See subtotal and total
- Proceed to checkout

#### 3. Order Payment
- Select payment method (Cash, Credit Card, Mobile Wallet)
- View available loyalty points
- Redeem points for discount (1 point = 0.1 EGP)
- See max redeemable points
- View points to be earned from order
- **FREE orders** when fully paid with points
- Confirm and place order

#### 4. Order History
- View past orders
- Check order status (NEW, PREPARING, READY)
- See order details and totals
- Track payment information

#### 5. Notifications
- Receive order status updates
  - **Orange badge** 👨‍🍳 - Order is being prepared
  - **Green badge** ✅ - Order ready for pickup
- Get reward notifications for bonus points
- View sale announcements
- Mark notifications as read

#### 6. Loyalty Program
- Earn 1 point for every 10 EGP spent
- Redeem points for discounts
- Get free items with enough points
- Real-time point updates (no logout needed)
- Receive bonus points from Student Manager

### What You Need
1. **Student Account** - Register in the app or via Terminal
2. **Internet Connection** - To access database
3. **Loyalty Program** - Automatically enrolled on registration

---

## 👔 AdminGUIApp

### Purpose
Administrative application for managing the cafeteria system with role-based access.

### How to Start
```bash
start-admin-gui.bat
```

### Prerequisites
- ✅ Java 11 or higher installed
- ✅ JavaFX SDK configured
- ✅ MySQL database running
- ✅ Admin/Manager accounts created in database

### User Roles & Login Credentials

#### 1. IT Admin
**Username:** `root` | **Password:** `root`

**Capabilities:**
- Manage all service managers
- Create/update/delete manager accounts
- View system reports
- Full system access

#### 2. Admin
**Username:** `admin` | **Password:** `admin123`

**Capabilities:**
- Access all dashboards
- Manage students, menu, orders, payments
- Send notifications
- View reports

#### 3. Student Manager
**Username:** `Mst00001` | **Password:** `Mst00001`

**Capabilities:**
- Register new students
- Update student information
- Award loyalty points (individual or broadcast)
- View student list
- Send reward notifications

#### 4. Menu Manager
**Username:** `Mm00001` | **Password:** `Mm00001`

**Capabilities:**
- Add new menu items
- Update item details (name, price, description, category)
- **Toggle items Active/Inactive**
  - Inactive items hidden from students
  - Can reactivate anytime
- Remove menu items
- View all menu items

#### 5. Order Manager
**Username:** `Mord00001` | **Password:** `Mord00001`

**Capabilities:**
- View pending orders
- Update order status:
  - NEW → PREPARING (sends orange notification)
  - PREPARING → READY (sends green notification)
- View order details
- Track order history

#### 6. Payment Manager
**Username:** `Mps00001` | **Password:** `Mps00001`

**Capabilities:**
- View payment transactions
- Verify payment status
- Process refunds
- Generate payment reports

#### 7. Notification Manager
**Username:** `Mns00001` | **Password:** `Mns00001`

**Capabilities:**
- Send notifications to students
- Broadcast announcements
- View notification history
- Manage notification types

#### 8. Report Manager
**Username:** `Mrs00001` | **Password:** `Mrs00001`

**Capabilities:**
- Generate sales reports
- View order statistics
- Analyze loyalty program data
- Export reports

### Features by Dashboard

#### Student Manager Dashboard
- Register students
- Update student info
- Award loyalty points
- Broadcast points to all students
- Send reward notifications

#### Menu Manager Dashboard
- Add/Update/Remove menu items
- **Toggle Active/Inactive status**
- View all items (active + inactive)
- Manage categories (MAIN_COURSE, SNACK, DRINK)

#### Order Manager Dashboard
- View pending orders
- Mark orders as PREPARING
- Mark orders as READY
- View order details
- **Automatic notifications sent on status change**

#### Payment Manager Dashboard
- View transactions
- Check payment status
- Process refunds
- Verify payments

#### Notification Manager Dashboard
- Send custom notifications
- Broadcast to all students
- View notification history
- Manage message types

#### Report Manager Dashboard
- Sales reports
- Order statistics
- Loyalty program analytics
- Revenue tracking

### What You Need
1. **Manager Account** - Created by IT Admin
2. **Assigned Role** - Determines dashboard access
3. **Database Access** - System must connect to MySQL

---

## 💻 Terminal Console

### Purpose
Command-line interface for all system operations without GUI.

### How to Start
```bash
# Compile first
javac -d build -cp "lib/*;src" src/terminal/*.java

# Run
java -cp "build;lib/*" terminal.Main
```

### Available Consoles

#### 1. Student Portal Console
**Access:** Select option 1 from main menu

**Features:**
- Register new account
- Login with student code and password
- Browse menu
- Place orders
- View order history
- Check loyalty points
- Redeem points
- View notifications

**Sample Flow:**
```
1. Register
   - Enter name: John Doe
   - Enter phone: 01234567890
   - Set password: ********
   - Receive student code: ST001

2. Login
   - Enter code: ST001
   - Enter password: ********

3. Browse Menu
   - View available items
   - See prices and categories

4. Place Order
   - Select items
   - Enter quantities
   - Choose payment method
   - Redeem points (optional)
   - Confirm order

5. View History
   - See past orders
   - Check status
   - View totals
```

#### 2. Admin Portal Console
**Access:** Select option 2 from main menu

**Features:**
- Login as any manager role
- Perform role-specific operations
- Manage system data
- Generate reports

**Sample Flow:**
```
1. Login
   - Enter username: menu1
   - Enter password: password123

2. Menu Management
   - Add new items
   - Update prices
   - Remove items
   - View menu

3. Student Management
   - Register students
   - Award points
   - View student list

4. Order Management
   - View pending orders
   - Update status
   - Process orders
```

### What You Need
1. **Java JDK** - Version 11 or higher
2. **MySQL Connector** - In lib folder
3. **Database Running** - MySQL server active
4. **Compiled Classes** - Run javac command first

---

## 🗄️ Database Requirements

### MySQL Setup

#### 1. Install MySQL
- Download from: https://dev.mysql.com/downloads/
- Install MySQL Server 8.0 or higher
- Set root password during installation

#### 2. Create Database
```sql
-- Run this first
mysql -u root -p < Database.sql
```

This creates:
- Database: `CafeteriaSystem`
- All required tables
- Proper relationships

#### 3. Apply Migration (IMPORTANT!)
```sql
-- Run this to add ORDER_PREPARING notification type
mysql -u root -p CafeteriaSystem < add-order-preparing-type.sql
```

#### 4. Verify Setup
```sql
USE CafeteriaSystem;
SHOW TABLES;
-- Should show: student, menu_item, orders, notification_history, etc.

DESCRIBE notification_history;
-- message_type should include: SALE, ORDER_READY, ORDER_PREPARING, GENERAL
```

### Database Connection

**Configuration File:** `src/DataBase/DBconnection.java`

```java
private static final String URL = "jdbc:mysql://localhost:3306/CafeteriaSystem";
private static final String USER = "root";
private static final String PASSWORD = "your_password";
```

**Update if needed:**
- Change `localhost` if database is on different server
- Change `3306` if using different port
- Update `USER` and `PASSWORD` with your MySQL credentials

---

## 📦 Dependencies & Libraries

### Required Libraries (in `lib` folder)

1. **MySQL Connector**
   - File: `mysql-connector-java-8.0.x.jar`
   - Purpose: Database connectivity
   - Download: https://dev.mysql.com/downloads/connector/j/

2. **JavaFX SDK** (for GUI apps)
   - Version: 11 or higher
   - Modules needed:
     - javafx.controls
     - javafx.fxml
     - javafx.graphics
   - Download: https://openjfx.io/

### Project Structure
```
CafeteriaSystem/
├── src/
│   ├── Core/              # Domain models
│   ├── Services/          # Business logic
│   ├── DataBase/          # DAO classes
│   ├── terminal/          # Console apps
│   └── app/
│       ├── gui/
│       │   ├── student/   # StudentGUIApp
│       │   └── admin/     # AdminGUIApp
│       └── resources/
│           ├── fxml/      # UI layouts
│           └── css/       # Stylesheets
├── lib/                   # External JARs
├── build/                 # Compiled classes
├── Database.sql           # Schema
├── add-order-preparing-type.sql  # Migration
├── start-student-gui.bat  # Student app launcher
└── start-admin-gui.bat    # Admin app launcher
```

---

## 🚀 Quick Start Guide

### First Time Setup

#### Step 1: Install Prerequisites
```bash
# Check Java version
java -version
# Should be 11 or higher

# Check MySQL
mysql --version
```

#### Step 2: Setup Database
```bash
# Create database
mysql -u root -p < Database.sql

# Apply migration
mysql -u root -p CafeteriaSystem < add-order-preparing-type.sql
```

#### Step 3: Configure Connection
Edit `src/DataBase/DBconnection.java`:
```java
private static final String PASSWORD = "your_mysql_password";
```

#### Step 4: Compile (if needed)
```bash
# Compile all
javac -d build -cp "lib/*;src" src/**/*.java
```

#### Step 5: Launch Applications
```bash
# Student App
start-student-gui.bat

# Admin App
start-admin-gui.bat

# Terminal
java -cp "build;lib/*" terminal.Main
```

---

## 🎯 Common Use Cases

### For Students

#### Scenario 1: First Time User
1. Launch StudentGUIApp
2. Click "Register"
3. Enter name and phone
4. Set password
5. Save your student code!
6. Login and start ordering

#### Scenario 2: Place Order
1. Login to StudentGUIApp
2. Browse menu
3. Add items to cart
4. Click "Checkout"
5. Redeem points (optional)
6. Select payment method
7. Confirm order
8. Wait for notification

#### Scenario 3: Get Free Item
1. Check your loyalty points
2. Find item you can afford (price ÷ 0.1 = points needed)
3. Add to cart
4. Redeem all required points
5. Total shows "FREE (0.00 EGP)"
6. Confirm order

### For Administrators

#### Scenario 1: Add Menu Item
1. Login as Menu Manager
2. Enter item details
3. Set price and category
4. Click "Add Item"
5. Item appears in menu

#### Scenario 2: Process Order
1. Login as Order Manager
2. View pending orders
3. Select order
4. Click "Mark as Preparing"
   - Student gets orange notification
5. When ready, click "Mark as Ready"
   - Student gets green notification

#### Scenario 3: Award Bonus Points
1. Login as Student Manager
2. Select student
3. Enter points amount
4. Click "Send Points"
   - Student gets reward notification

---

## 🔧 Troubleshooting

### Common Issues

#### "Cannot connect to database"
**Solution:**
1. Check MySQL is running
2. Verify credentials in DBconnection.java
3. Ensure database exists: `SHOW DATABASES;`

#### "Payment Failed" on free order
**Solution:**
- Ensure migration script was run
- Check OrderPaymentController has latest code
- Verify points are sufficient

#### "Notification not appearing"
**Solution:**
- Run migration: `add-order-preparing-type.sql`
- Check notification_history table exists
- Verify NotificationHistoryService is working

#### "Menu item not showing for students"
**Solution:**
- Check item is marked as ACTIVE
- Login as Menu Manager
- Toggle item to Active

#### "Points not updating"
**Solution:**
- Refresh dashboard (click Refresh button)
- Logout and login again
- Check database: `SELECT * FROM student WHERE code='ST001';`

---

## 📞 Support & Documentation

### Additional Documentation
- `MENU-ACTIVE-INACTIVE-FEATURE.md` - Menu management guide
- `ORDER-PREPARING-IMPLEMENTATION.md` - Notification system
- `FINAL-IMPLEMENTATION-SUMMARY.md` - Complete feature list
- `STUDENT-ENHANCEMENTS-SUMMARY.md` - Student app features

### Test Accounts

**Students:**
- Code: `st001` - `st010`
- Password: `password123`

**Managers:**
- All roles: password is `password123`
- Usernames: `itadmin1`, `admin1`, `student1`, `menu1`, `order1`, etc.

---

## ✨ Key Features Summary

### StudentGUIApp
✅ Browse menu with real-time updates
✅ Shopping cart with quantity control
✅ Loyalty points redemption
✅ FREE orders with full point redemption
✅ Real-time point updates (no logout)
✅ Color-coded notifications (orange/green)
✅ Order history tracking
✅ Reward notifications

### AdminGUIApp
✅ Role-based access control
✅ 8 different manager roles
✅ Menu item active/inactive toggle
✅ Order status management
✅ Automatic notification sending
✅ Loyalty point management
✅ Student registration
✅ Comprehensive reporting

### Terminal Console
✅ Full system access via CLI
✅ Student and admin portals
✅ All features available
✅ No GUI required
✅ Lightweight and fast

---

## 🎓 Getting Help

If you encounter issues:
1. Check this guide first
2. Review error messages in console
3. Check database connection
4. Verify all prerequisites installed
5. Ensure migration scripts run
6. Check documentation files

**Happy ordering! ☕🍽️**
