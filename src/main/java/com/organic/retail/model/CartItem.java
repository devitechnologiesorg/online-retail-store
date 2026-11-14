package com.organic.retail.model;

import java.math.BigDecimal;

/**
 * One line in a shopping cart.
 */
public final class CartItem {

    private final Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("product is required");
        }
        if (quantity < 1) {
            throw new IllegalArgumentException("quantity must be at least 1");
        }
        this.product = product;
        this.quantity = quantity;
    }

    public Product product() {
        return product;
    }

    public int quantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("quantity must be at least 1");
        }
        this.quantity = quantity;
    }

    public BigDecimal lineTotal() {
        return product.salePrice().multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal lineSavings() {
        return product.savings().multiply(BigDecimal.valueOf(quantity));
    }
}
