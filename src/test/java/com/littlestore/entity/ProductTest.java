package com.littlestore.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies fixes to Product.equals() and Product.hashCode():
 *  1. hashCode() must not throw NPE when nullable Float/Integer fields are null.
 *  2. equals() must compare the correct field pairs (retailPrice↔retailPrice,
 *     basePrice↔basePrice, currentPrice↔currentPrice — not all against currentPrice).
 *
 * Pure unit tests — no Spring context or DB required.
 */
class ProductTest {

    // -----------------------------------------------------------------------
    // Helper: minimal valid product
    // -----------------------------------------------------------------------

    private Product baseProduct() {
        Product p = new Product();
        p.setUpc("012345678901");
        p.setCategoryMain("Laundry");
        p.setCategorySpecific("Dryer Sheets");
        p.setName("Bounce");
        p.setBasePrice(2.99f);
        p.setCurrentPrice(2.49f);
        p.setOnSale(true);
        p.setStockQty(50);
        p.setTransparent(false);
        p.setDateAdded(LocalDateTime.of(2024, 1, 1, 0, 0));
        // nullable fields left null: cost, retailPrice, purchaseLimit, size, options, description, image
        return p;
    }

    // -----------------------------------------------------------------------
    // hashCode() null-safety tests
    // -----------------------------------------------------------------------

    @Test
    void hashCode_doesNotThrowWhenCostIsNull() {
        Product p = baseProduct();
        p.setCost(null);
        assertDoesNotThrow(p::hashCode, "hashCode() must not NPE when cost is null");
    }

    @Test
    void hashCode_doesNotThrowWhenRetailPriceIsNull() {
        Product p = baseProduct();
        p.setRetailPrice(null);
        assertDoesNotThrow(p::hashCode, "hashCode() must not NPE when retailPrice is null");
    }

    @Test
    void hashCode_doesNotThrowWhenPurchaseLimitIsNull() {
        Product p = baseProduct();
        p.setPurchaseLimit(null);
        assertDoesNotThrow(p::hashCode, "hashCode() must not NPE when purchaseLimit is null");
    }

    @Test
    void hashCode_doesNotThrowWhenAllNullableFieldsAreNull() {
        Product p = baseProduct();
        // all nullable scalar fields null
        p.setCost(null);
        p.setRetailPrice(null);
        p.setPurchaseLimit(null);
        p.setSize(null);
        p.setOptions(null);
        p.setDescription(null);
        p.setImage(null);
        assertDoesNotThrow(p::hashCode,
            "hashCode() must not NPE when all nullable fields are null");
    }

    @Test
    void hashCode_isStableAcrossTwoCalls() {
        Product p = baseProduct();
        assertEquals(p.hashCode(), p.hashCode(),
            "hashCode() must return the same value on repeated calls");
    }

    // -----------------------------------------------------------------------
    // equals() correctness tests
    // -----------------------------------------------------------------------

    @Test
    void equals_returnsTrueForIdenticalProducts() {
        Product p1 = baseProduct();
        Product p2 = baseProduct();
        assertEquals(p1, p2, "Two products with identical fields should be equal");
    }

    @Test
    void equals_returnsFalseWhenRetailPriceDiffers() {
        Product p1 = baseProduct();
        Product p2 = baseProduct();
        p1.setRetailPrice(3.99f);
        p2.setRetailPrice(4.99f);   // different retailPrice
        assertNotEquals(p1, p2,
            "Products with different retailPrice should not be equal");
    }

    @Test
    void equals_returnsFalseWhenBasePriceDiffers() {
        Product p1 = baseProduct();
        Product p2 = baseProduct();
        p1.setBasePrice(2.99f);
        p2.setBasePrice(3.99f);    // different basePrice
        assertNotEquals(p1, p2,
            "Products with different basePrice should not be equal");
    }

    @Test
    void equals_returnsFalseWhenCurrentPriceDiffers() {
        Product p1 = baseProduct();
        Product p2 = baseProduct();
        p1.setCurrentPrice(2.49f);
        p2.setCurrentPrice(1.99f); // different currentPrice
        assertNotEquals(p1, p2,
            "Products with different currentPrice should not be equal");
    }

    /**
     * The original bug: all three price checks compared 'this.currentPrice' against
     * the other's field, so this scenario would have falsely returned true before the fix.
     */
    @Test
    void equals_returnsFalseWhenRetailPriceDiffersButCurrentPriceMatches() {
        Product p1 = baseProduct();
        Product p2 = baseProduct();
        p1.setRetailPrice(5.00f);
        p2.setRetailPrice(6.00f);        // different retailPrice
        p1.setCurrentPrice(2.49f);       // same currentPrice
        p2.setCurrentPrice(2.49f);
        assertNotEquals(p1, p2,
            "equals() must detect different retailPrice even when currentPrice is the same " +
            "(regression: original code compared currentPrice against other.retailPrice)");
    }

    @Test
    void equals_returnsFalseWhenBasePriceDiffersButCurrentPriceMatches() {
        Product p1 = baseProduct();
        Product p2 = baseProduct();
        p1.setBasePrice(2.00f);
        p2.setBasePrice(3.00f);          // different basePrice
        p1.setCurrentPrice(1.99f);       // same currentPrice
        p2.setCurrentPrice(1.99f);
        assertNotEquals(p1, p2,
            "equals() must detect different basePrice even when currentPrice is the same " +
            "(regression: original code compared currentPrice against other.basePrice)");
    }

    @Test
    void equals_returnsTrueWithAllNullableFieldsNull() {
        Product p1 = baseProduct();
        Product p2 = baseProduct();
        // set all nullable fields to null in both
        p1.setCost(null);       p2.setCost(null);
        p1.setRetailPrice(null); p2.setRetailPrice(null);
        p1.setPurchaseLimit(null); p2.setPurchaseLimit(null);
        assertEquals(p1, p2,
            "Two products equal when all nullable fields are null in both");
    }

    @Test
    void equals_returnsFalseWhenOneCostIsNullAndOtherIsNot() {
        Product p1 = baseProduct();
        Product p2 = baseProduct();
        p1.setCost(null);
        p2.setCost(1.50f);
        assertNotEquals(p1, p2,
            "Products should not be equal when one has null cost and the other does not");
    }

    @Test
    void equals_returnsFalseWhenPurchaseLimitDiffers() {
        Product p1 = baseProduct();
        Product p2 = baseProduct();
        p1.setPurchaseLimit(5);
        p2.setPurchaseLimit(10);
        assertNotEquals(p1, p2,
            "Products with different purchaseLimit should not be equal");
    }

    @Test
    void hashCode_equalsContractHolds() {
        Product p1 = baseProduct();
        Product p2 = baseProduct();
        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode(),
            "Equal objects must have equal hash codes");
    }
}
