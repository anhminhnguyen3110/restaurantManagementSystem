package com.restaurant.models;

import com.restaurant.constants.PaymentMethod;
import com.restaurant.constants.PaymentStatus;
import jakarta.persistence.*;

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
    private PaymentStatus status = PaymentStatus.COMPLETED;

    public Payment() {
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
