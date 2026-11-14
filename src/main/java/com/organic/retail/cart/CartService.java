package com.organic.retail.cart;

import com.organic.retail.catalog.ProductCatalog;
import com.organic.retail.model.CartItem;
import com.organic.retail.model.CartSummary;
import com.organic.retail.model.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Session shopping cart. Shipping is free once the sale subtotal reaches
 * {@link #FREE_SHIPPING_THRESHOLD}; otherwise a flat fee applies. Tax is
 * {@link #TAX_RATE} of merchandise plus shipping.
 */
public final class CartService {

    public static final BigDecimal FREE_SHIPPING_THRESHOLD = new BigDecimal("35.00");
    public static final BigDecimal SHIPPING_FEE = new BigDecimal("5.99");
    public static final BigDecimal TAX_RATE = new BigDecimal("0.08");

    private final ProductCatalog catalog;
    private final Map<String, CartItem> items = new LinkedHashMap<>();

    public CartService(ProductCatalog catalog) {
        if (catalog == null) {
            throw new IllegalArgumentException("catalog is required");
        }
        this.catalog = catalog;
    }

    public List<CartItem> items() {
        return List.copyOf(items.values());
    }

    public int itemCount() {
        return items.values().stream().mapToInt(CartItem::quantity).sum();
    }

    public void add(String productId, int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("quantity must be at least 1");
        }
        Product product = requireProduct(productId);
        CartItem existing = items.get(product.id());
        int next = quantity + (existing == null ? 0 : existing.quantity());
        ensureStock(product, next);
        if (existing == null) {
            items.put(product.id(), new CartItem(product, quantity));
        } else {
            existing.setQuantity(next);
        }
    }

    public void update(String productId, int quantity) {
        Product product = requireProduct(productId);
        if (!items.containsKey(product.id())) {
            throw new IllegalArgumentException("product is not in the cart: " + product.id());
        }
        if (quantity < 1) {
            items.remove(product.id());
            return;
        }
        ensureStock(product, quantity);
        items.get(product.id()).setQuantity(quantity);
    }

    public void remove(String productId) {
        Product product = requireProduct(productId);
        items.remove(product.id());
    }

    public void clear() {
        items.clear();
    }

    public CartSummary summary() {
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal savings = BigDecimal.ZERO;
        int count = 0;
        for (CartItem item : items.values()) {
            subtotal = subtotal.add(item.lineTotal());
            savings = savings.add(item.lineSavings());
            count += item.quantity();
        }
        subtotal = money(subtotal);
        savings = money(savings);
        BigDecimal shipping = subtotal.signum() == 0 || subtotal.compareTo(FREE_SHIPPING_THRESHOLD) >= 0
                ? money(BigDecimal.ZERO)
                : SHIPPING_FEE;
        BigDecimal tax = money(subtotal.add(shipping).multiply(TAX_RATE));
        BigDecimal total = money(subtotal.add(shipping).add(tax));
        return new CartSummary(count, subtotal, savings, shipping, tax, total);
    }

    private Product requireProduct(String productId) {
        return catalog.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("unknown product: " + productId));
    }

    private static void ensureStock(Product product, int quantity) {
        if (quantity > product.stock()) {
            throw new IllegalArgumentException(
                    "only " + product.stock() + " of " + product.name() + " are in stock");
        }
    }

    private static BigDecimal money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
