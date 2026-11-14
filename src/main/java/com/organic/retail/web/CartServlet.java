package com.organic.retail.web;

import com.organic.retail.cart.CartService;
import com.organic.retail.catalog.ProductCatalog;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Session cart at /api/cart.
 * GET returns the cart. POST accepts action=add|update|remove|clear.
 */
public class CartServlet extends HttpServlet {

    static final String SESSION_KEY = "cart";

    private final ProductCatalog catalog = new ProductCatalog();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        writeCart(response, cart(request));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CartService cart = cart(request);
        String action = request.getParameter("action");
        if (action == null || action.isBlank()) {
            action = "add";
        }
        try {
            switch (action.trim().toLowerCase()) {
                case "add" -> cart.add(request.getParameter("productId"), quantity(request, 1));
                case "update" -> cart.update(request.getParameter("productId"), quantity(request, 0));
                case "remove" -> cart.remove(request.getParameter("productId"));
                case "clear" -> cart.clear();
                default -> throw new IllegalArgumentException("unknown action: " + action);
            }
            writeCart(response, cart);
        } catch (IllegalArgumentException ex) {
            write(response, HttpServletResponse.SC_BAD_REQUEST, Json.error(ex.getMessage()));
        }
    }

    private CartService cart(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        Object current = session.getAttribute(SESSION_KEY);
        if (current instanceof CartService existing) {
            return existing;
        }
        CartService created = new CartService(catalog);
        session.setAttribute(SESSION_KEY, created);
        return created;
    }

    private static int quantity(HttpServletRequest request, int fallback) {
        String raw = request.getParameter("quantity");
        if (raw == null || raw.isBlank()) {
            return fallback;
        }
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("quantity must be a whole number");
        }
    }

    private static void writeCart(HttpServletResponse response, CartService cart) throws IOException {
        write(response, HttpServletResponse.SC_OK, Json.cart(cart.items(), cart.summary()));
    }

    private static void write(HttpServletResponse response, int status, String body) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(body);
    }
}
