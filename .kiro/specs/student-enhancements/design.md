# Student GUI App Enhancements - Design

## Architecture Overview

### 1. Order Status Notification System

#### Components
- **OrderManagerDashboardController**: Trigger notification when status changes to PREPARING
- **NotificationHistoryService**: Store notification with special type
- **StudentGUIApp**: Display gold-styled notification

#### Flow
```
Order Manager changes status to PREPARING
    ↓
OrderManagerDashboardController.handleUpdateStatus()
    ↓
NotificationHistoryService.sendOrderPreparingNotification(studentCode, orderCode)
    ↓
Student sees gold notification on next refresh/login
```

#### Notification Format
- **Type:** ORDER_READY (reuse existing enum or add ORDER_PREPARING)
- **Message:** "🌟 Great news! Your order [ORDER_CODE] is now being prepared! 🌟"
- **Style:** Gold/yellow background with special icon

---

### 2. Loyalty Points Award Notification

#### Components
- **StudentManagerDashboardController**: Trigger notification when awarding points
- **NotificationHistoryService**: Store reward notification
- **LoyaltyProgramService**: Award points method

#### Flow
```
Student Manager awards points
    ↓
StudentManagerDashboardController.handleAwardPoints()
    ↓
LoyaltyProgramService.awardPoints(studentCode, points)
    ↓
NotificationHistoryService.sendRewardNotification(studentCode, points)
    ↓
Student sees "Reward Surprise" notification
```

#### Notification Format
- **Type:** GENERAL or new REWARD type
- **Message:** "🎁 Surprise! You've received [X] bonus loyalty points! 🎁"
- **Style:** Special reward styling

---

### 3. Free Product with Full Point Redemption

#### Problem Analysis
Current payment flow fails when amount = 0 because:
1. Payment processor expects amount > 0
2. No handling for "free" transactions
3. Points are refunded on payment failure

#### Solution Design

##### A. Payment Processor Update
```java
// In PaymentProcessor or OrderPaymentController
if (finalAmount <= 0) {
    // Skip payment processing
    // Mark as FREE transaction
    // Complete order directly
    return new PaymentResult(true, "FREE", "Paid with loyalty points");
}
```

##### B. Free Product Calculator
Create a new service method to calculate what items can be obtained for free:

```java
public class FreeProductCalculator {
    public List<FreeProductOption> calculateFreeProducts(int availablePoints) {
        // Get all active menu items
        // Calculate which items can be fully covered by points
        // Return sorted list by points required
    }
}
```

##### C. UI Updates
- Show "FREE" badge when total = 0 EGP
- Display available free products in a dialog/section
- Update payment confirmation message

#### Flow
```
Student redeems max points → Total = 0 EGP
    ↓
OrderPaymentController detects finalAmount = 0
    ↓
Skip payment processing
    ↓
Create order with status "PAID_WITH_POINTS"
    ↓
Show success: "Order placed! Paid with loyalty points (FREE)"
    ↓
Update loyalty points in UI
```

---

### 4. Real-Time Loyalty Points Update

#### Problem Analysis
Points are updated in database but not reflected in UI because:
1. MenuDashboardController caches student object
2. No refresh mechanism after order completion
3. Navigation back doesn't reload student data

#### Solution Design

##### A. Add Refresh Mechanism
```java
// In MenuDashboardController
public void refreshLoyaltyPoints() {
    if (currentStudent != null && loyaltyService != null) {
        int updatedPoints = loyaltyService.getBalance(currentStudent);
        currentStudent.setPoints(updatedPoints); // Update cached object
        updateLoyaltyPointsDisplay();
    }
}
```

##### B. Call Refresh After Order
```java
// In OrderPaymentController after successful payment
// Navigate back with refresh flag
StudentNavigationService.navigateToWithRefresh("menu-dashboard", currentStudent);
```

##### C. Navigation Service Update
```java
// In StudentNavigationService
public static void navigateToWithRefresh(String sceneName, Object data) {
    navigateToWithData(sceneName, data);
    // Trigger refresh on the controller
    if (currentController instanceof MenuDashboardController) {
        ((MenuDashboardController) currentController).refreshLoyaltyPoints();
    }
}
```

---

## Database Schema Changes

### Option 1: Add ORDER_PREPARING to notification types
```sql
ALTER TABLE notification_history 
MODIFY COLUMN message_type ENUM('SALE', 'ORDER_READY', 'ORDER_PREPARING', 'REWARD', 'GENERAL');
```

### Option 2: Use existing types
- ORDER_PREPARING → Use 'ORDER_READY' type
- REWARD → Use 'GENERAL' type

**Recommendation:** Use existing types to avoid schema changes.

---

## UI/UX Design

### Gold Notification Style
```css
.notification-gold {
    -fx-background-color: linear-gradient(to right, #FFD700, #FFA500);
    -fx-text-fill: #000000;
    -fx-font-weight: bold;
    -fx-border-color: #FF8C00;
    -fx-border-width: 2px;
}
```

### Free Product Display
- Show in payment screen when points can cover full amount
- Display as a list with item name, price, and required points
- Highlight items student can afford with current points

### Real-Time Update Indicator
- Show brief "Points Updated!" message after order
- Animate loyalty points label when value changes

---

## Error Handling

### Zero Amount Payment
- Don't call payment processor if amount = 0
- Create transaction record with special status
- Log as successful "free" transaction

### Point Redemption Failure
- Validate points before redemption
- Show clear error if insufficient points
- Don't deduct points until order is confirmed

### Notification Delivery
- Queue notifications if database is unavailable
- Retry failed notifications
- Log notification errors without blocking order flow

---

## Testing Strategy

### Test Cases

#### TC1: Order Preparing Notification
1. Place order as student
2. Login as Order Manager
3. Change order status to PREPARING
4. Return to student app
5. Verify gold notification appears

#### TC2: Reward Points Notification
1. Login as Student Manager
2. Award points to a student
3. Login as that student
4. Verify reward notification appears

#### TC3: Free Product with Full Redemption
1. Student has 200 points
2. Add item worth 200 points to cart
3. Redeem all 200 points
4. Confirm payment
5. Verify order completes successfully
6. Verify no payment failure error

#### TC4: Real-Time Points Update
1. Student has 0 points
2. Place order worth 100 EGP
3. Complete payment
4. Verify points update immediately on menu dashboard
5. Verify can redeem new points without logout

#### TC5: Free Product List
1. Student has 150 points
2. View payment screen
3. Verify list shows items that can be obtained for free
4. Verify items are sorted by points required

---

## Performance Considerations

- Cache free product calculations
- Optimize loyalty point queries
- Batch notification inserts
- Minimize UI redraws during point updates

---

## Security Considerations

- Validate point redemption server-side
- Prevent negative point balances
- Verify order ownership before point award
- Sanitize notification messages
