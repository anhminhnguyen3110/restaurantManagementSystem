package com.restaurant.utils.validators;

import com.restaurant.dtos.menuItem.CreateMenuItemDto;
import com.restaurant.dtos.menuItem.UpdateMenuItemDto;

import java.util.ArrayList;
import java.util.List;

public class MenuItemInputValidator {
    public static List<String> validate(CreateMenuItemDto dto) {
        List<String> errors = new ArrayList<>();
        if (dto.getName() == null || dto.getName().trim().isEmpty()) errors.add("• Name is required.");
        if (dto.getPrice() <= 0) errors.add("• Price must be greater than zero.");
        return errors;
    }

    public static List<String> validate(UpdateMenuItemDto dto) {
        return validate((CreateMenuItemDto) dto);
    }
}