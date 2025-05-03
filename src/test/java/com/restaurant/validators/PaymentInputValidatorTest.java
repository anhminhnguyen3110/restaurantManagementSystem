package com.restaurant.validators;

import com.restaurant.constants.PaymentMethod;
import com.restaurant.dtos.payment.CreatePaymentDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaymentInputValidatorTest {

    private PaymentInputValidator validator;
    private CreatePaymentDto dto;

    @BeforeEach
    void setUp() {
        validator = PaymentInputValidator.INSTANCE;
        dto = new CreatePaymentDto();
        dto.setOrderId(1);
        dto.setMethod(PaymentMethod.CREDIT_CARD);
        dto.setUserPayAmount(100.00);
        dto.setChangeAmount(0.00);
    }

    @Test
    void validateCreate_valid_noErrors() {
        List<String> errors = validator.validateCreate(dto);
        assertTrue(errors.isEmpty());
    }

    @Test
    void validateCreate_orderIdZero_reportsOrderError() {
        dto.setOrderId(0);
        List<String> errors = validator.validateCreate(dto);
        assertEquals(1, errors.size());
        assertEquals("• Order is required.", errors.get(0));
    }

    @Test
    void validateCreate_methodNull_reportsMethodError() {
        dto.setMethod(null);
        List<String> errors = validator.validateCreate(dto);
        assertEquals(1, errors.size());
        assertEquals("• Payment method is required.", errors.get(0));
    }

    @Test
    void validateCreate_userPayAmountZero_reportsPaidAmountError() {
        dto.setUserPayAmount(0);
        List<String> errors = validator.validateCreate(dto);
        assertEquals(1, errors.size());
        assertEquals("• Paid amount must be greater than zero.", errors.get(0));
    }

    @Test
    void validateCreate_userPayAmountNegative_reportsPaidAmountError() {
        dto.setUserPayAmount(-10.50);
        List<String> errors = validator.validateCreate(dto);
        assertEquals(1, errors.size());
        assertEquals("• Paid amount must be greater than zero.", errors.get(0));
    }

    @Test
    void validateCreate_changeAmountNegative_reportsChangeAmountError() {
        dto.setChangeAmount(-5.00);
        List<String> errors = validator.validateCreate(dto);
        assertEquals(1, errors.size());
        assertEquals("• Change amount cannot be negative.", errors.get(0));
    }

    @Test
    void validateCreate_multipleInvalidFields_reportsAllErrors() {
        dto.setOrderId(0);
        dto.setMethod(null);
        dto.setUserPayAmount(0);
        dto.setChangeAmount(-1.00);
        List<String> errors = validator.validateCreate(dto);
        assertEquals(4, errors.size());
        assertEquals("• Order is required.", errors.get(0));
        assertEquals("• Payment method is required.", errors.get(1));
        assertEquals("• Paid amount must be greater than zero.", errors.get(2));
        assertEquals("• Change amount cannot be negative.", errors.get(3));
    }
}
