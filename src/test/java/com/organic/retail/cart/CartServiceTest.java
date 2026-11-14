package com.organic.retail.cart;

import com.organic.retail.catalog.ProductCatalog;
import com.organic.retail.model.CartSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CartServiceTest {

    private CartService cart;

    @BeforeEach
    void setUp() {
        cart = new CartService(new ProductCatalog());
    }

    @Test
    void addsItemsAndMergesQuantities() {
        cart.add("wheat-bread", 1);
        cart.add("wheat-bread", 2);
        cart.add("ketchup", 1);

        assertEquals(2, cart.items().size());
        assertEquals(4, cart.itemCount());
        assertEquals(3, cart.items().get(0).quantity());
        assertEquals(new BigDecimal("54.00"), cart.items().get(0).lineTotal());
    }

    @Test
    void chargesShippingAndTaxBelowTheFreeShippingThreshold() {
        cart.add("wheat-bread", 1);

        CartSummary summary = cart.summary();
        assertEquals(1, summary.itemCount());
        assertEquals(new BigDecimal("18.00"), summary.subtotal());
        assertEquals(new BigDecimal("6.00"), summary.savings());
        assertEquals(new BigDecimal("5.99"), summary.shipping());
        assertEquals(new BigDecimal("1.92"), summary.tax());
        assertEquals(new BigDecimal("25.91"), summary.total());
    }

    @Test
    void shipsFreeOnceTheSubtotalReachesTheThreshold() {
        cart.add("oatmeal", 1);

        CartSummary summary = cart.summary();
        assertEquals(new BigDecimal("50.00"), summary.subtotal());
        assertEquals(new BigDecimal("0.00"), summary.shipping());
        assertEquals(new BigDecimal("4.00"), summary.tax());
        assertEquals(new BigDecimal("54.00"), summary.total());
    }

    @Test
    void updatesRemovesAndClearsLines() {
        cart.add("milk", 2);
        cart.add("yogurt", 1);

        cart.update("milk", 1);
        assertEquals(1, cart.items().get(0).quantity());

        cart.update("yogurt", 0);
        assertEquals(1, cart.items().size());

        cart.remove("milk");
        assertTrue(cart.items().isEmpty());
        assertEquals(new BigDecimal("0.00"), cart.summary().total());

        cart.add("grapes", 1);
        cart.clear();
        assertEquals(0, cart.itemCount());
    }

    @Test
    void rejectsUnknownProductsInvalidQuantitiesAndOversell() {
        assertThrows(IllegalArgumentException.class, () -> cart.add("does-not-exist", 1));
        assertThrows(IllegalArgumentException.class, () -> cart.add("milk", 0));
        assertThrows(IllegalArgumentException.class, () -> cart.add("salmon", 16));
        assertThrows(IllegalArgumentException.class, () -> cart.update("milk", 1));

        cart.add("salmon", 15);
        assertThrows(IllegalArgumentException.class, () -> cart.add("salmon", 1));
        assertEquals(15, cart.items().get(0).quantity());
    }
}
