package com.dbizz.servlet;

import java.io.IOException;

import com.dbizz.database.mysql.MySqlUserRepo;
import com.dbizz.model.User;
import com.dbizz.repo.UserRepo;
import com.dbizz.service.UserService;
import com.dbizz.util.DBConnection;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "RegisterServlet", urlPatterns = "/register", initParams = {
        @WebInitParam(name = "PARAM_USERNAME", value = "username"),
        @WebInitParam(name = "PARAM_EMAIL", value = "email"),
        @WebInitParam(name = "PARAM_PHONE", value = "phone"),
        @WebInitParam(name = "PARAM_PW", value = "password")
})
public class RegisterServlet extends HttpServlet {

    private String PARAM_USERNAME;
    private String PARAM_EMAIL;
    private String PARAM_PHONE;
    private String PARAM_PW;

    private User user;
    private UserRepo userRepo;
    private UserService userService;

    @Override
    public void init() throws ServletException {
        PARAM_USERNAME = getInitParameter("PARAM_USERNAME");
        PARAM_EMAIL = getInitParameter("PARAM_EMAIL");
        PARAM_PHONE = getInitParameter("PARAM_PHONE");
        PARAM_PW = getInitParameter("PARAM_PW");

        userRepo = new MySqlUserRepo(DBConnection.getMySQLDataSource());
        userService = new UserService(userRepo);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("========== RegisterServlet Called ==========");
        System.out.println("Context Path: " + req.getContextPath());
        System.out.println("Request URI: " + req.getRequestURI());
        System.out.println("Servlet Path: " + req.getServletPath());
        System.out.println("========================================");

        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/register.jsp");
        dispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("========== RegisterServlet Called ==========");
        System.out.println("Context Path: " + req.getContextPath());
        System.out.println("Request URI: " + req.getRequestURI());
        System.out.println("Servlet Path: " + req.getServletPath());
        System.out.println("========================================");

        String username = req.getParameter(PARAM_USERNAME);
        String email = req.getParameter(PARAM_EMAIL);
        String phoneNo = req.getParameter(PARAM_PHONE);
        String password = req.getParameter(PARAM_PW);

        try {
            user = new User(0, username, email, phoneNo, password, null);
            int userId = userService.register(user);
            if (userId < 1) {
                throw new IllegalArgumentException("Please try again.");
            }

            req.setAttribute("successMessage", "Registration successful! Please log in.");
            RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/login.jsp");
            dispatcher.forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", "Registration failed: " + e.getMessage());
            RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/register.jsp");
            dispatcher.forward(req, resp);
        }

    }
}
