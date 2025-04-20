package com.restaurant.constants;

public enum OrderType {
    DINE_IN("Dine_In"),
    TAKE_AWAY("Take_Away"),
    DELIVERY("Delivery");

    private final String type;

    OrderType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return type;
    }

    public static OrderType fromString(String status) {
        for (OrderType orderType : OrderType.values()) {
            if (orderType.type.equalsIgnoreCase(status)) {
                return orderType;
            }
        }
        throw new IllegalArgumentException("No constant with text " + status + " found");
    }
}
