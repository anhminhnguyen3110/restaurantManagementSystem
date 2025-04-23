package com.restaurant.constants;

public enum PaymentMethod {
    CASH("cash"),
    CREDIT_CARD("credit_card"),
    DIRECT_BANK_TRANSFER("direct_bank_transfer"),
    EWALLET("e_wallet");

    private final String method;

    PaymentMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method.toLowerCase().replace("_", " ");
    }

    @Override
    public String toString() {
        return this.getMethod();
    }

    public static PaymentMethod fromString(String status) {
        for (PaymentMethod paymentStatus : PaymentMethod.values()) {
            if (paymentStatus.method.equalsIgnoreCase(status)) {
                return paymentStatus;
            }
        }
        throw new IllegalArgumentException("No constant with text " + status + " found");
    }
}
