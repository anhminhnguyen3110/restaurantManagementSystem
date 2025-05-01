package com.restaurant.utils.validators;

import com.restaurant.dtos.restaurantTable.CreateRestaurantTableDto;
import com.restaurant.dtos.restaurantTable.UpdateRestaurantTableDto;

import java.util.ArrayList;
import java.util.List;

public class RestaurantTableInputValidator {
    private static final int MAX = 10;

    public static List<String> validate(CreateRestaurantTableDto dto) {
        List<String> errors = new ArrayList<>();
        if (dto.getNumber() <= 0) {
            errors.add("• Table number must be positive.");
        }
        if (dto.getCapacity() <= 0) {
            errors.add("• Capacity must be positive.");
        }
        if (dto.getStartX() < 0 || dto.getStartX() >= MAX) {
            errors.add("• Start X out of bounds (0–9).");
        }
        if (dto.getStartY() < 0 || dto.getStartY() >= MAX) {
            errors.add("• Start Y out of bounds (0–9).");
        }
        if (dto.getEndX() < dto.getStartX() || dto.getEndX() >= MAX) {
            errors.add("• End X must be ≥ Start X and < 10.");
        }
        if (dto.getEndY() < dto.getStartY() || dto.getEndY() >= MAX) {
            errors.add("• End Y must be ≥ Start Y and < 10.");
        }
        return errors;
    }

    public static List<String> validate(UpdateRestaurantTableDto dto) {
        return validate((CreateRestaurantTableDto) dto);
    }
}
