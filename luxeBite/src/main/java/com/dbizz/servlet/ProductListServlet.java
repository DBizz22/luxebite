package com.dbizz.servlet;

import java.io.IOException;
import java.util.List;

import com.dbizz.database.mysql.MySqlOrderItemRepo;
import com.dbizz.database.mysql.MySqlOrderRepo;
import com.dbizz.database.mysql.MySqlProductRepo;
import com.dbizz.model.Order;
import com.dbizz.model.PaymentStatus;
import com.dbizz.model.Product;
import com.dbizz.model.ProductCategory;
import com.dbizz.model.User;
import com.dbizz.repo.OrderItemRepo;
import com.dbizz.repo.OrderRepo;
import com.dbizz.repo.ProductRepo;
import com.dbizz.service.OrderService;
import com.dbizz.service.ProductService;
import com.dbizz.util.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ProductListServlet", urlPatterns = "/products")
public class ProductListServlet extends HttpServlet {

    private OrderRepo orderRepo;
    private OrderItemRepo orderItemRepo;
    private ProductRepo productRepo;
    private ProductService productService;
    private OrderService orderService;

    @Override
    public void init() throws ServletException {
        orderRepo = new MySqlOrderRepo(DBConnection.getMySQLDataSource());
        orderItemRepo = new MySqlOrderItemRepo(DBConnection.getMySQLDataSource());
        productRepo = new MySqlProductRepo(DBConnection.getMySQLDataSource());
        productService = new ProductService(productRepo, orderItemRepo);
        orderService = new OrderService(productRepo, orderRepo, orderItemRepo);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        System.out.println("========== ProductListServlet Called ==========");
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
            List<Product> topSellingProducts = productService.listTopSellingProducts(3);
            List<Product> mainProducts = productService.listProductsByCategory(ProductCategory.MAIN);
            List<Product> appetizerProducts = productService.listProductsByCategory(ProductCategory.APPETIZER);
            List<Product> dessertProducts = productService.listProductsByCategory(ProductCategory.SIDE);
            List<Product> drinkProducts = productService.listProductsByCategory(ProductCategory.DRINK);
            int pendingItemCount = 0;
            Order pendingOrder = orderService.getAllOrders(currentUser, PaymentStatus.PENDING).stream().findFirst()
                    .orElse(null);
            if (pendingOrder != null)
                pendingItemCount = orderService.getAllOrderItems(currentUser, pendingOrder).size();

            System.out.println("Top Selling Products Count: " + topSellingProducts.size());
            if (!topSellingProducts.isEmpty()) {
                System.out.println("First product: " + topSellingProducts.get(0).name() + " | Image URL: "
                        + topSellingProducts.get(0).imageUrl());
            }
            System.out.println("Main Products Count: " + mainProducts.size());
            System.out.println("Appetizer Products Count: " + appetizerProducts.size());
            System.out.println("Dessert Products Count: " + dessertProducts.size());
            System.out.println("Drink Products Count: " + drinkProducts.size());
            System.out.println("Pending Item Count in Cart: " + pendingItemCount);

            req.setAttribute("topSellingProducts", topSellingProducts);
            req.setAttribute("mainProducts", mainProducts);
            req.setAttribute("appetizerProducts", appetizerProducts);
            req.setAttribute("dessertProducts", dessertProducts);
            req.setAttribute("drinkProducts", drinkProducts);
            req.setAttribute("pendingItemCount", pendingItemCount);

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", "Failed to load products: " + e.getMessage());
        }

        // Forward to JSP page to display product list
        req.getRequestDispatcher("/WEB-INF/views/products.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("========== ProductListServlet Called ==========");
        System.out.println("Context Path: " + req.getContextPath());
        System.out.println("Request URI: " + req.getRequestURI());
        System.out.println("Servlet Path: " + req.getServletPath());
        System.out.println("========================================");
    }

}
