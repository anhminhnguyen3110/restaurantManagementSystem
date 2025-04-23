package com.restaurant.constants;

public enum ShipmentStatus {
    SHIPPING("shipping"),
    SUCCESS("success"),
    FAILED("failed");

    private final String status;

    ShipmentStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status.toLowerCase().replace("_", " ");
    }

    @Override
    public String toString() {
        return this.getStatus();
    }

    public static ShipmentStatus fromString(String status) {
        for (ShipmentStatus shipmentStatus : ShipmentStatus.values()) {
            if (shipmentStatus.status.equalsIgnoreCase(status)) {
                return shipmentStatus;
            }
        }
        throw new IllegalArgumentException("No constant with text " + status + " found");
    }
}
