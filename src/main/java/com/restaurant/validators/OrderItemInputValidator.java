package com.restaurant.validators;

import com.restaurant.dtos.orderItem.CreateOrderItemDto;
import com.restaurant.dtos.orderItem.UpdateOrderItemDto;

import java.util.ArrayList;
import java.util.List;

public class OrderItemInputValidator implements Validator<CreateOrderItemDto, UpdateOrderItemDto> {
    public static final OrderItemInputValidator INSTANCE = new OrderItemInputValidator();

    private OrderItemInputValidator() {
    }

    @Override
    public List<String> validateCreate(CreateOrderItemDto dto) {
        List<String> errors = new ArrayList<>();
        if (dto.getMenuItemId() <= 0)
            errors.add("• Menu item must be selected.");
        if (dto.getQuantity() <= 0)
            errors.add("• Quantity must be at least 1.");
        if (dto.getOrderId() <= 0)
            errors.add("• Order must be selected.");
        return errors;
    }

    @Override
    public List<String> validateUpdate(UpdateOrderItemDto dto) {
        List<String> errors = new ArrayList<>(validateCreate(dto));
        if (dto.getId() <= 0)
            errors.add("• Order item ID is required.");
        if (dto.getStatus() == null)
            errors.add("• Order item status is required.");
        return errors;
    }
}
