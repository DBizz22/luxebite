package com.dbizz.servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.dbizz.database.mysql.MySqlOrderItemRepo;
import com.dbizz.database.mysql.MySqlOrderRepo;
import com.dbizz.database.mysql.MySqlProductRepo;
import com.dbizz.model.OrderItem;
import com.dbizz.model.PaymentStatus;
import com.dbizz.model.User;
import com.dbizz.repo.OrderItemRepo;
import com.dbizz.repo.OrderRepo;
import com.dbizz.repo.ProductRepo;
import com.dbizz.service.OrderService;
import com.dbizz.util.DBConnection;
import com.dbizz.model.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

@WebServlet(name = "UpdateCartServlet", urlPatterns = "/cart/update")
public class UpdateCartServlet extends HttpServlet {

    private ProductRepo productRepo;
    private OrderRepo orderRepo;
    private OrderItemRepo orderItemRepo;
    private OrderService orderService;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init() throws ServletException {
        productRepo = new MySqlProductRepo(DBConnection.getMySQLDataSource());
        orderRepo = new MySqlOrderRepo(DBConnection.getMySQLDataSource());
        orderItemRepo = new MySqlOrderItemRepo(DBConnection.getMySQLDataSource());
        orderService = new OrderService(productRepo, orderRepo, orderItemRepo);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("========== UpdateCartServlet Called ==========");
        System.out.println("Context Path: " + req.getContextPath());
        System.out.println("Request URI: " + req.getRequestURI());
        System.out.println("Servlet Path: " + req.getServletPath());
        System.out.println("========================================");

        User currentUser = (User) req.getSession().getAttribute("LOGGED_IN_USER");
        if (currentUser == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("User not logged in.");
            return;
        }
        resp.setContentType("application/json");

        String itemIdParam = req.getParameter("itemId");
        String quantityParam = req.getParameter("quantity");

        if (itemIdParam == null || quantityParam == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            Map<String, Object> responseMap = Map.of(
                    "success", false,
                    "message", "Missing required parameters: itemId or quantity");
            objectMapper.writeValue(resp.getWriter(), responseMap);
            return;
        }

        int orderItemId = Integer.parseInt(itemIdParam);
        int quantity = Integer.parseInt(quantityParam);

        try {
            OrderItem existingItem = orderItemRepo.findById(orderItemId);
            if (existingItem == null) {
                throw new Exception("Order item not found with id: " + orderItemId);
            }

            // Verify the item belongs to a pending order of the current user
            Order order = orderRepo.findById(existingItem.orderId());
            if (order == null || order.userId() != currentUser.id() || order.status() != PaymentStatus.PENDING) {
                throw new Exception("Invalid order item or order is not pending");
            }

            // Update the order item with new quantity
            OrderItem updatedItem = new OrderItem(
                    existingItem.id(),
                    existingItem.orderId(),
                    existingItem.productId(),
                    existingItem.unitPrice(),
                    quantity,
                    existingItem.createdAt());

            int result = orderItemRepo.update(updatedItem);
            if (result < 1) {
                throw new Exception("Failed to update order item");
            }

            resp.setStatus(HttpServletResponse.SC_OK);

            Map<String, Object> responseMap = Map.of(
                    "success", true,
                    "message", "Quantity updated successfully",
                    "unitPrice", updatedItem.unitPrice(),
                    "newQuantity", updatedItem.quantity());
            objectMapper.writeValue(resp.getWriter(), responseMap);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, Object> responseMap = Map.of(
                    "success", false,
                    "message", "Error updating quantity: " + e.getMessage());
            objectMapper.writeValue(resp.getWriter(), responseMap);
        }
        // req.getRequestDispatcher("/WEB-INF/views/cart.jsp").forward(req, resp);
    }

}
