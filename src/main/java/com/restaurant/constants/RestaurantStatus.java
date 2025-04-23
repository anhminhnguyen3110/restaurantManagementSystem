package com.restaurant.constants;

public enum RestaurantStatus {
    INACTIVE("inactive"),
    ACTIVE("active"),
    MAINTENANCE("maintenance");

    private final String status;

    RestaurantStatus(String status) {
        this.status = status.toLowerCase().replace("_", " ");
    }

    public String getStatus() {
        return this.getStatus();
    }

    public static RestaurantStatus fromString(String status) {
        for (RestaurantStatus restaurantStatus : RestaurantStatus.values()) {
            if (restaurantStatus.status.equalsIgnoreCase(status)) {
                return restaurantStatus;
            }
        }
        throw new IllegalArgumentException("No constant with text " + status + " found");
    }

    @Override
    public String toString() {
        return status;
    }
}
