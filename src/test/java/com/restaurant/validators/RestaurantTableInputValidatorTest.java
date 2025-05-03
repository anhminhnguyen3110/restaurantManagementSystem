package com.restaurant.validators;

import com.restaurant.dtos.restaurantTable.CreateRestaurantTableDto;
import com.restaurant.dtos.restaurantTable.UpdateRestaurantTableDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RestaurantTableInputValidatorTest {

    private RestaurantTableInputValidator validator;
    private CreateRestaurantTableDto createDto;

    @BeforeEach
    void setUp() {
        validator = RestaurantTableInputValidator.INSTANCE;
        createDto = new CreateRestaurantTableDto();
        createDto.setRestaurantId(1);
        createDto.setNumber(1);
        createDto.setCapacity(4);
        createDto.setStartX(0);
        createDto.setStartY(0);
        createDto.setEndX(1);
        createDto.setEndY(1);
    }

    @Test
    void validateCreate_valid_noErrors() {
        List<String> errors = validator.validateCreate(createDto);
        assertTrue(errors.isEmpty());
    }

    @Test
    void validateCreate_missingRestaurant_reportsError() {
        createDto.setRestaurantId(0);
        List<String> errors = validator.validateCreate(createDto);
        assertEquals(1, errors.size());
        assertEquals("• Restaurant must be selected.", errors.get(0));
    }

    @Test
    void validateCreate_nonPositiveNumber_reportsError() {
        createDto.setNumber(0);
        List<String> errors = validator.validateCreate(createDto);
        assertEquals(1, errors.size());
        assertEquals("• Table number must be positive.", errors.get(0));
    }

    @Test
    void validateCreate_nonPositiveCapacity_reportsError() {
        createDto.setCapacity(0);
        List<String> errors = validator.validateCreate(createDto);
        assertEquals(1, errors.size());
        assertEquals("• Capacity must be positive.", errors.get(0));
    }

    @Test
    void validateCreate_startXBelowZero_reportsError() {
        createDto.setStartX(-1);
        List<String> errors = validator.validateCreate(createDto);
        assertEquals(1, errors.size());
        assertEquals("• Start X out of bounds (0–9).", errors.get(0));
    }

    @Test
    void validateCreate_startXAtMax_reportsError() {
        createDto.setStartX(10);
        List<String> errors = validator.validateCreate(createDto);
        assertTrue(errors.contains("• Start X out of bounds (0–9)."));
    }

    @Test
    void validateCreate_startYBelowZero_reportsError() {
        createDto.setStartY(-1);
        List<String> errors = validator.validateCreate(createDto);
        assertEquals(1, errors.size());
        assertEquals("• Start Y out of bounds (0–9).", errors.get(0));
    }

    @Test
    void validateCreate_startYAtMax_reportsError() {
        createDto.setStartY(10);
        List<String> errors = validator.validateCreate(createDto);
        assertTrue(errors.contains("• Start Y out of bounds (0–9)."));
    }

    @Test
    void validateCreate_endXBeforeStartX_reportsError() {
        createDto.setStartX(5);
        createDto.setEndX(4);
        List<String> errors = validator.validateCreate(createDto);
        assertEquals(1, errors.size());
        assertEquals("• End X must be ≥ Start X and < 10.", errors.get(0));
    }

    @Test
    void validateCreate_endXAtMax_reportsError() {
        createDto.setEndX(10);
        List<String> errors = validator.validateCreate(createDto);
        assertEquals(1, errors.size());
        assertEquals("• End X must be ≥ Start X and < 10.", errors.get(0));
    }

    @Test
    void validateCreate_endYBeforeStartY_reportsError() {
        createDto.setStartY(5);
        createDto.setEndY(4);
        List<String> errors = validator.validateCreate(createDto);
        assertEquals(1, errors.size());
        assertEquals("• End Y must be ≥ Start Y and < 10.", errors.get(0));
    }

    @Test
    void validateCreate_endYAtMax_reportsError() {
        createDto.setEndY(10);
        List<String> errors = validator.validateCreate(createDto);
        assertEquals(1, errors.size());
        assertEquals("• End Y must be ≥ Start Y and < 10.", errors.get(0));
    }

    @Test
    void validateCreate_multipleErrors_reportsAll() {
        createDto.setRestaurantId(0);
        createDto.setNumber(0);
        createDto.setCapacity(0);
        createDto.setStartX(-1);
        createDto.setStartY(10);
        createDto.setEndX(5);
        createDto.setEndY(4);
        List<String> errors = validator.validateCreate(createDto);
        assertEquals(6, errors.size());
        assertEquals("• Restaurant must be selected.", errors.get(0));
        assertEquals("• Table number must be positive.", errors.get(1));
        assertEquals("• Capacity must be positive.", errors.get(2));
        assertEquals("• Start X out of bounds (0–9).", errors.get(3));
        assertEquals("• Start Y out of bounds (0–9).", errors.get(4));
        assertEquals("• End Y must be ≥ Start Y and < 10.", errors.get(5));
    }

    @Test
    void validateUpdate_valid_noErrors() {
        UpdateRestaurantTableDto u = new UpdateRestaurantTableDto();
        u.setRestaurantId(2);
        u.setNumber(3);
        u.setCapacity(6);
        u.setStartX(1);
        u.setStartY(1);
        u.setEndX(2);
        u.setEndY(2);
        u.setId(5);
        List<String> errors = validator.validateUpdate(u);
        assertTrue(errors.isEmpty());
    }

    @Test
    void validateUpdate_missingId_reportsIdError() {
        UpdateRestaurantTableDto u = new UpdateRestaurantTableDto();
        u.setRestaurantId(2);
        u.setNumber(3);
        u.setCapacity(6);
        u.setStartX(1);
        u.setStartY(1);
        u.setEndX(2);
        u.setEndY(2);
        u.setId(0);
        List<String> errors = validator.validateUpdate(u);
        assertEquals(1, errors.size());
        assertEquals("• Table ID is required.", errors.get(0));
    }

    @Test
    void validateUpdate_propagatesCreateAndUpdateErrors_reportsAll() {
        UpdateRestaurantTableDto u = new UpdateRestaurantTableDto();
        u.setRestaurantId(0);
        u.setNumber(0);
        u.setCapacity(0);
        u.setStartX(10);
        u.setStartY(-1);
        u.setEndX(0);
        u.setEndY(0);
        u.setId(0);
        List<String> errors = validator.validateUpdate(u);
        assertEquals(7, errors.size());
        assertEquals("• Restaurant must be selected.", errors.get(0));
        assertEquals("• Table number must be positive.", errors.get(1));
        assertEquals("• Capacity must be positive.", errors.get(2));
        assertEquals("• Start X out of bounds (0–9).", errors.get(3));
        assertEquals("• Start Y out of bounds (0–9).", errors.get(4));
        assertEquals("• End X must be ≥ Start X and < 10.", errors.get(5));
        assertEquals("• Table ID is required.", errors.get(6));
    }
}
