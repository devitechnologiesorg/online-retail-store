package com.organic.retail.catalog;

import com.organic.retail.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductCatalogTest {

    private ProductCatalog catalog;

    @BeforeEach
    void setUp() {
        catalog = new ProductCatalog();
    }

    @Test
    void loadsTheStorefrontCatalog() {
        assertTrue(catalog.all().size() >= 12);
        Product bread = catalog.findById("wheat-bread").orElseThrow();
        assertEquals("Whole Wheat Sandwich Bread", bread.name());
        assertEquals(new BigDecimal("18.00"), bread.salePrice());
        assertEquals("breads-sweets", bread.category());
    }

    @Test
    void filtersByCategoryAndSearch() {
        List<Product> produce = catalog.findByCategory("FRUITS-VEGES");
        assertTrue(produce.stream().allMatch(product -> product.category().equals("fruits-veges")));
        assertTrue(produce.stream().anyMatch(product -> product.id().equals("honeycrisp")));

        List<Product> apples = catalog.search("apple");
        assertEquals(List.of("granny-smith", "honeycrisp"), apples.stream().map(Product::id).toList());
    }

    @Test
    void blankFiltersReturnTheFullCatalog() {
        assertEquals(catalog.all(), catalog.findByCategory(" "));
        assertEquals(catalog.all(), catalog.search(null));
        assertTrue(catalog.findById("missing").isEmpty());
        assertTrue(catalog.findById("  ").isEmpty());
    }

    @Test
    void listsCategoriesInOrder() {
        assertEquals(
                List.of("beverages", "breads-sweets", "dairy", "fruits-veges", "meat", "pantry"),
                catalog.categories());
    }

    @Test
    void rejectsAnEmptyCatalog() {
        assertThrows(IllegalArgumentException.class, () -> new ProductCatalog(List.of()));
    }
}
