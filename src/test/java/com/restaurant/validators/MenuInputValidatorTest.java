package com.restaurant.validators;

import com.restaurant.dtos.menu.CreateMenuDto;
import com.restaurant.dtos.menu.UpdateMenuDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MenuInputValidatorTest {

    private MenuInputValidator validator;
    private CreateMenuDto createDto;

    @BeforeEach
    void setUp() {
        validator = MenuInputValidator.INSTANCE;
        createDto = new CreateMenuDto();
        createDto.setName("Lunch Menu");
        createDto.setRestaurantId(42);
        createDto.setDescription("Delicious midday meals");
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
    void validateCreate_invalidRestaurant_reportsRestaurantError() {
        createDto.setRestaurantId(0);
        List<String> errors = validator.validateCreate(createDto);
        assertEquals(1, errors.size());
        assertEquals("• Restaurant must be selected.", errors.get(0));
    }

    @Test
    void validateCreate_missingBoth_reportsBothErrors() {
        createDto.setName("");
        createDto.setRestaurantId(-1);
        List<String> errors = validator.validateCreate(createDto);
        assertEquals(2, errors.size());
        assertEquals("• Name is required.", errors.get(0));
        assertEquals("• Restaurant must be selected.", errors.get(1));
    }

    @Test
    void validateUpdate_valid_noErrors() {
        UpdateMenuDto updateDto = new UpdateMenuDto();
        updateDto.setId(5);
        updateDto.setName("Dinner Menu");
        updateDto.setRestaurantId(7);
        updateDto.setDescription("Evening specials");
        List<String> errors = validator.validateUpdate(updateDto);
        assertTrue(errors.isEmpty());
    }

    @Test
    void validateUpdate_missingId_reportsIdError() {
        UpdateMenuDto updateDto = new UpdateMenuDto();
        updateDto.setName("Specials");
        updateDto.setRestaurantId(3);
        List<String> errors = validator.validateUpdate(updateDto);
        assertEquals(1, errors.size());
        assertEquals("• Menu ID is required.", errors.get(0));
    }

    @Test
    void validateUpdate_propagatesCreateErrors_andIdError() {
        UpdateMenuDto updateDto = new UpdateMenuDto();
        updateDto.setId(0);
        updateDto.setName("");
        updateDto.setRestaurantId(0);
        List<String> errors = validator.validateUpdate(updateDto);
        assertEquals(3, errors.size());
        assertEquals("• Name is required.", errors.get(0));
        assertEquals("• Restaurant must be selected.", errors.get(1));
        assertEquals("• Menu ID is required.", errors.get(2));
    }
}
