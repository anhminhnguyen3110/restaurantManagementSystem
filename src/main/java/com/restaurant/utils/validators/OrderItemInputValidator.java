package com.restaurant.utils.validators;

import com.restaurant.models.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class OrderItemInputValidator {
    public static List<String> validate(MenuItem menuItem, int quantity) {
        List<String> errors = new ArrayList<>();
        if (menuItem == null) {
            errors.add("• Menu item must be selected.");
        }
        if (quantity <= 0) {
            errors.add("• Quantity must be at least 1.");
        }
        return errors;
    }
}
