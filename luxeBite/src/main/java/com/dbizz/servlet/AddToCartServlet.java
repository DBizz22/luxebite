package com.dbizz.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.dbizz.database.mysql.MySqlOrderItemRepo;
import com.dbizz.database.mysql.MySqlOrderRepo;
import com.dbizz.database.mysql.MySqlProductRepo;
import com.dbizz.model.OrderItem;
import com.dbizz.model.Order;
import com.dbizz.model.PaymentStatus;
import com.dbizz.model.Product;
import com.dbizz.model.User;
import com.dbizz.repo.OrderItemRepo;
import com.dbizz.repo.OrderRepo;
import com.dbizz.repo.ProductRepo;
import com.dbizz.service.OrderService;
import com.dbizz.util.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

@WebServlet(name = "AddToCartServlet", urlPatterns = "/cart/add")
public class AddToCartServlet extends HttpServlet {

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
        System.out.println("========== AddToCartServlet Called ==========");
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
        resp.setCharacterEncoding("UTF-8");
        try {
            int productId = Integer.parseInt(req.getParameter("productId"));
            double productPrice = Double.parseDouble(req.getParameter("productPrice"));
            int quantity = Integer.parseInt(req.getParameter("quantity"));

            OrderItem orderItem = new OrderItem(0, 0, productId, productPrice, quantity, null);
            orderItem = orderService.saveOrder(currentUser, orderItem);
            if (orderItem == null) {
                throw new Exception("Failed to add item to cart.");
            }
            int pendingItemCount = 0;
            Order pendingOrder = orderService.getAllOrders(currentUser, PaymentStatus.PENDING).stream().findFirst()
                    .orElse(null);
            if (pendingOrder != null)
                pendingItemCount = orderService.getAllOrderItems(currentUser, pendingOrder).size();
            Map<String, Object> responseMap = Map.of(
                    "success", true,
                    "message", "Item added to cart.",
                    "pendingItemCount", pendingItemCount);
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getWriter(), responseMap);

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, Object> responseMap = Map.of(
                    "success", false,
                    "message", e.getMessage(),
                    "pendingItemCount", 0);
            objectMapper.writeValue(resp.getWriter(), responseMap);
        }
    }
}
