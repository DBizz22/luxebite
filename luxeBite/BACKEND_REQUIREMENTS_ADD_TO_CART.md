# Backend Requirements: Add to Cart Servlet Response

## Overview
The frontend now uses AJAX to add items to cart and expects a JSON response to update the UI without page reload.

## Endpoint
**POST** `/cart/add`

## Request Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `productId` | Integer | Yes | The ID of the product to add |
| `productPrice` | BigDecimal | Yes | The price of the product |
| `quantity` | Integer | Yes | Number of items to add (default: 1) |
| `CSRF_TOKEN` | String | Yes | CSRF token for security validation |

## Response Format

### Success Response
**Content-Type:** `application/json`

```json
{
  "success": true,
  "message": "Product added to cart successfully",
  "pendingItemCount": 5
}
```

### Error Response
```json
{
  "success": false,
  "message": "Failed to add product to cart",
  "pendingItemCount": 0
}
```

## Response Fields

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `success` | Boolean | Yes | Indicates if the operation was successful |
| `message` | String | Yes | User-friendly message to display in toast notification |
| `pendingItemCount` | Integer | Yes | Total count of items in pending orders (for cart badge) |

## Implementation Requirements

### 1. Servlet Configuration
```java
@WebServlet("/cart/add")
public class AddToCartServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Implementation here
    }
}
```

### 2. CSRF Token Validation
- Validate the `CSRF_TOKEN` from request against session
- Return error response if validation fails:
```json
{
  "success": false,
  "message": "Invalid CSRF token",
  "pendingItemCount": 0
}
```

### 3. Calculate Pending Item Count
The `pendingItemCount` should be the total number of items across all PENDING orders for the user.

**SQL Query Example:**
```sql
SELECT COALESCE(SUM(oi.quantity), 0) as pendingItemCount
FROM order_items oi
JOIN orders o ON oi.order_id = o.id
WHERE o.user_id = ? AND o.payment_status = 'PENDING'
```

### 4. Business Logic

#### Add New Item to Cart
1. Check if user has an existing PENDING order
2. If yes:
   - Check if product already exists in order items
   - If exists: Update quantity (existing + new)
   - If not: Add new order item
3. If no PENDING order:
   - Create new order with status PENDING
   - Add order item

#### Example Implementation Flow
```java
// 1. Validate CSRF Token
String sessionToken = (String) request.getSession().getAttribute("CSRF_TOKEN");
String requestToken = request.getParameter("CSRF_TOKEN");
if (!requestToken.equals(sessionToken)) {
    writeErrorResponse(response, "Invalid CSRF token");
    return;
}

// 2. Get parameters
int productId = Integer.parseInt(request.getParameter("productId"));
BigDecimal productPrice = new BigDecimal(request.getParameter("productPrice"));
int quantity = Integer.parseInt(request.getParameter("quantity"));
User user = (User) request.getSession().getAttribute("LOGGED_IN_USER");

// 3. Add to cart logic
OrderService orderService = new OrderService();
boolean success = orderService.addToCart(user.id(), productId, productPrice, quantity);

// 4. Get pending item count
int pendingItemCount = orderService.getPendingItemCount(user.id());

// 5. Send response
JsonObject jsonResponse = new JsonObject();
jsonResponse.addProperty("success", success);
jsonResponse.addProperty("message", success ? 
    "Product added to cart successfully" : "Failed to add product to cart");
jsonResponse.addProperty("pendingItemCount", pendingItemCount);

response.getWriter().write(jsonResponse.toString());
```

## Error Scenarios

### 1. Product Not Found
```json
{
  "success": false,
  "message": "Product not found",
  "pendingItemCount": 0
}
```

### 2. Insufficient Stock
```json
{
  "success": false,
  "message": "Insufficient stock available",
  "pendingItemCount": 3
}
```

### 3. Invalid Quantity
```json
{
  "success": false,
  "message": "Invalid quantity specified",
  "pendingItemCount": 3
}
```

### 4. User Not Logged In
```json
{
  "success": false,
  "message": "Please login to add items to cart",
  "pendingItemCount": 0
}
```

## Database Operations

### Insert New Order
```sql
INSERT INTO orders (user_id, order_date, payment_status, total_amount)
VALUES (?, NOW(), 'PENDING', 0.00)
```

### Insert Order Item
```sql
INSERT INTO order_items (order_id, product_id, unit_price, quantity)
VALUES (?, ?, ?, ?)
```

### Update Existing Order Item
```sql
UPDATE order_items 
SET quantity = quantity + ?
WHERE order_id = ? AND product_id = ?
```

### Update Order Total
```sql
UPDATE orders 
SET total_amount = (
    SELECT SUM(unit_price * quantity) 
    FROM order_items 
    WHERE order_id = ?
)
WHERE id = ?
```

## Frontend Integration

### Initial Page Load
Set the `pendingItemCount` attribute in request before forwarding to JSP:
```java
int pendingItemCount = orderService.getPendingItemCount(user.id());
request.setAttribute("pendingItemCount", pendingItemCount);
request.getRequestDispatcher("/WEB-INF/views/products.jsp").forward(request, response);
```

### Cart Badge Behavior
- Badge is **hidden** when `pendingItemCount = 0`
- Badge is **visible** and shows count when `pendingItemCount > 0`
- Badge updates automatically after successful add-to-cart

## Testing Checklist

- [ ] Add first item creates new PENDING order
- [ ] Add same product increases quantity
- [ ] Add different product creates new order item
- [ ] Cart badge shows correct count after add
- [ ] Cart badge hides when count is 0
- [ ] CSRF token validation works
- [ ] Error messages display correctly
- [ ] Stock validation works
- [ ] Invalid input handled gracefully
- [ ] Session timeout handled

## Success Messages (Suggestions)

- "Added to cart successfully!"
- "Added {quantity}x {productName} to cart"
- "Cart updated successfully"

## Error Messages (Suggestions)

- "Failed to add to cart. Please try again."
- "This product is out of stock"
- "Invalid quantity. Please enter a positive number."
- "Your session has expired. Please login again."
- "Product not available at this time"
