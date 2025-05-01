package com.restaurant.utils.validators;

import com.restaurant.dtos.restaurantTable.CreateRestaurantTableDto;
import com.restaurant.dtos.restaurantTable.UpdateRestaurantTableDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantTableInputValidatorTest {

    private CreateRestaurantTableDto validDto() {
        CreateRestaurantTableDto dto = new CreateRestaurantTableDto();
        dto.setNumber(1);
        dto.setCapacity(4);
        dto.setStartX(2);
        dto.setStartY(3);
        dto.setEndX(5);
        dto.setEndY(6);
        return dto;
    }

    @Test
    void validate_allValid_noErrors() {
        CreateRestaurantTableDto dto = validDto();
        List<String> errors = RestaurantTableInputValidator.validate(dto);
        assertTrue(errors.isEmpty());
    }

    @Test
    void validate_invalidNumber_onlyNumberError() {
        CreateRestaurantTableDto dto = validDto();
        dto.setNumber(0);
        List<String> errors = RestaurantTableInputValidator.validate(dto);
        assertEquals(1, errors.size());
        assertEquals("• Table number must be positive.", errors.get(0));
    }

    @Test
    void validate_invalidCapacity_onlyCapacityError() {
        CreateRestaurantTableDto dto = validDto();
        dto.setCapacity(0);
        List<String> errors = RestaurantTableInputValidator.validate(dto);
        assertEquals(1, errors.size());
        assertEquals("• Capacity must be positive.", errors.get(0));
    }

    @Test
    void validate_invalidStartX_andStartY_errors() {
        CreateRestaurantTableDto dto = validDto();
        dto.setStartX(-1);
        dto.setStartY(10);
        List<String> errors = RestaurantTableInputValidator.validate(dto);
        assertEquals(3, errors.size());
        assertTrue(errors.contains("• Start X out of bounds (0–9)."));
        assertTrue(errors.contains("• Start Y out of bounds (0–9)."));
    }

    @Test
    void validate_endXBeforeStart_error() {
        CreateRestaurantTableDto dto = validDto();
        dto.setStartX(4);
        dto.setEndX(3);
        List<String> errors = RestaurantTableInputValidator.validate(dto);
        assertEquals(1, errors.size());
        assertEquals("• End X must be ≥ Start X and < 10.", errors.get(0));
    }

    @Test
    void validate_endXTooLarge_error() {
        CreateRestaurantTableDto dto = validDto();
        dto.setEndX(10);
        List<String> errors = RestaurantTableInputValidator.validate(dto);
        assertEquals(1, errors.size());
        assertEquals("• End X must be ≥ Start X and < 10.", errors.get(0));
    }

    @Test
    void validate_endYBeforeStart_error() {
        CreateRestaurantTableDto dto = validDto();
        dto.setStartY(5);
        dto.setEndY(4);
        List<String> errors = RestaurantTableInputValidator.validate(dto);
        assertEquals(1, errors.size());
        assertEquals("• End Y must be ≥ Start Y and < 10.", errors.get(0));
    }

    @Test
    void validate_endYTooLarge_error() {
        CreateRestaurantTableDto dto = validDto();
        dto.setStartY(5);
        dto.setEndY(10);
        List<String> errors = RestaurantTableInputValidator.validate(dto);
        assertEquals(1, errors.size());
        assertEquals("• End Y must be ≥ Start Y and < 10.", errors.get(0));
    }

    @Test
    void validate_multipleIssues_allErrors() {
        CreateRestaurantTableDto dto = new CreateRestaurantTableDto();
        dto.setNumber(-1);
        dto.setCapacity(0);
        dto.setStartX(11);
        dto.setStartY(-2);
        dto.setEndX(5);
        dto.setEndY(20);
        List<String> errors = RestaurantTableInputValidator.validate(dto);
        assertEquals(6, errors.size());
        assertTrue(errors.contains("• Table number must be positive."));
        assertTrue(errors.contains("• Capacity must be positive."));
        assertTrue(errors.contains("• Start X out of bounds (0–9)."));
        assertTrue(errors.contains("• Start Y out of bounds (0–9)."));
        assertTrue(errors.contains("• End X must be ≥ Start X and < 10."));
        assertTrue(errors.contains("• End Y must be ≥ Start Y and < 10."));
    }

    @Test
    void validateUpdate_delegatesToCreate_sameBehavior() {
        UpdateRestaurantTableDto upd = new UpdateRestaurantTableDto();
        upd.setNumber(0);
        upd.setCapacity(-5);
        upd.setStartX(10);
        upd.setStartY(10);
        upd.setEndX(9);
        upd.setEndY(8);
        List<String> errors = RestaurantTableInputValidator.validate(upd);
        assertEquals(6, errors.size());
        assertTrue(errors.contains("• Table number must be positive."));
        assertTrue(errors.contains("• Capacity must be positive."));
        assertTrue(errors.contains("• Start X out of bounds (0–9)."));
        assertTrue(errors.contains("• Start Y out of bounds (0–9)."));
        assertTrue(errors.contains("• End X must be ≥ Start X and < 10."));
        assertTrue(errors.contains("• End Y must be ≥ Start Y and < 10."));
    }
}
