package com.dbizz.filter;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

import com.dbizz.util.TokenUtil;

import ch.qos.logback.core.subst.Token;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter(filterName = "CSRFFilter", urlPatterns = { "/products", "/admin_info", "/cart/*", "/checkout" })
public class CSRFFilter implements Filter {

    // private static final SecureRandom secureRandom = new SecureRandom();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        System.out.println("========== CSRFFilter Called ==========");
        System.out.println("Context Path: " + httpRequest.getContextPath());
        System.out.println("Request URI: " + httpRequest.getRequestURI());
        System.out.println("Servlet Path: " + httpRequest.getServletPath());
        System.out.println("========================================");

        HttpSession session = httpRequest.getSession();

        String token = (String) session.getAttribute(TokenUtil.CSRF_TOKEN);

        // Allow safe methods without CSRF token
        String method = httpRequest.getMethod();
        if ("GET".equalsIgnoreCase(method) || "HEAD".equalsIgnoreCase(method) || "OPTIONS".equalsIgnoreCase(method)) {
            chain.doFilter(request, response);
            return;
        }

        // Allow login endpoint
        if (httpRequest.getRequestURI().endsWith("/login")) {
            chain.doFilter(request, response);
            return;
        }

        // For state-changing methods, require token (param or header)
        String requestToken = httpRequest.getParameter(TokenUtil.CSRF_TOKEN);
        if (requestToken == null) {
            requestToken = httpRequest.getHeader("X-CSRF-TOKEN");
        }

        if (requestToken == null || token == null || !requestToken.equals(token)) {
            System.out.println("CSRF token mismatch. Request denied.");
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF token");
            return;
        }

        chain.doFilter(request, response);
    }

}
