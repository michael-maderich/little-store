package com.littlestore.entity;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies that CartDetail.compareTo() and OrderDetail.compareTo() are
 * null-safe for Product.size and Product.options (both nullable in DB).
 *
 * These are pure unit tests — no Spring context or DB required.
 */
class ComparatorNullSafetyTest {

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private Product makeProduct(String cat, String subcat, String name,
                                String size, String options) {
        Product p = new Product();
        p.setCategoryMain(cat);
        p.setCategorySpecific(subcat);
        p.setName(name);
        p.setSize(size);
        p.setOptions(options);
        return p;
    }

    private CartDetail makeCartDetail(Product p) {
        CartDetail cd = new CartDetail();
        cd.setProduct(p);
        cd.setCart(new Cart());
        cd.setQty(1);
        cd.setRetailPrice(0f);
        cd.setBasePrice(1f);
        cd.setPrice(1f);
        cd.setLineNumber(1);
        return cd;
    }

    private OrderDetail makeOrderDetail(Product p) {
        OrderDetail od = new OrderDetail();
        od.setProduct(p);
        od.setOrder(new Order());
        od.setQty(1);
        od.setRetailPrice(0f);
        od.setBasePrice(1f);
        od.setPrice(1f);
        od.setLineNumber(1);
        od.setDescription("");
        od.setImage("");
        return od;
    }

    // -----------------------------------------------------------------------
    // CartDetail tests
    // -----------------------------------------------------------------------

    @Test
    void cartDetail_compareTo_doesNotThrowWhenBothSizeAndOptionsAreNull() {
        Product p1 = makeProduct("Laundry", "Dryer Sheets", "Bounce", null, null);
        Product p2 = makeProduct("Laundry", "Dryer Sheets", "Bounce", null, null);
        CartDetail cd1 = makeCartDetail(p1);
        CartDetail cd2 = makeCartDetail(p2);
        assertDoesNotThrow(() -> cd1.compareTo(cd2));
        assertEquals(0, cd1.compareTo(cd2), "Identical null-field items should compare as equal");
    }

    @Test
    void cartDetail_compareTo_doesNotThrowWhenOneSizeIsNull() {
        Product p1 = makeProduct("Laundry", "Dryer Sheets", "Bounce", null, "Fresh Scent");
        Product p2 = makeProduct("Laundry", "Dryer Sheets", "Bounce", "60 ct", "Fresh Scent");
        CartDetail cd1 = makeCartDetail(p1);
        CartDetail cd2 = makeCartDetail(p2);
        assertDoesNotThrow(() -> cd1.compareTo(cd2));
        // null treated as "" → "" < "60 ct" → cd1 sorts before cd2
        assertTrue(cd1.compareTo(cd2) < 0, "null size should sort before non-null size");
    }

    @Test
    void cartDetail_compareTo_doesNotThrowWhenOneOptionsIsNull() {
        Product p1 = makeProduct("Cleaning Supplies", "Dish Soap", "Dawn", "20 oz", null);
        Product p2 = makeProduct("Cleaning Supplies", "Dish Soap", "Dawn", "20 oz", "Original");
        CartDetail cd1 = makeCartDetail(p1);
        CartDetail cd2 = makeCartDetail(p2);
        assertDoesNotThrow(() -> cd1.compareTo(cd2));
        // null treated as "" → "" < "Original"
        assertTrue(cd1.compareTo(cd2) < 0, "null options should sort before non-null options");
    }

    @Test
    void cartDetail_sortList_doesNotThrowWithMixedNullFields() {
        List<CartDetail> items = new ArrayList<>();
        items.add(makeCartDetail(makeProduct("Personal Care", "Shampoo", "Pantene", "12 oz", null)));
        items.add(makeCartDetail(makeProduct("Laundry",        "Pods",    "Tide",    null,    null)));
        items.add(makeCartDetail(makeProduct("Cleaning Supplies", "Dish Soap", "Dawn", null, "Original")));
        items.add(makeCartDetail(makeProduct("Laundry",        "Pods",    "Tide",    null,    "Original")));
        assertDoesNotThrow(() -> Collections.sort(items),
            "Collections.sort should not throw NPE when size/options are null");
        assertEquals(4, items.size());
    }

    // -----------------------------------------------------------------------
    // OrderDetail tests
    // -----------------------------------------------------------------------

    @Test
    void orderDetail_compareTo_doesNotThrowWhenBothSizeAndOptionsAreNull() {
        Product p1 = makeProduct("Pantry", "Snacks", "Chips Ahoy", null, null);
        Product p2 = makeProduct("Pantry", "Snacks", "Chips Ahoy", null, null);
        OrderDetail od1 = makeOrderDetail(p1);
        OrderDetail od2 = makeOrderDetail(p2);
        assertDoesNotThrow(() -> od1.compareTo(od2));
        assertEquals(0, od1.compareTo(od2));
    }

    @Test
    void orderDetail_compareTo_doesNotThrowWhenOneSizeIsNull() {
        Product p1 = makeProduct("Pantry", "Snacks", "Chips Ahoy", null, "Chocolate Chip");
        Product p2 = makeProduct("Pantry", "Snacks", "Chips Ahoy", "13 oz", "Chocolate Chip");
        OrderDetail od1 = makeOrderDetail(p1);
        OrderDetail od2 = makeOrderDetail(p2);
        assertDoesNotThrow(() -> od1.compareTo(od2));
        assertTrue(od1.compareTo(od2) < 0, "null size should sort before non-null size");
    }

    @Test
    void orderDetail_sortList_doesNotThrowWithMixedNullFields() {
        List<OrderDetail> items = new ArrayList<>();
        items.add(makeOrderDetail(makeProduct("Oral Care", "Toothpaste", "Colgate", null, null)));
        items.add(makeOrderDetail(makeProduct("Oral Care", "Toothpaste", "Colgate", "6 oz", null)));
        items.add(makeOrderDetail(makeProduct("Oral Care", "Toothbrush", "Oral-B", null, "Soft")));
        items.add(makeOrderDetail(makeProduct("Baby Care", "Diapers", "Huggies", "Size 2", null)));
        assertDoesNotThrow(() -> Collections.sort(items),
            "Collections.sort should not throw NPE when size/options are null");
        assertEquals(4, items.size());
    }
}
