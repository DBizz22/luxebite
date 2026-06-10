package com.dbizz.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter(filterName = "AuthenticationFilter", urlPatterns = { "/products", "/admin_info", "/cart/*", "/checkout" })
public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();
        System.out.println("[AuthFilter] Intercepted URI: " + requestURI);
        if (requestURI.startsWith(httpRequest.getContextPath() + "/images/") ||
                requestURI.endsWith(".css") ||
                requestURI.endsWith(".js") ||
                requestURI.endsWith(".png") ||
                requestURI.endsWith(".jpg") ||
                requestURI.endsWith(".jpeg") ||
                requestURI.endsWith(".gif") ||
                requestURI.endsWith(".svg")) {
            System.out.println("[AuthFilter] Bypassing static resource: " + requestURI);
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(false);
        System.out.println("========== AuthenticationFilter Called ==========");
        System.out.println("Context Path: " + httpRequest.getContextPath());
        System.out.println("Request URI: " + httpRequest.getRequestURI());
        System.out.println("Servlet Path: " + httpRequest.getServletPath());
        System.out.println("========================================");

        if (session != null && session.getAttribute("LOGGED_IN_USER") != null) {
            System.out.println("User is logged in. Proceeding with request.");
            System.out.println("Session ID: " + session.getId());
            System.out.println("Logged In User: " + session.getAttribute("LOGGED_IN_USER"));
            chain.doFilter(request, response);
            return;
        }

        String loginURI = httpRequest.getContextPath() + "/login";
        boolean isLoginRequest = httpRequest.getRequestURI().equals(loginURI);
        boolean isLoginPage = httpRequest.getRequestURI().endsWith("login.jsp");

        if (!isLoginRequest && !isLoginPage) {
            System.out.println("User not logged in. Redirecting to login page.");
            String returnURI = httpRequest.getRequestURI();
            httpRequest.getSession().setAttribute("RETURN_URI", returnURI);
            httpResponse.sendRedirect(loginURI);
        } else {
            System.out.println("User not logged in. Proceeding to login page.");
            chain.doFilter(request, response);
        }

    }

}
