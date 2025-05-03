package com.restaurant.validators;

import com.restaurant.constants.ShipmentService;
import com.restaurant.constants.ShipmentStatus;
import com.restaurant.dtos.shipment.CreateShipmentDto;
import com.restaurant.dtos.shipment.UpdateShipmentDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShipmentInputValidatorTest {

    private ShipmentInputValidator validator;
    private CreateShipmentDto createDto;

    @BeforeEach
    void setUp() {
        validator = ShipmentInputValidator.INSTANCE;
        createDto = new CreateShipmentDto();
        createDto.setServiceType(ShipmentService.GRAB);
        createDto.setOrderId(10);
        createDto.setShipperId(0);
        createDto.setCustomerName("John Doe");
        createDto.setCustomerPhone("+1234567890");
        createDto.setCustomerEmail("john@example.com");
        createDto.setCustomerAddress("123 Maple St");
    }

    @Test
    void validateCreate_externalService_noErrors() {
        List<String> errors = validator.validateCreate(createDto);
        assertTrue(errors.isEmpty());
    }

    @Test
    void validateCreate_internalServiceWithShipper_noErrors() {
        createDto.setServiceType(ShipmentService.INTERNAL);
        createDto.setShipperId(5);
        List<String> errors = validator.validateCreate(createDto);
        assertTrue(errors.isEmpty());
    }

    @Test
    void validateCreate_missingServiceType_reportsError() {
        createDto.setServiceType(null);
        List<String> errors = validator.validateCreate(createDto);
        assertEquals(1, errors.size());
        assertEquals("• Shipment service is required.", errors.get(0));
    }

    @Test
    void validateCreate_orderIdZero_reportsError() {
        createDto.setOrderId(0);
        List<String> errors = validator.validateCreate(createDto);
        assertEquals(1, errors.size());
        assertEquals("• Order is required.", errors.get(0));
    }

    @Test
    void validateCreate_internalWithoutShipper_reportsError() {
        createDto.setServiceType(ShipmentService.INTERNAL);
        createDto.setShipperId(0);
        List<String> errors = validator.validateCreate(createDto);
        assertEquals(1, errors.size());
        assertEquals("• Shipper must be selected for internal.", errors.get(0));
    }

    @Test
    void validateCreate_missingCustomerName_reportsError() {
        createDto.setCustomerName(null);
        List<String> errors1 = validator.validateCreate(createDto);
        assertTrue(errors1.contains("• Customer name is required."));
        createDto.setCustomerName("   ");
        List<String> errors2 = validator.validateCreate(createDto);
        assertTrue(errors2.contains("• Customer name is required."));
    }

    @Test
    void validateCreate_invalidPhone_reportsError() {
        createDto.setCustomerPhone(null);
        assertTrue(validator.validateCreate(createDto).contains("• Valid customer phone is required."));
        createDto.setCustomerPhone("abc");
        assertTrue(validator.validateCreate(createDto).contains("• Valid customer phone is required."));
    }

    @Test
    void validateCreate_invalidEmail_reportsError() {
        createDto.setCustomerEmail("bad-email");
        List<String> errors = validator.validateCreate(createDto);
        assertTrue(errors.contains("• Valid customer email is required."));
    }

    @Test
    void validateCreate_blankOrNullEmail_noError() {
        createDto.setCustomerEmail(null);
        assertFalse(validator.validateCreate(createDto).contains("• Valid customer email is required."));
        createDto.setCustomerEmail("   ");
        assertFalse(validator.validateCreate(createDto).contains("• Valid customer email is required."));
    }

    @Test
    void validateCreate_missingAddress_reportsError() {
        createDto.setCustomerAddress(null);
        assertTrue(validator.validateCreate(createDto).contains("• Customer address is required."));
        createDto.setCustomerAddress("  ");
        assertTrue(validator.validateCreate(createDto).contains("• Customer address is required."));
    }

    @Test
    void validateCreate_multipleErrors_reportsAll() {
        createDto.setServiceType(null);
        createDto.setOrderId(0);
        createDto.setCustomerName("");
        createDto.setCustomerPhone("bad");
        createDto.setCustomerEmail("bad-email");
        createDto.setCustomerAddress("");
        List<String> errors = validator.validateCreate(createDto);
        assertEquals(6, errors.size());
        assertEquals("• Shipment service is required.", errors.get(0));
        assertEquals("• Order is required.", errors.get(1));
        assertEquals("• Customer name is required.", errors.get(2));
        assertEquals("• Valid customer phone is required.", errors.get(3));
        assertEquals("• Valid customer email is required.", errors.get(4));
        assertEquals("• Customer address is required.", errors.get(5));
    }

    @Test
    void validateUpdate_valid_noErrors() {
        UpdateShipmentDto u = new UpdateShipmentDto();
        u.setServiceType(ShipmentService.INTERNAL);
        u.setOrderId(20);
        u.setShipperId(7);
        u.setCustomerName("Jane");
        u.setCustomerPhone("+1987654321");
        u.setCustomerEmail("jane@example.com");
        u.setCustomerAddress("456 Oak Ave");
        u.setId(55);
        u.setStatus(ShipmentStatus.SUCCESS);
        List<String> errors = validator.validateUpdate(u);
        assertTrue(errors.isEmpty());
    }

    @Test
    void validateUpdate_missingId_reportsError() {
        UpdateShipmentDto u = new UpdateShipmentDto();
        u.setServiceType(ShipmentService.DIDI);
        u.setOrderId(30);
        u.setCustomerName("X");
        u.setCustomerPhone("+1111111111");
        u.setCustomerEmail("x@example.com");
        u.setCustomerAddress("789 Pine Rd");
        u.setStatus(ShipmentStatus.SHIPPING);
        List<String> errors = validator.validateUpdate(u);
        assertEquals(1, errors.size());
        assertEquals("• Shipment ID is required.", errors.get(0));
    }

    @Test
    void validateUpdate_propagatesCreateAndUpdateErrors_reportsAll() {
        UpdateShipmentDto u = new UpdateShipmentDto();
        u.setServiceType(null);
        u.setOrderId(0);
        u.setShipperId(0);
        u.setCustomerName("");
        u.setCustomerPhone("bad");
        u.setCustomerEmail("bad-email");
        u.setCustomerAddress("");
        u.setId(0);
        u.setStatus(null);
        List<String> errors = validator.validateUpdate(u);
        assertTrue(errors.contains("• Shipment service is required."));
        assertTrue(errors.contains("• Order is required."));
        assertFalse(errors.contains("• Shipper must be selected for internal."));
        assertTrue(errors.contains("• Customer name is required."));
        assertTrue(errors.contains("• Valid customer phone is required."));
        assertTrue(errors.contains("• Valid customer email is required."));
        assertTrue(errors.contains("• Customer address is required."));
        assertTrue(errors.contains("• Shipment ID is required."));
        assertTrue(errors.contains("• Shipment status is required."));
    }
}
