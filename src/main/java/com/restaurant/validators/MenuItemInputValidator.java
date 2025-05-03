package com.restaurant.validators;

import com.restaurant.dtos.menuItem.CreateMenuItemDto;
import com.restaurant.dtos.menuItem.UpdateMenuItemDto;

import java.util.ArrayList;
import java.util.List;

public class MenuItemInputValidator implements Validator<CreateMenuItemDto, UpdateMenuItemDto> {
    public static final MenuItemInputValidator INSTANCE = new MenuItemInputValidator();

    private MenuItemInputValidator() {
    }

    @Override
    public List<String> validateCreate(CreateMenuItemDto dto) {
        List<String> errors = new ArrayList<>();
        if (dto.getName() == null || dto.getName().trim().isEmpty())
            errors.add("• Name is required.");
        if (dto.getPrice() <= 0)
            errors.add("• Price must be greater than zero.");
        if (dto.getMenuId() <= 0)
            errors.add("• Menu must be selected.");
        return errors;
    }

    @Override
    public List<String> validateUpdate(UpdateMenuItemDto dto) {
        List<String> errors = new ArrayList<>(validateCreate(dto));
        if (dto.getId() <= 0)
            errors.add("• Menu item ID is required.");
        return errors;
    }
}
