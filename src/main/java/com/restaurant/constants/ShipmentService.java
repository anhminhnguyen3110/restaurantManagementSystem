package com.restaurant.constants;

public enum ShipmentService {
    GRAB("Grab"), DIDI("Didi"), INTERNAL("Internal");

    private final String service;

    ShipmentService(String service) {
        this.service = service;
    }

    public String getService() {
        return service;
    }

    @Override
    public String toString() {
        return service;
    }

    public static ShipmentService fromString(String service) {
        for (ShipmentService shipmentService : ShipmentService.values()) {
            if (shipmentService.service.equalsIgnoreCase(service)) {
                return shipmentService;
            }
        }
        throw new IllegalArgumentException("No constant with text " + service + " found");
    }
}
