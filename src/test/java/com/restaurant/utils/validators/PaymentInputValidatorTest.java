package com.restaurant.utils.validators;

import com.restaurant.constants.PaymentMethod;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PaymentInputValidatorTest {

    @Test
    void zeroTotalPrice_returnsTotalPriceErrorOnly() {
        List<String> errors = PaymentInputValidator.validate(0.0, PaymentMethod.CASH, 10.0);
        assertEquals(1, errors.size());
        assertEquals("• Total price is required.", errors.get(0));
    }

    @Test
    void nonZeroTotalPrice_nullMethod_returnsMethodErrorOnly_whenPayAmountSufficient() {
        List<String> errors = PaymentInputValidator.validate(50.0, null, 50.0);
        assertEquals(1, errors.size());
        assertEquals("• Payment method is required.", errors.get(0));
    }

    @Test
    void nonZeroTotalPrice_nullMethod_andInsufficientPay_returnsTwoErrors() {
        List<String> errors = PaymentInputValidator.validate(75.0, null, 50.0);
        assertEquals(2, errors.size());
        assertTrue(errors.contains("• Payment method is required."));
        assertTrue(errors.contains("• Paid amount (50.00) must be at least total price (75.00)."));
    }

    @Test
    void nonZeroTotalPrice_validMethod_insufficientPay_returnsPaidAmountErrorOnly() {
        List<String> errors = PaymentInputValidator.validate(20.0, PaymentMethod.CREDIT_CARD, 10.0);
        assertEquals(1, errors.size());
        assertEquals("• Paid amount (10.00) must be at least total price (20.00).", errors.get(0));
    }

    @Test
    void nonZeroTotalPrice_validMethod_sufficientPay_returnsNoErrors() {
        List<String> errors = PaymentInputValidator.validate(30.0, PaymentMethod.E_WALLET, 30.0);
        assertTrue(errors.isEmpty());
    }
}
