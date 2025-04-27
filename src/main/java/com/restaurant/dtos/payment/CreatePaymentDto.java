package com.restaurant.dtos.payment;

import com.restaurant.constants.PaymentMethod;

public class CreatePaymentDto {
    private int orderId;
    private PaymentMethod method;
    private double userPayAmount;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    public double getUserPayAmount() {
        return userPayAmount;
    }

    public void setUserPayAmount(double userPayAmount) {
        this.userPayAmount = userPayAmount;
    }
}
