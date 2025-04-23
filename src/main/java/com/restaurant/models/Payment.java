package com.restaurant.models;

import com.restaurant.constants.PaymentMethod;
import com.restaurant.constants.PaymentStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment extends BaseModel {
    @OneToOne
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(nullable = false)
    private double userPayAmount;

    @Column(nullable = false)
    private double changeAmount;

    @Column(length = 10, nullable = false)
    private PaymentMethod method;

    @Column(length = 10, nullable = false)
    private PaymentStatus status;

    @Column(name = "ts", columnDefinition = "TIMESTAMP")
    private LocalDateTime timestamp = LocalDateTime.now();

    public Payment() {
    }

    public Payment(Order order, double amount, PaymentMethod method, PaymentStatus status) {
        this.order = order;
        this.changeAmount = amount;
        this.method = method;
        this.status = status;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order o) {
        this.order = o;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime t) {
        this.timestamp = t;
    }

    public double getUserPayAmount() {
        return userPayAmount;
    }

    public void setUserPayAmount(double userPayAmount) {
        this.userPayAmount = userPayAmount;
    }

    public double getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(double changeAmount) {
        this.changeAmount = changeAmount;
    }
}
