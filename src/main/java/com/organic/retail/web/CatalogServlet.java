package com.organic.retail.web;

import com.organic.retail.catalog.ProductCatalog;
import com.organic.retail.model.Product;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * GET /api/products
 * Optional query parameters: id, category, q.
 */
public class CatalogServlet extends HttpServlet {

    private final ProductCatalog catalog = new ProductCatalog();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter("id");
        if (id != null && !id.isBlank()) {
            Optional<Product> product = catalog.findById(id);
            if (product.isEmpty()) {
                write(response, HttpServletResponse.SC_NOT_FOUND, Json.error("unknown product: " + id.trim()));
                return;
            }
            write(response, HttpServletResponse.SC_OK, Json.products(List.of(product.get())));
            return;
        }

        String query = request.getParameter("q");
        List<Product> products = (query != null && !query.isBlank())
                ? catalog.search(query)
                : catalog.findByCategory(request.getParameter("category"));
        write(response, HttpServletResponse.SC_OK, Json.products(products));
    }

    private static void write(HttpServletResponse response, int status, String body) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(body);
    }
}
