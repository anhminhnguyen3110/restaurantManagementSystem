package com.restaurant.controllers;

import com.restaurant.controllers.impl.PaymentControllerImpl;
import com.restaurant.daos.OrderDAO;
import com.restaurant.daos.PaymentDAO;
import com.restaurant.dtos.payment.CreatePaymentDto;
import com.restaurant.dtos.payment.GetPaymentDto;
import com.restaurant.models.Order;
import com.restaurant.models.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerImplTest {
    @Mock
    PaymentDAO paymentDAO;
    @Mock
    OrderDAO orderDAO;
    @InjectMocks
    PaymentControllerImpl controller;

    CreatePaymentDto createDto;
    GetPaymentDto getDto;

    @BeforeEach
    void setUp() {
        createDto = new CreatePaymentDto();
        getDto = new GetPaymentDto();
    }

    @Test
    void createPayment_exists_noAdd() {
        createDto.setOrderId(1);
        when(paymentDAO.existsByOrder(1)).thenReturn(true);
        controller.createPayment(createDto);
        verify(paymentDAO).existsByOrder(1);
        verifyNoMoreInteractions(paymentDAO, orderDAO);
    }

    @Test
    void createPayment_addsPayment_withChange() {
        createDto.setOrderId(2);
        createDto.setMethod(com.restaurant.constants.PaymentMethod.CREDIT_CARD);
        createDto.setUserPayAmount(50.0);
        createDto.setChangeAmount(5.0);
        when(paymentDAO.existsByOrder(2)).thenReturn(false);
        Order order = new Order();
        when(orderDAO.getById(2)).thenReturn(order);
        controller.createPayment(createDto);
        ArgumentCaptor<Payment> capt = ArgumentCaptor.forClass(Payment.class);
        verify(orderDAO).getById(2);
        verify(paymentDAO).add(capt.capture());
        Payment p = capt.getValue();
        assertSame(order, p.getOrder());
        assertEquals(com.restaurant.constants.PaymentMethod.CREDIT_CARD, p.getMethod());
        assertEquals(50.0, p.getUserPayAmount());
        assertEquals(5.0, p.getChangeAmount());
    }

    @Test
    void createPayment_addsPayment_noChange() {
        createDto.setOrderId(3);
        createDto.setMethod(com.restaurant.constants.PaymentMethod.CASH);
        createDto.setUserPayAmount(20.0);
        createDto.setChangeAmount(0.0);
        when(paymentDAO.existsByOrder(3)).thenReturn(false);
        Order order = new Order();
        when(orderDAO.getById(3)).thenReturn(order);
        controller.createPayment(createDto);
        ArgumentCaptor<Payment> capt = ArgumentCaptor.forClass(Payment.class);
        verify(orderDAO).getById(3);
        verify(paymentDAO).add(capt.capture());
        Payment p = capt.getValue();
        assertSame(order, p.getOrder());
        assertEquals(com.restaurant.constants.PaymentMethod.CASH, p.getMethod());
        assertEquals(20.0, p.getUserPayAmount());
        assertEquals(0.0, p.getChangeAmount());
    }

    @Test
    void findPayments_delegatesToDao() {
        List<Payment> list = List.of(new Payment(), new Payment());
        when(paymentDAO.find(getDto)).thenReturn(list);
        assertEquals(list, controller.findPayments(getDto));
        verify(paymentDAO).find(getDto);
    }
}
