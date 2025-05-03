package com.restaurant.validators;

import com.restaurant.constants.RestaurantStatus;
import com.restaurant.dtos.restaurant.CreateRestaurantDto;
import com.restaurant.dtos.restaurant.UpdateRestaurantDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RestaurantInputValidatorTest {

    private RestaurantInputValidator validator;
    private CreateRestaurantDto createDto;

    @BeforeEach
    void setUp() {
        validator = RestaurantInputValidator.INSTANCE;
        createDto = new CreateRestaurantDto();
        createDto.setName("Test Restaurant");
        createDto.setAddress("123 Main St");
        createDto.setMaxX(5);
        createDto.setMaxY(7);
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
    void validateCreate_missingAddress_reportsAddressError() {
        createDto.setAddress(null);
        List<String> errors = validator.validateCreate(createDto);
        assertEquals(1, errors.size());
        assertEquals("• Address is required.", errors.get(0));
    }

    @Test
    void validateCreate_blankAddress_reportsAddressError() {
        createDto.setAddress("   ");
        List<String> errors = validator.validateCreate(createDto);
        assertEquals(1, errors.size());
        assertEquals("• Address is required.", errors.get(0));
    }

    @Test
    void validateCreate_maxXBelowRange_reportsWidthError() {
        createDto.setMaxX(0);
        List<String> errors = validator.validateCreate(createDto);
        assertEquals(1, errors.size());
        assertEquals("• Map width (X) must be between 1 and 10.", errors.get(0));
    }

    @Test
    void validateCreate_maxXAboveRange_reportsWidthError() {
        createDto.setMaxX(11);
        List<String> errors = validator.validateCreate(createDto);
        assertEquals(1, errors.size());
        assertEquals("• Map width (X) must be between 1 and 10.", errors.get(0));
    }

    @Test
    void validateCreate_maxYBelowRange_reportsHeightError() {
        createDto.setMaxY(0);
        List<String> errors = validator.validateCreate(createDto);
        assertEquals(1, errors.size());
        assertEquals("• Map height (Y) must be between 1 and 10.", errors.get(0));
    }

    @Test
    void validateCreate_maxYAboveRange_reportsHeightError() {
        createDto.setMaxY(12);
        List<String> errors = validator.validateCreate(createDto);
        assertEquals(1, errors.size());
        assertEquals("• Map height (Y) must be between 1 and 10.", errors.get(0));
    }

    @Test
    void validateCreate_multipleErrors_reportsAll() {
        createDto.setName("");
        createDto.setAddress("");
        createDto.setMaxX(0);
        createDto.setMaxY(12);
        List<String> errors = validator.validateCreate(createDto);
        assertEquals(4, errors.size());
        assertEquals("• Name is required.", errors.get(0));
        assertEquals("• Address is required.", errors.get(1));
        assertEquals("• Map width (X) must be between 1 and 10.", errors.get(2));
        assertEquals("• Map height (Y) must be between 1 and 10.", errors.get(3));
    }

    @Test
    void validateUpdate_valid_noErrors() {
        UpdateRestaurantDto u = new UpdateRestaurantDto();
        u.setName("Updt Name");
        u.setAddress("456 Side St");
        u.setMaxX(3);
        u.setMaxY(4);
        u.setId(99);
        u.setStatus(RestaurantStatus.ACTIVE);
        List<String> errors = validator.validateUpdate(u);
        assertTrue(errors.isEmpty());
    }

    @Test
    void validateUpdate_missingId_reportsIdError() {
        UpdateRestaurantDto u = new UpdateRestaurantDto();
        u.setName("Name");
        u.setAddress("Addr");
        u.setMaxX(2);
        u.setMaxY(2);
        u.setStatus(RestaurantStatus.INACTIVE);
        List<String> errors = validator.validateUpdate(u);
        assertEquals(1, errors.size());
        assertEquals("• Restaurant ID is required.", errors.get(0));
    }

    @Test
    void validateUpdate_missingStatus_reportsStatusError() {
        UpdateRestaurantDto u = new UpdateRestaurantDto();
        u.setName("Name");
        u.setAddress("Addr");
        u.setMaxX(2);
        u.setMaxY(2);
        u.setId(1);
        List<String> errors = validator.validateUpdate(u);
        assertEquals(1, errors.size());
        assertEquals("• Restaurant status is required.", errors.get(0));
    }

    @Test
    void validateUpdate_propagatesCreateAndUpdateErrors_reportsAll() {
        UpdateRestaurantDto u = new UpdateRestaurantDto();
        u.setName("");
        u.setAddress("");
        u.setMaxX(0);
        u.setMaxY(11);
        u.setId(0);
        u.setStatus(null);
        List<String> errors = validator.validateUpdate(u);
        assertEquals(6, errors.size());
        assertEquals("• Name is required.", errors.get(0));
        assertEquals("• Address is required.", errors.get(1));
        assertEquals("• Map width (X) must be between 1 and 10.", errors.get(2));
        assertEquals("• Map height (Y) must be between 1 and 10.", errors.get(3));
        assertEquals("• Restaurant ID is required.", errors.get(4));
        assertEquals("• Restaurant status is required.", errors.get(5));
    }
}
