# Backend Requirements for Cart/Orders Page

## Overview
The cart.jsp has been redesigned to display **pending** and **completed** orders with their order items. Users can modify quantities for pending orders, remove items, and checkout.

---

## Required Servlet/Endpoint: `/cart` (GET)

### Purpose
Display the cart/orders page with pending and completed orders.

### Request Parameters
None (uses session user)

### Required Session Attributes to Set
```java
// User information
request.setAttribute("LOGGED_IN_USER", user);

// Pending orders (PaymentStatus.PENDING)
request.setAttribute("pendingOrders", List<Order>);

// Completed orders (PaymentStatus.COMPLETED)
request.setAttribute("completedOrders", List<Order>);

// Map of order items grouped by orderId
// Key: orderId (Integer), Value: List of OrderItemWithProduct
request.setAttribute("orderItemsMap", Map<Integer, List<OrderItemWithProduct>>);

// CSRF Token
session.setAttribute("CSRF_TOKEN", csrfToken);

// Optional: Success/Error messages
session.setAttribute("successMessage", "Item added to cart!");
session.setAttribute("errorMessage", "Failed to add item");
```

### OrderItemWithProduct DTO
You'll need to create a DTO or use a wrapper that includes both OrderItem and Product:
```java
public class OrderItemWithProduct {
    private OrderItem orderItem;
    private Product product;
    
    // Constructor, getters
    public int getId() { return orderItem.id(); }
    public int getOrderId() { return orderItem.orderId(); }
    public int getProductId() { return orderItem.productId(); }
    public double getUnitPrice() { return orderItem.unitPrice(); }
    public int getQuantity() { return orderItem.quantity(); }
    public Product getProduct() { return product; }
}
```

Or use a Map in JSP access pattern:
```java
// In JSP: ${item.product.name()} expects item to have a getProduct() method
```

### Response
Forward to: `/WEB-INF/views/cart.jsp`

---

## Required Endpoint: `/cart/update` (POST)

### Purpose
Update the quantity of an order item in a pending order.

### Request Parameters
```
itemId: int (OrderItem ID)
quantity: int (new quantity, must be >= 1)
CSRF_TOKEN: String
```

### Business Logic
1. Validate CSRF token
2. Verify the order item exists
3. Verify the order is PENDING (cannot modify completed orders)
4. Verify the order belongs to the logged-in user
5. Update the quantity in the database
6. Return JSON response

### JSON Response
**Success:**
```json
{
    "success": true,
    "message": "Quantity updated successfully",
    "unitPrice": 25.99,
    "newQuantity": 3
}
```

**Error:**
```json
{
    "success": false,
    "message": "Cannot modify completed orders"
}
```

### Response Headers
```
Content-Type: application/json
```

---

## Required Endpoint: `/cart/remove` (POST)

### Purpose
Remove an order item from a pending order.

### Request Parameters
```
itemId: int (OrderItem ID)
CSRF_TOKEN: String
```

### Business Logic
1. Validate CSRF token
2. Verify the order item exists
3. Verify the order is PENDING
4. Verify the order belongs to the logged-in user
5. Delete the order item from database
6. **IMPORTANT**: If the order has no more items, delete the order itself
7. Return JSON response

### JSON Response
**Success:**
```json
{
    "success": true,
    "message": "Item removed successfully"
}
```

**Error:**
```json
{
    "success": false,
    "message": "Item not found or already removed"
}
```

---

## Required Endpoint: `/checkout` (POST)

### Purpose
Process checkout for a pending order (change status to COMPLETED).

### Request Parameters
```
orderId: int
CSRF_TOKEN: String
```

### Business Logic
1. Validate CSRF token
2. Verify the order exists
3. Verify the order is PENDING
4. Verify the order belongs to the logged-in user
5. Verify the order has at least one item
6. Update order status to COMPLETED
7. Set success message in session
8. Redirect to cart page

### Response
**Success:**
- Set session attribute: `successMessage = "Order #123 completed successfully!"`
- Redirect to: `/cart`

**Error:**
- Set session attribute: `errorMessage = "Checkout failed: [reason]"`
- Redirect to: `/cart`

---

## Database Queries Needed

### 1. Get All Orders by User ID and Status
```sql
SELECT * FROM orders 
WHERE user_id = ? AND status = ? 
ORDER BY created_at DESC;
```

### 2. Get Order Items with Product Details
```sql
SELECT oi.*, p.* 
FROM order_items oi
JOIN products p ON oi.product_id = p.id
WHERE oi.order_id = ?;
```

### 3. Update Order Item Quantity
```sql
UPDATE order_items 
SET quantity = ? 
WHERE id = ?;
```

### 4. Delete Order Item
```sql
DELETE FROM order_items 
WHERE id = ?;
```

### 5. Count Order Items for an Order
```sql
SELECT COUNT(*) FROM order_items 
WHERE order_id = ?;
```

### 6. Delete Order (if empty)
```sql
DELETE FROM orders 
WHERE id = ?;
```

### 7. Update Order Status
```sql
UPDATE orders 
SET status = ? 
WHERE id = ?;
```

---

## Additional Notes

### Error Handling
- Always validate that the user owns the order before any modifications
- Validate CSRF tokens on all POST requests
- Return appropriate HTTP status codes (200, 400, 403, 404)
- Clear success/error messages from session after displaying (to prevent re-display on refresh)

### Session Message Cleanup
After displaying messages in JSP, remove them:
```java
session.removeAttribute("successMessage");
session.removeAttribute("errorMessage");
```

### Security Considerations
1. Always verify order ownership (order.userId == loggedInUser.id)
2. Validate CSRF tokens on all state-changing operations
3. Only allow modifications to PENDING orders
4. Sanitize all user inputs

### Optional Enhancements
1. Add stock validation when updating quantities
2. Calculate and display delivery estimates
3. Add order notes/comments
4. Email notifications on checkout
5. Order cancellation for pending orders

---

## Frontend Features Implemented

✅ **Toast Notifications** - Success/error messages with auto-dismiss
✅ **Tab Switching** - Toggle between pending and completed orders
✅ **Quantity Controls** - +/- buttons with real-time updates
✅ **Remove Items** - Delete confirmation before removal
✅ **Order Summary** - Subtotal, tax (8.875%), and total calculations
✅ **Checkout Button** - Submit order for completion
✅ **Responsive Design** - Mobile-friendly layout
✅ **CSRF Protection** - Tokens included in all POST requests
✅ **Empty States** - Friendly messages when no orders exist

---

## Testing Checklist

- [ ] Load cart page with no orders
- [ ] Load cart page with pending orders
- [ ] Load cart page with completed orders
- [ ] Update quantity in pending order
- [ ] Try to decrease quantity below 1 (should prompt removal)
- [ ] Remove item from pending order
- [ ] Remove last item from order (order should be deleted)
- [ ] Try to modify completed order (should fail)
- [ ] Checkout a pending order
- [ ] Verify CSRF protection on all POST endpoints
- [ ] Test with multiple concurrent users
- [ ] Test session message display and cleanup

---

## Frontend → Backend Communication Summary

| Action | Method | Endpoint | Parameters | Response Type |
|--------|--------|----------|-----------|---------------|
| Load Page | GET | `/cart` | None | HTML (JSP) |
| Update Quantity | POST | `/cart/update` | itemId, quantity, CSRF_TOKEN | JSON |
| Remove Item | POST | `/cart/remove` | itemId, CSRF_TOKEN | JSON |
| Checkout | POST | `/checkout` | orderId, CSRF_TOKEN | Redirect |
