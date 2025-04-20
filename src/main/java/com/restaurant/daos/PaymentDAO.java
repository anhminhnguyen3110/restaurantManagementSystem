package com.restaurant.daos;

import com.restaurant.models.Payment;
import com.restaurant.constants.PaymentMethod;
import com.restaurant.constants.PaymentStatus;

import java.util.List;

public interface PaymentDAO {
    void add(Payment payment);

    Payment getById(int id);

    List<Payment> findAll();

    Payment findByOrderId(int orderId);

    List<Payment> findByStatus(PaymentStatus status);

    List<Payment> findByMethod(PaymentMethod method);

    void update(Payment payment);

    void delete(int id);
}