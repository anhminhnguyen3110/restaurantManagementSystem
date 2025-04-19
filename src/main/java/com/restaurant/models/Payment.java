package com.restaurant.models;

import java.time.LocalDateTime;

public class Payment {
    private int id;
    private int orderId;
    private double amount;
    private String method;  // CASH, CARD, POS, etc.
    private String status;  // PENDING, PAID, FAILED
    private LocalDateTime timestamp;

    public Payment() {
    }

    public Payment(int id, int orderId, double amount, String method, String status, LocalDateTime ts) {
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.method = method;
        this.status = status;
        this.timestamp = ts;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
