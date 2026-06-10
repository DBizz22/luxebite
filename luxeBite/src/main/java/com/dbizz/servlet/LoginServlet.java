package com.dbizz.servlet;

import java.io.IOException;

import com.dbizz.database.mysql.MySqlUserRepo;
import com.dbizz.model.User;
import com.dbizz.repo.UserRepo;
import com.dbizz.service.UserService;
import com.dbizz.util.DBConnection;
import com.dbizz.util.EmailUtil;
import com.dbizz.util.PhoneNoUtil;
import com.dbizz.util.TokenUtil;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "LoginServlet", urlPatterns = { "/login" }, initParams = {
        @WebInitParam(name = "PARAM_EMAIL", value = "email"),
        @WebInitParam(name = "PARAM_PW", value = "password")
})
public class LoginServlet extends HttpServlet {

    private String PARAM_EMAIL;
    private String PARAM_PW;

    private UserRepo userRepo;
    private UserService userService;

    @Override
    public void init() throws ServletException {
        PARAM_EMAIL = getInitParameter("PARAM_EMAIL");
        PARAM_PW = getInitParameter("PARAM_PW");
        userRepo = new MySqlUserRepo(DBConnection.getMySQLDataSource());
        userService = new UserService(userRepo);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("========== LoginServlet Called ==========");
        System.out.println("Context Path: " + req.getContextPath());
        System.out.println("Request URI: " + req.getRequestURI());
        System.out.println("Servlet Path: " + req.getServletPath());
        System.out.println("========================================");

        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/login.jsp");
        dispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("========== LoginServlet Called ==========");
        System.out.println("Context Path: " + req.getContextPath());
        System.out.println("Request URI: " + req.getRequestURI());
        System.out.println("Servlet Path: " + req.getServletPath());
        System.out.println("========================================");

        String email = req.getParameter(PARAM_EMAIL);
        String password = req.getParameter(PARAM_PW);
        User user = new User(0, null, email, null, password, null);

        try {
            user = userService.login(user);

            if (user == null) {
                throw new IllegalArgumentException("Invalid email or password");
            }

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", "Login failed: " + e.getMessage());
            RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/login.jsp");
            dispatcher.forward(req, resp);
            return;
        }

        HttpSession session = req.getSession();
        String token = TokenUtil.generateToken();
        req.setAttribute(TokenUtil.CSRF_TOKEN, token);
        session.setAttribute(TokenUtil.CSRF_TOKEN, token);
        session.setAttribute("LOGGED_IN_USER", user);
        resp.sendRedirect(req.getContextPath() + "/products");
    }

}
