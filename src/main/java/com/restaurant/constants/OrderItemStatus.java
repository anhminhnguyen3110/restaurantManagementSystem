package com.restaurant.constants;

public enum OrderItemStatus {
    PENDING("pending"),
    READY("ready"),
    SERVED("served"),
    CANCELLED("cancelled");

    private final String status;

    OrderItemStatus(String status) {
        this.status = status;
    }

    public static OrderItemStatus fromString(String status) {
        for (OrderItemStatus orderItemStatus : OrderItemStatus.values()) {
            if (orderItemStatus.status.equalsIgnoreCase(status)) {
                return orderItemStatus;
            }
        }
        throw new IllegalArgumentException("No constant with text " + status + " found");
    }

    public String getStatus() {
        return status.toLowerCase().replace("_", " ");
    }

    @Override
    public String toString() {
        return this.getStatus();
    }
}
