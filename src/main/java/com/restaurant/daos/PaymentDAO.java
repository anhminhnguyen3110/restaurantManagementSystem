package com.restaurant.daos;

import com.restaurant.models.Payment;

import java.util.List;

public interface PaymentDAO {
    void addPayment(Payment p);

    Payment getPaymentById(int id);

    List<Payment> getPaymentsByOrder(int orderId);

    void updatePaymentStatus(int id, String status);

    void deletePayment(int id);
}
