package com.organic.retail.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * A grocery item sold by the store. Prices are in USD.
 */
public record Product(
        String id,
        String name,
        String category,
        BigDecimal listPrice,
        BigDecimal salePrice,
        String image,
        int stock) {

    public Product {
        id = requireText(id, "id");
        name = requireText(name, "name");
        category = requireText(category, "category");
        image = requireText(image, "image");
        listPrice = money(listPrice, "listPrice");
        salePrice = money(salePrice, "salePrice");
        if (salePrice.compareTo(listPrice) > 0) {
            throw new IllegalArgumentException("salePrice cannot exceed listPrice");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("stock cannot be negative");
        }
    }

    public BigDecimal savings() {
        return listPrice.subtract(salePrice);
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return value.trim();
    }

    private static BigDecimal money(BigDecimal value, String field) {
        Objects.requireNonNull(value, field + " is required");
        if (value.signum() < 0) {
            throw new IllegalArgumentException(field + " cannot be negative");
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
