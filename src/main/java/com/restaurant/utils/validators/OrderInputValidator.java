package com.restaurant.utils.validators;

import com.restaurant.constants.OrderType;
import java.util.ArrayList;
import java.util.List;

public class OrderInputValidator {
    public static List<String> validate(OrderType type, int tableId) {
        List<String> errors = new ArrayList<>();
        if (type == null) {
            errors.add("• Order type is required.");
            return errors;
        }
        if (type == OrderType.DINE_IN && tableId <= 0) {
            errors.add("• You must select a table for dine-in.");
        }
        return errors;
    }
}