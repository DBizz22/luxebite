package com.dbizz.filter;

import java.io.IOException;

import com.dbizz.model.User;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter(filterName = "AuthorizationFilter", urlPatterns = "/admin_info")
public class AuthorizationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        System.out.println("========== AuthorizationFilter Called ==========");
        System.out.println("Context Path: " + httpRequest.getContextPath());
        System.out.println("Request URI: " + httpRequest.getRequestURI());
        System.out.println("Servlet Path: " + httpRequest.getServletPath());
        System.out.println("========================================");

        HttpSession session = httpRequest.getSession(false);
        if (session != null && session.getAttribute("ROLE") != null) {

            // TODO: Add logic to check if the user has admin privileges

            System.out.println("User has admin privileges. Proceeding with request.");
            System.out.println("Session ID: " + session.getId());
            System.out.println("User Role: " + session.getAttribute("ROLE"));
            chain.doFilter(request, response);
        } else {
            System.out.println("User not logged in or does not have admin privileges. Redirecting to login page.");
            String loginURI = httpRequest.getContextPath() + "/login";
            httpResponse.sendRedirect(loginURI);
        }
    }

}
