package com.restaurant.validators;

import com.restaurant.dtos.menuItem.CreateMenuItemDto;
import com.restaurant.dtos.menuItem.UpdateMenuItemDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MenuItemInputValidatorTest {

    private MenuItemInputValidator validator;
    private CreateMenuItemDto createDto;

    @BeforeEach
    void setUp() {
        validator = MenuItemInputValidator.INSTANCE;
        createDto = new CreateMenuItemDto();
        createDto.setName("Burger");
        createDto.setDescription("Tasty beef burger");
        createDto.setPrice(9.99);
        createDto.setMenuId(3);
    }

    @Test
    void validateCreate_valid_noErrors() {
        List<String> errors = validator.validateCreate(createDto);
        assertTrue(errors.isEmpty());
    }

    @Test
    void validateCreate_missingName_reportsNameError() {
        createDto.setName(null);
        List<String> errors = validator.validateCreate(createDto);
        assertEquals(1, errors.size());
        assertEquals("• Name is required.", errors.get(0));
    }

    @Test
    void validateCreate_blankName_reportsNameError() {
        createDto.setName("   ");
        List<String> errors = validator.validateCreate(createDto);
        assertEquals(1, errors.size());
        assertEquals("• Name is required.", errors.get(0));
    }

    @Test
    void validateCreate_priceZero_reportsPriceError() {
        createDto.setPrice(0);
        List<String> errors = validator.validateCreate(createDto);
        assertEquals(1, errors.size());
        assertEquals("• Price must be greater than zero.", errors.get(0));
    }

    @Test
    void validateCreate_priceNegative_reportsPriceError() {
        createDto.setPrice(-5);
        List<String> errors = validator.validateCreate(createDto);
        assertEquals(1, errors.size());
        assertEquals("• Price must be greater than zero.", errors.get(0));
    }

    @Test
    void validateCreate_menuIdZero_reportsMenuError() {
        createDto.setMenuId(0);
        List<String> errors = validator.validateCreate(createDto);
        assertEquals(1, errors.size());
        assertEquals("• Menu must be selected.", errors.get(0));
    }

    @Test
    void validateCreate_multipleErrors_reportsAll() {
        createDto.setName("");
        createDto.setPrice(0);
        createDto.setMenuId(-1);
        List<String> errors = validator.validateCreate(createDto);
        assertEquals(3, errors.size());
        assertEquals("• Name is required.", errors.get(0));
        assertEquals("• Price must be greater than zero.", errors.get(1));
        assertEquals("• Menu must be selected.", errors.get(2));
    }

    @Test
    void validateUpdate_valid_noErrors() {
        UpdateMenuItemDto updateDto = new UpdateMenuItemDto();
        updateDto.setId(10);
        updateDto.setName("Salad");
        updateDto.setDescription("Fresh veggies");
        updateDto.setPrice(5.50);
        updateDto.setMenuId(2);
        List<String> errors = validator.validateUpdate(updateDto);
        assertTrue(errors.isEmpty());
    }

    @Test
    void validateUpdate_missingId_reportsIdError() {
        UpdateMenuItemDto updateDto = new UpdateMenuItemDto();
        updateDto.setName("Fries");
        updateDto.setDescription("Crispy");
        updateDto.setPrice(3.00);
        updateDto.setMenuId(1);
        List<String> errors = validator.validateUpdate(updateDto);
        assertEquals(1, errors.size());
        assertEquals("• Menu item ID is required.", errors.get(0));
    }

    @Test
    void validateUpdate_propagatesCreateErrors_andIdError() {
        UpdateMenuItemDto updateDto = new UpdateMenuItemDto();
        updateDto.setId(0);
        updateDto.setName("");
        updateDto.setPrice(0);
        updateDto.setMenuId(0);
        List<String> errors = validator.validateUpdate(updateDto);
        assertEquals(4, errors.size());
        assertEquals("• Name is required.", errors.get(0));
        assertEquals("• Price must be greater than zero.", errors.get(1));
        assertEquals("• Menu must be selected.", errors.get(2));
        assertEquals("• Menu item ID is required.", errors.get(3));
    }
}
