package com.restaurant.validators;

import com.restaurant.constants.OrderStatus;
import com.restaurant.constants.OrderType;
import com.restaurant.dtos.order.CreateOrderDto;
import com.restaurant.dtos.order.UpdateOrderDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderInputValidatorTest {

    private OrderInputValidator validator;
    private CreateOrderDto createDto;

    @BeforeEach
    void setUp() {
        validator = OrderInputValidator.INSTANCE;
        createDto = new CreateOrderDto();
        createDto.setOrderType(OrderType.DINE_IN);
        createDto.setRestaurantId(10);
        createDto.setRestaurantTableId(5);
    }

    @Test
    void validateCreate_dineInWithTable_noErrors() {
        List<String> errors = validator.validateCreate(createDto);
        assertTrue(errors.isEmpty());
    }

    @Test
    void validateCreate_nonDineInIgnoresTable_noErrors() {
        createDto.setOrderType(OrderType.DELIVERY);
        createDto.setRestaurantTableId(0);
        List<String> errors = validator.validateCreate(createDto);
        assertTrue(errors.isEmpty());
    }

    @Test
    void validateCreate_missingOrderType_reportsOrderTypeError() {
        createDto.setOrderType(null);
        List<String> errors = validator.validateCreate(createDto);
        assertEquals(1, errors.size());
        assertEquals("• Order type is required.", errors.get(0));
    }

    @Test
    void validateCreate_invalidRestaurant_reportsRestaurantError() {
        createDto.setRestaurantId(0);
        List<String> errors = validator.validateCreate(createDto);
        assertEquals(1, errors.size());
        assertEquals("• Restaurant is required.", errors.get(0));
    }

    @Test
    void validateCreate_dineInMissingTable_reportsTableError() {
        createDto.setRestaurantTableId(0);
        List<String> errors = validator.validateCreate(createDto);
        assertEquals(1, errors.size());
        assertEquals("• You must select a table for dine-in.", errors.get(0));
    }

    @Test
    void validateCreate_multipleErrors_reportsAll() {
        createDto.setOrderType(null);
        createDto.setRestaurantId(0);
        createDto.setRestaurantTableId(0);
        List<String> errors = validator.validateCreate(createDto);
        assertEquals(2, errors.size());
        assertTrue(errors.contains("• Order type is required."));
        assertTrue(errors.contains("• Restaurant is required."));
    }

    @Test
    void validateUpdate_allFieldsSet_noErrors() {
        UpdateOrderDto updateDto = new UpdateOrderDto();
        updateDto.setOrderType(OrderType.DINE_IN);
        updateDto.setRestaurantId(20);
        updateDto.setRestaurantTableId(3);
        updateDto.setId(1);
        updateDto.setStatus(OrderStatus.COMPLETED);
        List<String> errors = validator.validateUpdate(updateDto);
        assertTrue(errors.isEmpty());
    }

    @Test
    void validateUpdate_missingId_reportsIdError() {
        UpdateOrderDto updateDto = new UpdateOrderDto();
        updateDto.setOrderType(OrderType.DELIVERY);
        updateDto.setRestaurantId(2);
        updateDto.setRestaurantTableId(0);
        updateDto.setStatus(OrderStatus.PENDING);
        List<String> errors = validator.validateUpdate(updateDto);
        assertEquals(1, errors.size());
        assertEquals("• Order ID is required.", errors.get(0));
    }

    @Test
    void validateUpdate_propagatesCreateAndUpdateErrors_reportsAll() {
        UpdateOrderDto updateDto = new UpdateOrderDto();
        updateDto.setOrderType(null);
        updateDto.setRestaurantId(0);
        updateDto.setRestaurantTableId(0);
        updateDto.setId(0);
        updateDto.setStatus(null);
        List<String> errors = validator.validateUpdate(updateDto);
        assertEquals(4, errors.size());
        assertTrue(errors.contains("• Order type is required."));
        assertTrue(errors.contains("• Restaurant is required."));
        assertTrue(errors.contains("• Order ID is required."));
        assertTrue(errors.contains("• Order status is required."));
    }
}
