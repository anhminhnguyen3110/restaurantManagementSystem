package com.restaurant.utils.validators;

import com.restaurant.dtos.menu.CreateMenuDto;
import com.restaurant.dtos.menu.UpdateMenuDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MenuInputValidatorTest {

    private CreateMenuDto baseDto() {
        CreateMenuDto dto = new CreateMenuDto();
        dto.setName("Lunch Specials");
        dto.setRestaurantId(42);
        return dto;
    }

    @Test
    void validCreateDto_returnsNoErrors() {
        CreateMenuDto dto = baseDto();
        List<String> errors = MenuInputValidator.validate(dto);
        assertTrue(errors.isEmpty(), "Expected no errors for valid CreateMenuDto");
    }

    @Test
    void missingName_reportsNameRequired() {
        CreateMenuDto dto = baseDto();
        dto.setName("   ");
        List<String> errors = MenuInputValidator.validate(dto);
        assertEquals(1, errors.size());
        assertEquals("• Name is required.", errors.get(0));
    }

    @Test
    void nonPositiveRestaurantId_reportsRestaurantSelection() {
        CreateMenuDto dto = baseDto();
        dto.setRestaurantId(0);
        List<String> errors = MenuInputValidator.validate(dto);
        assertEquals(1, errors.size());
        assertEquals("• Restaurant must be selected.", errors.get(0));
    }

    @Test
    void missingNameAndInvalidRestaurantId_reportsBothErrors() {
        CreateMenuDto dto = new CreateMenuDto();
        dto.setName("");
        dto.setRestaurantId(-5);
        List<String> errors = MenuInputValidator.validate(dto);
        assertEquals(2, errors.size());
        assertTrue(errors.contains("• Name is required."));
        assertTrue(errors.contains("• Restaurant must be selected."));
    }

    @Test
    void validUpdateDto_returnsNoErrors() {
        UpdateMenuDto dto = new UpdateMenuDto();
        dto.setName("Dinner Menu");
        dto.setRestaurantId(7);
        List<String> errors = MenuInputValidator.validate(dto);
        assertTrue(errors.isEmpty(), "Expected no errors for valid UpdateMenuDto");
    }

    @Test
    void updateDto_missingFields_reportsSameAsCreate() {
        UpdateMenuDto dto = new UpdateMenuDto();
        dto.setName(null);
        dto.setRestaurantId(0);
        List<String> errors = MenuInputValidator.validate(dto);
        assertEquals(2, errors.size());
        assertTrue(errors.contains("• Name is required."));
        assertTrue(errors.contains("• Restaurant must be selected."));
    }
}
