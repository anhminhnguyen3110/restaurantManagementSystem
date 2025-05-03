package com.restaurant.validators;

import com.restaurant.dtos.menu.CreateMenuDto;
import com.restaurant.dtos.menu.UpdateMenuDto;

import java.util.ArrayList;
import java.util.List;

public class MenuInputValidator implements Validator<CreateMenuDto, UpdateMenuDto> {
    public static final MenuInputValidator INSTANCE = new MenuInputValidator();

    private MenuInputValidator() {
    }

    @Override
    public List<String> validateCreate(CreateMenuDto dto) {
        List<String> errors = new ArrayList<>();
        if (dto.getName() == null || dto.getName().trim().isEmpty())
            errors.add("• Name is required.");
        if (dto.getRestaurantId() <= 0)
            errors.add("• Restaurant must be selected.");
        return errors;
    }

    @Override
    public List<String> validateUpdate(UpdateMenuDto dto) {
        List<String> errors = new ArrayList<>(validateCreate(dto));
        if (dto.getId() <= 0)
            errors.add("• Menu ID is required.");
        return errors;
    }
}
