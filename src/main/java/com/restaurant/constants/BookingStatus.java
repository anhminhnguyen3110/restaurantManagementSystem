package com.restaurant.constants;

public enum BookingStatus {
    BOOKED("Booked"),
    CANCELLED("Cancelled"),
    COMPLETED("Completed");

    private final String status;

    BookingStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return status;
    }
}
