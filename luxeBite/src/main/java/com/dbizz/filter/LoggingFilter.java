package com.dbizz.filter;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;

import org.slf4j.Logger;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter(filterName = "LoggingFilter", urlPatterns = "/*")
public class LoggingFilter implements Filter {

    private static final java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("UTC"));

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        System.out.println("========== LoggingFilter Called ==========");
        System.out.println("Context Path: " + httpRequest.getContextPath());
        System.out.println("Request URI: " + httpRequest.getRequestURI());
        System.out.println("Servlet Path: " + httpRequest.getServletPath());
        System.out.println("========================================");

        long startTime = System.currentTimeMillis();

        // Log incoming request
        logRequest(httpRequest);
        // Let the request pass through
        chain.doFilter(request, response);

        // Log response details
        long timeTaken = System.currentTimeMillis() - startTime;
        logResponse(httpRequest, httpResponse, timeTaken);
    }

    private void logRequest(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();

        sb.append("\n========== INCOMING REQUEST ==========\n");
        sb.append("Time      : ").append(dtf.format(Instant.now())).append("\n");
        sb.append("IP        : ").append(getClientIp(request)).append("\n");
        sb.append("Method    : ").append(request.getMethod()).append("\n");
        sb.append("URL       : ").append(request.getRequestURL());
        if (request.getQueryString() != null) {
            sb.append("?").append(request.getQueryString());
        }
        sb.append("\n");
        sb.append("User      : ").append(getUsername(request)).append("\n");
        sb.append("Session ID: ").append(request.getSession(false) != null ? request.getSession(false).getId() : "none")
                .append("\n");
        sb.append("Headers   : ").append(getRequestHeaders(request)).append("\n");

        // Log parameters (careful in production with passwords!)
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            sb.append("Parameters: ").append(getRequestParameters(request)).append("\n");
        }
        sb.append("======================================\n");

        logger.info(sb.toString());
    }

    private void logResponse(HttpServletRequest request, HttpServletResponse response, long timeTaken) {
        logger.info("============ RESPONSE ============");
        logger.info("URL       : {} {}", request.getMethod(), request.getRequestURI());
        logger.info("Status    : {} {}", response.getStatus(),
                getStatusMessage(response.getStatus()));
        logger.info("Time      : {} ms", timeTaken);
        logger.info("User      : {}", getUsername(request));
        logger.info("======================================");
    }

    // Helper methods
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip != null ? ip.split(",")[0].trim() : "unknown";
    }

    private String getUsername(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object user = session.getAttribute("user");
            if (user != null) {
                return user.toString();
            }
            Object username = session.getAttribute("username");
            if (username != null)
                return username.toString();
        }
        return "anonymous";
    }

    private String getRequestHeaders(HttpServletRequest request) {
        StringBuilder headers = new StringBuilder();
        request.getHeaderNames().asIterator()
                .forEachRemaining(name -> headers.append(name).append("=").append(request.getHeader(name)).append(" "));
        return headers.toString().trim();
    }

    private String getRequestParameters(HttpServletRequest request) {
        return request.getParameterMap()
                .entrySet()
                .stream()
                .map(e -> e.getKey() + "=" + String.join(",", e.getValue()))
                .reduce((a, b) -> a + "&" + b)
                .orElse("");
    }

    private String getStatusMessage(int status) {
        return switch (status) {
            case 200 -> "OK";
            case 201 -> "Created";
            case 400 -> "Bad Request";
            case 401 -> "Unauthorized";
            case 403 -> "Forbidden";
            case 404 -> "Not Found";
            case 500 -> "Server Error";
            default -> "Other";
        };
    }

}
