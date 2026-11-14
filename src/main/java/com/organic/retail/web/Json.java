package com.organic.retail.web;

import com.organic.retail.model.CartItem;
import com.organic.retail.model.CartSummary;
import com.organic.retail.model.Product;

import java.util.List;

/**
 * Tiny JSON writer for the catalog and cart responses. No extra libraries.
 */
public final class Json {

    private Json() {
    }

    public static String products(List<Product> products) {
        StringBuilder json = new StringBuilder();
        json.append("{\"products\":[");
        for (int i = 0; i < products.size(); i++) {
            if (i > 0) {
                json.append(',');
            }
            json.append(product(products.get(i)));
        }
        json.append("]}");
        return json.toString();
    }

    public static String cart(List<CartItem> items, CartSummary summary) {
        StringBuilder json = new StringBuilder();
        json.append("{\"items\":[");
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                json.append(',');
            }
            CartItem item = items.get(i);
            json.append('{');
            field(json, "productId", item.product().id());
            json.append(',');
            field(json, "name", item.product().name());
            json.append(',');
            field(json, "quantity", item.quantity());
            json.append(',');
            field(json, "unitPrice", item.product().salePrice().toPlainString());
            json.append(',');
            field(json, "lineTotal", item.lineTotal().toPlainString());
            json.append('}');
        }
        json.append(",\"summary\":{");
        json.append("\"itemCount\":").append(summary.itemCount());
        json.append(',');
        field(json, "subtotal", summary.subtotal().toPlainString());
        json.append(',');
        field(json, "savings", summary.savings().toPlainString());
        json.append(',');
        field(json, "shipping", summary.shipping().toPlainString());
        json.append(',');
        field(json, "tax", summary.tax().toPlainString());
        json.append(',');
        field(json, "total", summary.total().toPlainString());
        json.append("}}");
        return json.toString();
    }

    public static String error(String message) {
        return "{\"error\":\"" + escape(message) + "\"}";
    }

    public static String escape(String value) {
        if (value == null) {
            return "";
        }
        StringBuilder escaped = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            switch (ch) {
                case '"' -> escaped.append("\\\"");
                case '\\' -> escaped.append("\\\\");
                case '\n' -> escaped.append("\\n");
                case '\r' -> escaped.append("\\r");
                case '\t' -> escaped.append("\\t");
                default -> escaped.append(ch);
            }
        }
        return escaped.toString();
    }

    private static String product(Product product) {
        StringBuilder json = new StringBuilder();
        json.append('{');
        field(json, "id", product.id());
        json.append(',');
        field(json, "name", product.name());
        json.append(',');
        field(json, "category", product.category());
        json.append(',');
        field(json, "listPrice", product.listPrice().toPlainString());
        json.append(',');
        field(json, "salePrice", product.salePrice().toPlainString());
        json.append(',');
        field(json, "image", product.image());
        json.append(',');
        json.append("\"stock\":").append(product.stock());
        json.append('}');
        return json.toString();
    }

    private static void field(StringBuilder json, String name, String value) {
        json.append('"').append(name).append("\":\"").append(escape(value)).append('"');
    }

    private static void field(StringBuilder json, String name, int value) {
        json.append('"').append(name).append("\":").append(value);
    }
}
