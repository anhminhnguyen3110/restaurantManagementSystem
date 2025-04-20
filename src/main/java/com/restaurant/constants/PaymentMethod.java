package com.restaurant.constants;

public enum PaymentMethod {
    CASH("Cash"),
    CREDIT_CARD("Credit_Card"),
    DIRECT_BANK_TRANSFER("Direct_Bank_Transfer"),
    EWALLET("E_Wallet");

    private final String method;

    PaymentMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    @Override
    public String toString() {
        return method;
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
