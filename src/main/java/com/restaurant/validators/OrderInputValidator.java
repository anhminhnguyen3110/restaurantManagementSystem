package com.restaurant.validators;

import com.restaurant.constants.OrderType;
import com.restaurant.dtos.order.CreateOrderDto;
import com.restaurant.dtos.order.UpdateOrderDto;

import java.util.ArrayList;
import java.util.List;

public class OrderInputValidator implements Validator<CreateOrderDto, UpdateOrderDto> {
    public static final OrderInputValidator INSTANCE = new OrderInputValidator();

    private OrderInputValidator() {
    }

    @Override
    public List<String> validateCreate(CreateOrderDto dto) {
        List<String> errors = new ArrayList<>();
        if (dto.getOrderType() == null) {
            errors.add("• Order type is required.");
        }
        if (dto.getRestaurantId() <= 0) {
            errors.add("• Restaurant is required.");
        }
        if (dto.getOrderType() == OrderType.DINE_IN && dto.getRestaurantTableId() <= 0) {
            errors.add("• You must select a table for dine-in.");
        }
        return errors;
    }

    @Override
    public List<String> validateUpdate(UpdateOrderDto dto) {
        List<String> errors = new ArrayList<>(validateCreate(dto));
        if (dto.getId() <= 0) {
            errors.add("• Order ID is required.");
        }
        if (dto.getStatus() == null) {
            errors.add("• Order status is required.");
        }
        return errors;
    }
}
