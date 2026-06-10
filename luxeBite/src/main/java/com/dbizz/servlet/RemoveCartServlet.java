package com.dbizz.servlet;

import java.util.List;
import java.util.Map;

import com.dbizz.database.mysql.MySqlOrderItemRepo;
import com.dbizz.database.mysql.MySqlOrderRepo;
import com.dbizz.database.mysql.MySqlProductRepo;
import com.dbizz.model.Order;
import com.dbizz.model.OrderItem;
import com.dbizz.model.User;
import com.dbizz.repo.OrderItemRepo;
import com.dbizz.repo.OrderRepo;
import com.dbizz.repo.ProductRepo;
import com.dbizz.service.OrderService;
import com.dbizz.util.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import tools.jackson.databind.ObjectMapper;

@WebServlet(name = "RemoveCartServlet", urlPatterns = "/cart/remove")
public class RemoveCartServlet extends HttpServlet {

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
    protected void doPost(jakarta.servlet.http.HttpServletRequest req, jakarta.servlet.http.HttpServletResponse resp)
            throws jakarta.servlet.ServletException, java.io.IOException {
        System.out.println("========== RemoveCartServlet Called ==========");
        System.out.println("Context Path: " + req.getContextPath());
        System.out.println("Request URI: " + req.getRequestURI());
        System.out.println("Servlet Path: " + req.getServletPath());
        System.out.println("========================================");

        User currentUser = (User) req.getSession().getAttribute("LOGGED_IN_USER");
        if (currentUser == null) {
            resp.setStatus(jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("User not logged in.");
            return;
        }
        resp.setContentType("application/json");

        int orderItemId = Integer.parseInt(req.getParameter("itemId"));

        try {
            OrderItem orderItemToDelete = orderItemRepo.findById(orderItemId);
            orderService.cancelOrderItem(currentUser, orderItemToDelete);
            resp.setStatus(jakarta.servlet.http.HttpServletResponse.SC_OK);
            Map<String, Object> responseMap = Map.of("success", true, "message", "Item removed from cart.");
            objectMapper.writeValue(resp.getWriter(), responseMap);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, Object> responseMap = Map.of("success", false, "message", "Item not found or already removed");
            objectMapper.writeValue(resp.getWriter(), responseMap);
        }
    }
}
