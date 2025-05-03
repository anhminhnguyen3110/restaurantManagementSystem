package com.restaurant.validators;

import com.restaurant.constants.OrderItemStatus;
import com.restaurant.dtos.orderItem.CreateOrderItemDto;
import com.restaurant.dtos.orderItem.UpdateOrderItemDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderItemInputValidatorTest {

    private OrderItemInputValidator validator;
    private CreateOrderItemDto createDto;

    @BeforeEach
    void setUp() {
        validator = OrderItemInputValidator.INSTANCE;
        createDto = new CreateOrderItemDto();
        createDto.setMenuItemId(100);
        createDto.setQuantity(2);
        createDto.setCustomization("No onions");
        createDto.setOrderId(50);
    }

    @Test
    void validateCreate_valid_noErrors() {
        List<String> errors = validator.validateCreate(createDto);
        assertTrue(errors.isEmpty());
    }

    @Test
    void validateCreate_missingMenuItem_reportsMenuItemError() {
        createDto.setMenuItemId(0);
        List<String> errors = validator.validateCreate(createDto);
        assertEquals(1, errors.size());
        assertEquals("• Menu item must be selected.", errors.get(0));
    }

    @Test
    void validateCreate_invalidQuantity_reportsQuantityError() {
        createDto.setQuantity(0);
        List<String> errors = validator.validateCreate(createDto);
        assertEquals(1, errors.size());
        assertEquals("• Quantity must be at least 1.", errors.get(0));
    }

    @Test
    void validateCreate_negativeQuantity_reportsQuantityError() {
        createDto.setQuantity(-5);
        List<String> errors = validator.validateCreate(createDto);
        assertEquals(1, errors.size());
        assertEquals("• Quantity must be at least 1.", errors.get(0));
    }

    @Test
    void validateCreate_missingOrder_reportsOrderError() {
        createDto.setOrderId(0);
        List<String> errors = validator.validateCreate(createDto);
        assertEquals(1, errors.size());
        assertEquals("• Order must be selected.", errors.get(0));
    }

    @Test
    void validateCreate_multipleErrors_reportsAll() {
        createDto.setMenuItemId(0);
        createDto.setQuantity(0);
        createDto.setOrderId(0);
        List<String> errors = validator.validateCreate(createDto);
        assertEquals(3, errors.size());
        assertEquals("• Menu item must be selected.", errors.get(0));
        assertEquals("• Quantity must be at least 1.", errors.get(1));
        assertEquals("• Order must be selected.", errors.get(2));
    }

    @Test
    void validateUpdate_valid_noErrors() {
        UpdateOrderItemDto updateDto = new UpdateOrderItemDto();
        updateDto.setId(10);
        updateDto.setMenuItemId(100);
        updateDto.setQuantity(3);
        updateDto.setCustomization("Extra cheese");
        updateDto.setOrderId(25);
        updateDto.setStatus(OrderItemStatus.PENDING);
        List<String> errors = validator.validateUpdate(updateDto);
        assertTrue(errors.isEmpty());
    }

    @Test
    void validateUpdate_missingId_reportsIdError() {
        UpdateOrderItemDto updateDto = new UpdateOrderItemDto();
        updateDto.setMenuItemId(100);
        updateDto.setQuantity(1);
        updateDto.setOrderId(20);
        updateDto.setStatus(OrderItemStatus.READY);
        List<String> errors = validator.validateUpdate(updateDto);
        assertEquals(1, errors.size());
        assertEquals("• Order item ID is required.", errors.get(0));
    }

    @Test
    void validateUpdate_propagatesCreateErrors_andIdAndStatusErrors() {
        UpdateOrderItemDto updateDto = new UpdateOrderItemDto();
        updateDto.setId(0);
        updateDto.setMenuItemId(0);
        updateDto.setQuantity(0);
        updateDto.setOrderId(0);
        updateDto.setStatus(null);
        List<String> errors = validator.validateUpdate(updateDto);
        assertEquals(5, errors.size());
        assertEquals("• Menu item must be selected.", errors.get(0));
        assertEquals("• Quantity must be at least 1.", errors.get(1));
        assertEquals("• Order must be selected.", errors.get(2));
        assertEquals("• Order item ID is required.", errors.get(3));
        assertEquals("• Order item status is required.", errors.get(4));
    }
}
