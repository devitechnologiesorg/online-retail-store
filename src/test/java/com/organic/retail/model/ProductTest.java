package com.organic.retail.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductTest {

    @Test
    void roundsMoneyAndComputesSavings() {
        Product product = new Product(
                " wheat-bread ",
                " Whole Wheat Sandwich Bread ",
                " breads-sweets ",
                new BigDecimal("24"),
                new BigDecimal("18.004"),
                " images/product-thumb-1.png ",
                40);

        assertEquals("wheat-bread", product.id());
        assertEquals("Whole Wheat Sandwich Bread", product.name());
        assertEquals(new BigDecimal("24.00"), product.listPrice());
        assertEquals(new BigDecimal("18.00"), product.salePrice());
        assertEquals(new BigDecimal("6.00"), product.savings());
    }

    @Test
    void rejectsSalePriceAboveListPrice() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () ->
                new Product("milk", "Milk", "dairy", new BigDecimal("4.00"), new BigDecimal("5.00"), "images/milk.png", 1));

        assertEquals("salePrice cannot exceed listPrice", error.getMessage());
    }

    @Test
    void rejectsMissingNameAndNegativeStock() {
        assertThrows(IllegalArgumentException.class, () ->
                new Product("id", "  ", "dairy", BigDecimal.ONE, BigDecimal.ONE, "images/x.png", 1));
        assertThrows(IllegalArgumentException.class, () ->
                new Product("id", "Milk", "dairy", BigDecimal.ONE, BigDecimal.ONE, "images/x.png", -1));
    }
}
