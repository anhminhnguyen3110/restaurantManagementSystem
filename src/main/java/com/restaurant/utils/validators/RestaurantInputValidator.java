package com.restaurant.utils.validators;

import com.restaurant.dtos.restaurant.CreateRestaurantDto;
import com.restaurant.dtos.restaurant.UpdateRestaurantDto;

import java.util.ArrayList;
import java.util.List;

public class RestaurantInputValidator {
    public static List<String> validate(CreateRestaurantDto dto) {
        List<String> errors = new ArrayList<>();
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            errors.add("• Name is required.");
        }
        if (dto.getAddress() == null || dto.getAddress().trim().isEmpty()) {
            errors.add("• Address is required.");
        }
        if (dto.getMaxX() < 1 || dto.getMaxX() > 10) {
            errors.add("• Map width (X) must be between 1 and 10.");
        }
        if (dto.getMaxY() < 1 || dto.getMaxY() > 10) {
            errors.add("• Map height (Y) must be between 1 and 10.");
        }
        return errors;
    }

    public static List<String> validate(UpdateRestaurantDto dto) {
        CreateRestaurantDto base = new CreateRestaurantDto();
        base.setName(dto.getName());
        base.setAddress(dto.getAddress());
        base.setMaxX(dto.getMaxX());
        base.setMaxY(dto.getMaxY());
        return validate(base);
    }
}