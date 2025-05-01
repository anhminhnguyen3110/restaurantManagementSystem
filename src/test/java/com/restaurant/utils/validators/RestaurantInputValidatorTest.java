package com.restaurant.utils.validators;

import com.restaurant.dtos.restaurant.CreateRestaurantDto;
import com.restaurant.dtos.restaurant.UpdateRestaurantDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantInputValidatorTest {

    @Test
    void validateCreate_allValid_noErrors() {
        CreateRestaurantDto dto = new CreateRestaurantDto();
        dto.setName("My Restaurant");
        dto.setAddress("123 Main St");
        dto.setMaxX(5);
        dto.setMaxY(7);

        List<String> errors = RestaurantInputValidator.validate(dto);
        assertTrue(errors.isEmpty());
    }

    @Test
    void validateCreate_missingName_andAddress_andInvalidDims_returnsAllErrors() {
        CreateRestaurantDto dto = new CreateRestaurantDto();
        dto.setName("  ");
        dto.setAddress(null);
        dto.setMaxX(0);
        dto.setMaxY(11);

        List<String> errors = RestaurantInputValidator.validate(dto);
        assertEquals(4, errors.size());
        assertTrue(errors.contains("• Name is required."));
        assertTrue(errors.contains("• Address is required."));
        assertTrue(errors.contains("• Map width (X) must be between 1 and 10."));
        assertTrue(errors.contains("• Map height (Y) must be between 1 and 10."));
    }

    @Test
    void validateCreate_invalidX_onlyWidthError() {
        CreateRestaurantDto dto = new CreateRestaurantDto();
        dto.setName("R");
        dto.setAddress("Addr");
        dto.setMaxX(20);
        dto.setMaxY(3);

        List<String> errors = RestaurantInputValidator.validate(dto);
        assertEquals(1, errors.size());
        assertEquals("• Map width (X) must be between 1 and 10.", errors.get(0));
    }

    @Test
    void validateCreate_invalidY_onlyHeightError() {
        CreateRestaurantDto dto = new CreateRestaurantDto();
        dto.setName("R");
        dto.setAddress("Addr");
        dto.setMaxX(3);
        dto.setMaxY(-1);

        List<String> errors = RestaurantInputValidator.validate(dto);
        assertEquals(1, errors.size());
        assertEquals("• Map height (Y) must be between 1 and 10.", errors.get(0));
    }

    @Test
    void validateUpdate_delegatesToCreate_sameBehavior() {
        UpdateRestaurantDto upd = new UpdateRestaurantDto();
        upd.setName("");
        upd.setAddress(" ");
        upd.setMaxX(15);
        upd.setMaxY(0);

        List<String> errors = RestaurantInputValidator.validate(upd);
        assertEquals(4, errors.size());
        assertTrue(errors.contains("• Name is required."));
        assertTrue(errors.contains("• Address is required."));
        assertTrue(errors.contains("• Map width (X) must be between 1 and 10."));
        assertTrue(errors.contains("• Map height (Y) must be between 1 and 10."));
    }
}
