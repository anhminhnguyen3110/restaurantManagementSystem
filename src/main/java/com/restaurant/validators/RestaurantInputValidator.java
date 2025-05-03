package com.restaurant.validators;

import com.restaurant.dtos.restaurant.CreateRestaurantDto;
import com.restaurant.dtos.restaurant.UpdateRestaurantDto;

import java.util.ArrayList;
import java.util.List;

public class RestaurantInputValidator implements Validator<CreateRestaurantDto, UpdateRestaurantDto> {
    public static final RestaurantInputValidator INSTANCE = new RestaurantInputValidator();

    private RestaurantInputValidator() {
    }

    @Override
    public List<String> validateCreate(CreateRestaurantDto dto) {
        List<String> errors = new ArrayList<>();
        if (dto.getName() == null || dto.getName().trim().isEmpty())
            errors.add("• Name is required.");
        if (dto.getAddress() == null || dto.getAddress().trim().isEmpty())
            errors.add("• Address is required.");
        if (dto.getMaxX() < 1 || dto.getMaxX() > 10)
            errors.add("• Map width (X) must be between 1 and 10.");
        if (dto.getMaxY() < 1 || dto.getMaxY() > 10)
            errors.add("• Map height (Y) must be between 1 and 10.");
        return errors;
    }

    @Override
    public List<String> validateUpdate(UpdateRestaurantDto dto) {
        List<String> errors = new ArrayList<>(validateCreate(dto));
        if (dto.getId() <= 0)
            errors.add("• Restaurant ID is required.");
        if (dto.getStatus() == null)
            errors.add("• Restaurant status is required.");
        return errors;
    }
}
