package com.dbizz.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.dbizz.database.mysql.MySqlOrderItemRepo;
import com.dbizz.database.mysql.MySqlOrderRepo;
import com.dbizz.database.mysql.MySqlProductRepo;
import com.dbizz.model.Order;
import com.dbizz.model.OrderItem;
import com.dbizz.model.OrderItemWithProduct;
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

@WebServlet(name = "CartServlet", urlPatterns = "/cart")
public class CartServlet extends HttpServlet {

    private ProductRepo productRepo;
    private OrderRepo orderRepo;
    private OrderItemRepo orderItemRepo;
    private OrderService orderService;

    @Override
    public void init() throws ServletException {
        productRepo = new MySqlProductRepo(DBConnection.getMySQLDataSource());
        orderRepo = new MySqlOrderRepo(DBConnection.getMySQLDataSource());
        orderItemRepo = new MySqlOrderItemRepo(DBConnection.getMySQLDataSource());
        orderService = new OrderService(productRepo, orderRepo, orderItemRepo);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("========== CartServlet Called ==========");
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

        try {
            List<Order> pendingOrders = orderService.getAllOrders(currentUser, PaymentStatus.PENDING);
            List<Order> completedOrders = orderService.getAllOrders(currentUser, PaymentStatus.COMPLETED);
            req.setAttribute("pendingOrders", pendingOrders);
            req.setAttribute("completedOrders", completedOrders);

            Map<Integer, List<OrderItemWithProduct>> orderItemsMap = new HashMap<>();

            for (Order order : Stream.concat(pendingOrders.stream(), completedOrders.stream()).toList()) {
                List<OrderItem> items = orderService.getAllOrderItems(currentUser, order);
                List<OrderItemWithProduct> itemsWithProducts = items.stream()
                        .map(item -> {
                            Product product = null;
                            try {
                                product = productRepo.findById(item.productId());
                            } catch (Exception e) {
                                e.printStackTrace();
                                throw new RuntimeException("Failed to fetch product for order item. " + e.getMessage());
                            }
                            return new OrderItemWithProduct(item, product);
                        })
                        .toList();
                orderItemsMap.put(order.id(), itemsWithProducts);
            }

            req.setAttribute("orderItemsMap", orderItemsMap);

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", e.getMessage());
        }

        req.getRequestDispatcher("/WEB-INF/views/cart.jsp").forward(req, resp);
    }

}
