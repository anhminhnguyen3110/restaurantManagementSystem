package com.restaurant.utils.validators;

import com.restaurant.constants.PaymentMethod;

import java.util.ArrayList;
import java.util.List;

public class PaymentInputValidator {
    public static List<String> validate(double totolPrice, PaymentMethod method, double payAmount) {
        List<String> errors = new ArrayList<>();
        if (totolPrice == 0) {
            errors.add("• Total price is required.");
            return errors;
        }

        if (method == null) {
            errors.add("• Payment method is required.");
        }

        if (payAmount < totolPrice) {
            errors.add(String.format("• Paid amount (%.2f) must be at least total price (%.2f).", payAmount, totolPrice));
        }
        return errors;
    }
}