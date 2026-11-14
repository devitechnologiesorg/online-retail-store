package com.organic.retail.model;

import java.math.BigDecimal;

/**
 * Totals for a cart: merchandise, savings, shipping, tax, and amount due.
 */
public record CartSummary(
        int itemCount,
        BigDecimal subtotal,
        BigDecimal savings,
        BigDecimal shipping,
        BigDecimal tax,
        BigDecimal total) {
}
