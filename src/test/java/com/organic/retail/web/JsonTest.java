package com.organic.retail.web;

import com.organic.retail.cart.CartService;
import com.organic.retail.catalog.ProductCatalog;
import com.organic.retail.model.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonTest {

    @Test
    void escapesQuotesAndWritesAProduct() {
        assertEquals("a\\\"b\\\\c", Json.escape("a\"b\\c"));

        Product product = new Product(
                "milk", "Organic \"2%\" Milk", "dairy",
                new BigDecimal("5.00"), new BigDecimal("4.50"),
                "images/product-thumb-9.png", 10);

        String json = Json.products(List.of(product));
        assertTrue(json.contains("\"id\":\"milk\""));
        assertTrue(json.contains("Organic \\\"2%\\\" Milk"));
        assertTrue(json.contains("\"salePrice\":\"4.50\""));
        assertTrue(json.contains("\"stock\":10"));
    }

    @Test
    void writesCartTotals() {
        CartService cart = new CartService(new ProductCatalog());
        cart.add("wheat-bread", 1);

        String json = Json.cart(cart.items(), cart.summary());
        assertTrue(json.contains("\"productId\":\"wheat-bread\""));
        assertTrue(json.contains("\"quantity\":1"));
        assertTrue(json.contains("\"shipping\":\"5.99\""));
        assertTrue(json.contains("\"total\":\"25.91\""));
    }

    @Test
    void writesErrors() {
        assertEquals("{\"error\":\"unknown product: nope\"}", Json.error("unknown product: nope"));
    }
}
