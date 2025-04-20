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

    private double amount;

    @Column(length = 10)
    private PaymentMethod method;

    @Column(length = 10)
    private PaymentStatus status;

    @Column(name = "ts", columnDefinition = "TIMESTAMP")
    private LocalDateTime timestamp = LocalDateTime.now();

    public Payment() {
    }

    public Payment(Order order, double amount, PaymentMethod method, PaymentStatus status) {
        this.order = order;
        this.amount = amount;
        this.method = method;
        this.status = status;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order o) {
        this.order = o;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double a) {
        this.amount = a;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(String m) {
        this.method = PaymentMethod.fromString(m);
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(String s) {
        this.status = PaymentStatus.fromString(s);
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime t) {
        this.timestamp = t;
    }
}
