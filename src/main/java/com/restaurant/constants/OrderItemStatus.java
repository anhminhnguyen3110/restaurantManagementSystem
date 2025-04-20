package com.restaurant.constants;

public enum OrderItemStatus {
    PENDING("Pending"),
    PREPARING("Preparing"),
    READY("Ready"),
    SERVED("Served"),
    CANCELLED("Cancelled");

    private final String status;

    OrderItemStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return status;
    }

    public static OrderItemStatus fromString(String status) {
        for (OrderItemStatus orderItemStatus : OrderItemStatus.values()) {
            if (orderItemStatus.status.equalsIgnoreCase(status)) {
                return orderItemStatus;
            }
        }
        throw new IllegalArgumentException("No constant with text " + status + " found");
    }
}
