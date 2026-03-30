package com.littlestore.entity;

public enum OrderStatus {
    PROCESSING,
    SHIPPED,
    DELIVERED,
    PICKED_UP,
    CANCELLED,
    RETURNED;

    /** Human-readable label for display in UI dropdowns and tables. */
    public String getLabel() {
        switch (this) {
            case PROCESSING: return "Processing";
            case SHIPPED:    return "Shipped";
            case DELIVERED:  return "Delivered";
            case PICKED_UP:  return "Picked Up";
            case CANCELLED:  return "Cancelled";
            case RETURNED:   return "Returned";
            default:         return name();
        }
    }
}
