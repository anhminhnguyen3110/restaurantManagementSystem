package com.restaurant.utils.validators;

import com.restaurant.constants.OrderType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderInputValidatorTest {

    @Test
    void nullType_returnsOrderTypeRequired() {
        List<String> errors = OrderInputValidator.validate(null, 1);
        assertEquals(1, errors.size());
        assertEquals("• Order type is required.", errors.get(0));
    }

    @Test
    void dineInWithNoTable_reportsTableRequired() {
        List<String> errors = OrderInputValidator.validate(OrderType.DINE_IN, 0);
        assertEquals(1, errors.size());
        assertEquals("• You must select a table for dine-in.", errors.get(0));
    }

    @Test
    void dineInWithValidTable_returnsNoErrors() {
        List<String> errors = OrderInputValidator.validate(OrderType.DINE_IN, 5);
        assertTrue(errors.isEmpty());
    }

    @Test
    void deliveryWithAnyTable_returnsNoErrors() {
        List<String> errorsZero = OrderInputValidator.validate(OrderType.DELIVERY, 0);
        List<String> errorsPositive = OrderInputValidator.validate(OrderType.DELIVERY, 3);
        assertTrue(errorsZero.isEmpty());
        assertTrue(errorsPositive.isEmpty());
    }
}
