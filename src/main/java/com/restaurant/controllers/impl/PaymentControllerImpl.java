package com.restaurant.controllers.impl;

import com.restaurant.controllers.PaymentController;
import com.restaurant.daos.OrderDAO;
import com.restaurant.daos.PaymentDAO;
import com.restaurant.di.Inject;
import com.restaurant.di.Injectable;
import com.restaurant.dtos.payment.CreatePaymentDto;
import com.restaurant.dtos.payment.GetPaymentDto;
import com.restaurant.models.Order;
import com.restaurant.models.Payment;

import java.util.List;

@Injectable
public class PaymentControllerImpl implements PaymentController {
    @Inject
    private PaymentDAO paymentDAO;
    @Inject
    private OrderDAO orderDAO;

    public PaymentControllerImpl() {
        // Default constructor for DI
    }

    @Override
    public void createPayment(CreatePaymentDto dto) {
        if (paymentDAO.existsByOrder(dto.getOrderId())) {
            System.out.println("Payment already exists for order " + dto.getOrderId());
            return;
        }
        Order order = orderDAO.getById(dto.getOrderId());
        Payment p = new Payment();
        p.setOrder(order);
        p.setMethod(dto.getMethod());
        p.setUserPayAmount(dto.getUserPayAmount());
        if (dto.getChangeAmount() > 0) {
            p.setChangeAmount(dto.getChangeAmount());
        }
        paymentDAO.add(p);
    }

    @Override
    public List<Payment> findPayments(GetPaymentDto dto) {
        return paymentDAO.find(dto);
    }
}