package com.restaurant.validators;

import com.restaurant.dtos.payment.CreatePaymentDto;

import java.util.ArrayList;
import java.util.List;

public class PaymentInputValidator implements Validator<CreatePaymentDto, CreatePaymentDto> {
    public static final PaymentInputValidator INSTANCE = new PaymentInputValidator();

    private PaymentInputValidator() {
    }

    @Override
    public List<String> validateCreate(CreatePaymentDto dto) {
        List<String> errors = new ArrayList<>();
        if (dto.getOrderId() <= 0)
            errors.add("• Order is required.");
        if (dto.getMethod() == null)
            errors.add("• Payment method is required.");
        if (dto.getUserPayAmount() <= 0)
            errors.add("• Paid amount must be greater than zero.");
        if (dto.getChangeAmount() < 0)
            errors.add("• Change amount cannot be negative.");
        return errors;
    }

    @Override
    public List<String> validateUpdate(CreatePaymentDto dto) {
        return validateCreate(dto);
    }
}
