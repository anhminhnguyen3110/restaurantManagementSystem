package com.restaurant.utils.validators;

import com.restaurant.constants.ShipmentService;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ShipmentInputValidator {
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9\\- ]{7,15}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public static List<String> validate(
            ShipmentService service,
            int shipperId,
            String customerName,
            String customerPhone,
            String customerEmail,
            String customerAddress
    ) {
        List<String> errors = new ArrayList<>();
        if (service == null) {
            errors.add("• Shipment service is required.");
        }
        if (service == ShipmentService.INTERNAL && shipperId <= 0) {
            errors.add("• Shipper must be selected for internal.");
        }
        if (customerName.isBlank()) {
            errors.add("• Customer name is required.");
        }
        if (!PHONE_PATTERN.matcher(customerPhone).matches()) {
            errors.add("• Valid customer phone is required.");
        }
        if (!customerEmail.isBlank()) {
            if (!EMAIL_PATTERN.matcher(customerEmail).matches()) {
                errors.add("• Valid customer email is required.");
            }
        }
        if (customerAddress.isBlank()) {
            errors.add("• Customer address is required.");
        }
        return errors;
    }
}
