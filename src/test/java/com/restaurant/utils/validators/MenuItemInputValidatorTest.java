package com.restaurant.utils.validators;

import com.restaurant.dtos.menuItem.CreateMenuItemDto;
import com.restaurant.dtos.menuItem.UpdateMenuItemDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MenuItemInputValidatorTest {

    private CreateMenuItemDto baseDto() {
        CreateMenuItemDto dto = new CreateMenuItemDto();
        dto.setName("Cheeseburger");
        dto.setDescription("Delicious");
        dto.setPrice(9.99);
        dto.setMenuId(1);
        return dto;
    }

    @Test
    void validCreateDto_returnsNoErrors() {
        CreateMenuItemDto dto = baseDto();
        List<String> errors = MenuItemInputValidator.validate(dto);
        assertTrue(errors.isEmpty(), "Expected no errors for valid CreateMenuItemDto");
    }

    @Test
    void missingName_reportsNameRequired() {
        CreateMenuItemDto dto = baseDto();
        dto.setName("  ");
        List<String> errors = MenuItemInputValidator.validate(dto);
        assertEquals(1, errors.size());
        assertEquals("• Name is required.", errors.get(0));
    }

    @Test
    void zeroPrice_reportsPriceError() {
        CreateMenuItemDto dto = baseDto();
        dto.setPrice(0.0);
        List<String> errors = MenuItemInputValidator.validate(dto);
        assertEquals(1, errors.size());
        assertEquals("• Price must be greater than zero.", errors.get(0));
    }

    @Test
    void negativePrice_reportsPriceError() {
        CreateMenuItemDto dto = baseDto();
        dto.setPrice(-5.0);
        List<String> errors = MenuItemInputValidator.validate(dto);
        assertEquals(1, errors.size());
        assertEquals("• Price must be greater than zero.", errors.get(0));
    }

    @Test
    void missingNameAndInvalidPrice_reportsBothErrors() {
        CreateMenuItemDto dto = baseDto();
        dto.setName("");
        dto.setPrice(0);
        List<String> errors = MenuItemInputValidator.validate(dto);
        assertEquals(2, errors.size());
        assertTrue(errors.contains("• Name is required."));
        assertTrue(errors.contains("• Price must be greater than zero."));
    }

    @Test
    void validUpdateDto_returnsNoErrors() {
        UpdateMenuItemDto dto = new UpdateMenuItemDto();
        dto.setId(10);
        dto.setName("Veggie Pizza");
        dto.setDescription("Healthy");
        dto.setPrice(12.50);
        dto.setMenuId(2);
        List<String> errors = MenuItemInputValidator.validate(dto);
        assertTrue(errors.isEmpty(), "Expected no errors for valid UpdateMenuItemDto");
    }

    @Test
    void updateDto_missingFields_reportsSameAsCreate() {
        UpdateMenuItemDto dto = new UpdateMenuItemDto();
        dto.setId(11);
        dto.setName(null);
        dto.setPrice(-1.0);
        List<String> errors = MenuItemInputValidator.validate(dto);
        assertEquals(2, errors.size());
        assertTrue(errors.contains("• Name is required."));
        assertTrue(errors.contains("• Price must be greater than zero."));
    }
}
