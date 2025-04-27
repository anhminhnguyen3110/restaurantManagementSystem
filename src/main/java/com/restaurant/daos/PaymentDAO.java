package com.restaurant.daos;

import com.restaurant.dtos.payment.GetPaymentDto;
import com.restaurant.models.Payment;

import java.util.List;

public interface PaymentDAO {
    void add(Payment payment);

    Payment getById(int id);

    List<Payment> find(GetPaymentDto dto);

    boolean existsByOrder(int orderId);
}