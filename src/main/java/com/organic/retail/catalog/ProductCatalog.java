package com.organic.retail.catalog;

import com.organic.retail.model.Product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * In-memory grocery catalog used by the storefront API.
 */
public final class ProductCatalog {

    private final List<Product> products;

    public ProductCatalog() {
        this(defaultProducts());
    }

    public ProductCatalog(List<Product> products) {
        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException("catalog must contain at least one product");
        }
        this.products = List.copyOf(products);
    }

    public List<Product> all() {
        return products;
    }

    public Optional<Product> findById(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        String key = id.trim();
        return products.stream().filter(product -> product.id().equals(key)).findFirst();
    }

    public List<Product> findByCategory(String category) {
        if (category == null || category.isBlank()) {
            return all();
        }
        String key = category.trim().toLowerCase(Locale.ROOT);
        return products.stream()
                .filter(product -> product.category().equalsIgnoreCase(key))
                .toList();
    }

    public List<Product> search(String query) {
        if (query == null || query.isBlank()) {
            return all();
        }
        String key = query.trim().toLowerCase(Locale.ROOT);
        return products.stream()
                .filter(product -> product.name().toLowerCase(Locale.ROOT).contains(key)
                        || product.category().toLowerCase(Locale.ROOT).contains(key))
                .toList();
    }

    public List<String> categories() {
        return products.stream()
                .map(Product::category)
                .distinct()
                .sorted(Comparator.naturalOrder())
                .toList();
    }

    private static List<Product> defaultProducts() {
        List<Product> items = new ArrayList<>();
        items.add(item("wheat-bread", "Whole Wheat Sandwich Bread", "breads-sweets", "24.00", "18.00", "images/product-thumb-1.png", 40));
        items.add(item("oatmeal", "Whole Grain Oatmeal", "breads-sweets", "54.00", "50.00", "images/product-thumb-2.png", 30));
        items.add(item("cheddar", "Sharp Cheddar Cheese Block", "dairy", "14.00", "12.00", "images/product-thumb-3.png", 25));
        items.add(item("baby-spinach", "Organic Baby Spinach", "fruits-veges", "24.00", "18.00", "images/product-thumb-4.png", 50));
        items.add(item("spinach-leaves", "Organic Spinach Leaves", "fruits-veges", "24.00", "18.00", "images/product-thumb-5.png", 35));
        items.add(item("salmon", "Fresh Salmon", "meat", "28.00", "24.00", "images/product-thumb-6.png", 15));
        items.add(item("spaghetti", "Imported Italian Spaghetti Pasta", "pantry", "8.00", "6.00", "images/product-thumb-7.png", 60));
        items.add(item("granny-smith", "Granny Smith Apples", "fruits-veges", "6.00", "5.00", "images/product-thumb-8.png", 80));
        items.add(item("milk", "Organic 2% Reduced Fat Milk", "dairy", "5.00", "4.50", "images/product-thumb-9.png", 45));
        items.add(item("yogurt", "Greek Style Plain Yogurt", "dairy", "7.00", "6.00", "images/product-thumb-10.png", 40));
        items.add(item("orange-juice", "Pure Squeezed No Pulp Orange Juice", "beverages", "8.00", "7.00", "images/product-thumb-11.png", 36));
        items.add(item("oranges", "Fresh Oranges", "fruits-veges", "6.00", "5.00", "images/product-thumb-12.png", 70));
        items.add(item("chocolate", "Gourmet Dark Chocolate Bars", "breads-sweets", "12.00", "10.00", "images/product-thumb-13.png", 28));
        items.add(item("celery", "Fresh Green Celery", "fruits-veges", "4.00", "3.00", "images/product-thumb-14.png", 22));
        items.add(item("sandwich-bread", "Sandwich Bread", "breads-sweets", "5.00", "4.00", "images/product-thumb-15.png", 33));
        items.add(item("honeycrisp", "Honeycrisp Apples", "fruits-veges", "9.00", "7.00", "images/product-thumb-16.png", 48));
        items.add(item("melon-juice", "Sunstar Fresh Melon Juice", "beverages", "10.00", "8.00", "images/product-thumb-17.png", 20));
        items.add(item("growers-cider", "Growers Cider", "beverages", "14.00", "12.00", "images/product-thumb-18.png", 18));
        items.add(item("grapes", "Fresh Grapes", "fruits-veges", "10.00", "8.00", "images/product-thumb-19.png", 42));
        items.add(item("ketchup", "Heinz Tomato Ketchup", "pantry", "6.00", "5.00", "images/product-thumb-20.png", 55));
        return items;
    }

    private static Product item(String id, String name, String category, String list, String sale, String image, int stock) {
        return new Product(id, name, category, new BigDecimal(list), new BigDecimal(sale), image, stock);
    }
}
