package com.restaurant.constants;

public enum OrderStatus {
    PENDING("pending"),
    PREPARING("preparing"),
    READY("ready"),
    PROCESSED("processed"),
    READY_FOR_PICKUP("ready_for_pickup"),
    COMPLETED("completed"),
    CANCELLED("cancelled");

    private final String status;

    OrderStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status.toLowerCase().replace("_", " ");
    }

    @Override
    public String toString() {
        return this.getStatus();
    }

    public static OrderStatus fromString(String status) {
        for (OrderStatus os : OrderStatus.values()) {
            if (os.status.equalsIgnoreCase(status)) {
                return os;
            }
        }
        throw new IllegalArgumentException("No constant with status " + status + " found");
    }
}