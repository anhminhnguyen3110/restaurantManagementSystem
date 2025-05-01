package com.restaurant.constants;

public enum ShipmentService {
    GRAB("grab"), DIDI("didi"), INTERNAL("internal");

    private final String service;

    ShipmentService(String service) {
        this.service = service;
    }

    public static ShipmentService fromString(String service) {
        for (ShipmentService shipmentService : ShipmentService.values()) {
            if (shipmentService.service.equalsIgnoreCase(service)) {
                return shipmentService;
            }
        }
        throw new IllegalArgumentException("No constant with text " + service + " found");
    }

    public String getService() {
        return service.toLowerCase().replace("_", " ");
    }

    @Override
    public String toString() {
        return this.getService();
    }
}
