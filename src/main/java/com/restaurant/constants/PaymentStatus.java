package com.restaurant.constants;

public enum PaymentStatus {
    PENDING("pending"),
    COMPLETED("completed"),
    CANCELLED("cancelled"),
    FAILED("failed");

    private final String status;

    PaymentStatus(String status) {
        this.status = status;
    }

    public static PaymentStatus fromString(String status) {
        for (PaymentStatus paymentStatus : PaymentStatus.values()) {
            if (paymentStatus.status.equalsIgnoreCase(status)) {
                return paymentStatus;
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
