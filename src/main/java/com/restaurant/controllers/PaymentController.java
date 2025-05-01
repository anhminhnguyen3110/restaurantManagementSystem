package com.restaurant.controllers;

import com.restaurant.dtos.payment.CreatePaymentDto;
import com.restaurant.dtos.payment.GetPaymentDto;
import com.restaurant.models.Payment;

import java.util.List;

public interface PaymentController {
    void createPayment(CreatePaymentDto createPaymentDto);

    List<Payment> findPayments(GetPaymentDto getPaymentDto);
}
