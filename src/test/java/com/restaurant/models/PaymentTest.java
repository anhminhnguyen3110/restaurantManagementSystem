package com.restaurant.models;

import com.restaurant.constants.PaymentMethod;
import com.restaurant.constants.PaymentStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    @Test
    void defaultConstructor_andDefaults() {
        Payment p = new Payment();
        assertNull(p.getOrder());
        assertEquals(0.0, p.getUserPayAmount());
        assertEquals(0.0, p.getChangeAmount());
        assertNull(p.getMethod());
        assertEquals(PaymentStatus.COMPLETED, p.getStatus());
    }

    @Test
    void settersAndGetters_work() {
        Payment p = new Payment();
        Order o = new Order();
        p.setOrder(o);
        p.setUserPayAmount(15.5);
        p.setChangeAmount(0.5);
        p.setMethod(PaymentMethod.CASH);
        p.setStatus(PaymentStatus.FAILED);
        assertSame(o, p.getOrder());
        assertEquals(15.5, p.getUserPayAmount());
        assertEquals(0.5, p.getChangeAmount());
        assertEquals(PaymentMethod.CASH, p.getMethod());
        assertEquals(PaymentStatus.FAILED, p.getStatus());
    }
}
