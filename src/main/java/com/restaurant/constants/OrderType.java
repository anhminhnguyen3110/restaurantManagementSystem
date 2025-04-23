package com.restaurant.constants;

public enum OrderType {
    DINE_IN("dine_in"),
    TAKE_AWAY("take_away"),
    DELIVERY("delivery");

    private final String type;

    OrderType(String type) {
        this.type = type;
    }

    public String getType() {
        return type.toLowerCase().replace("_", " ");
    }

    @Override
    public String toString() {
        return this.getType();
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
