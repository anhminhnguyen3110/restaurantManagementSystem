package com.restaurant.constants;

public enum BookingStatus {
    BOOKED("booked"),
    CANCELLED("cancelled"),
    COMPLETED("completed");

    private final String status;

    BookingStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status.toLowerCase().replace("_", " ");
    }

    @Override
    public String toString() {
        return this.getStatus();
    }

    public static BookingStatus fromString(String status) {
        for (BookingStatus bookingStatus : BookingStatus.values()) {
            if (bookingStatus.status.equalsIgnoreCase(status)) {
                return bookingStatus;
            }
        }
        throw new IllegalArgumentException("No constant with text " + status + " found");
    }
}
