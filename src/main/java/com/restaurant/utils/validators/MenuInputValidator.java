package com.restaurant.utils.validators;

import com.restaurant.dtos.menu.CreateMenuDto;
import com.restaurant.dtos.menu.UpdateMenuDto;

import java.util.ArrayList;
import java.util.List;

public class MenuInputValidator {
    public static List<String> validate(CreateMenuDto dto) {
        List<String> errors = new ArrayList<>();
        if (dto.getName() == null || dto.getName().trim().isEmpty()) errors.add("• Name is required.");
        if (dto.getRestaurantId() <= 0) errors.add("• Restaurant must be selected.");
        return errors;
    }

    public static List<String> validate(UpdateMenuDto dto) {
        return validate((CreateMenuDto) dto);
    }
}