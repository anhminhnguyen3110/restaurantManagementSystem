package com.restaurant.utils.validators;

import com.restaurant.constants.ShipmentService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShipmentInputValidatorTest {

    @Test
    void validate_allValidPickup_noErrors() {
        List<String> errors = ShipmentInputValidator.validate(
                ShipmentService.GRAB,
                0,
                "Alice",
                "+123-456-7890",
                "alice@example.com",
                "123 Main St"
        );
        assertTrue(errors.isEmpty());
    }

    @Test
    void validate_nullService_error() {
        List<String> errors = ShipmentInputValidator.validate(
                null,
                5,
                "Bob",
                "1234567",
                "",
                "Some Address"
        );
        assertEquals(1, errors.size());
        assertEquals("• Shipment service is required.", errors.get(0));
    }

    @Test
    void validate_internalNoShipper_error() {
        List<String> errors = ShipmentInputValidator.validate(
                ShipmentService.INTERNAL,
                0,
                "Carol",
                "123 456 7890",
                "",
                "Address"
        );
        assertTrue(errors.contains("• Shipper must be selected for internal."));
        assertEquals(1, errors.size());
    }

    @Test
    void validate_blankCustomerName_error() {
        List<String> errors = ShipmentInputValidator.validate(
                ShipmentService.DIDI,
                0,
                "   ",
                "+12345678",
                "",
                "Addr"
        );
        assertEquals(1, errors.size());
        assertEquals("• Customer name is required.", errors.get(0));
    }

    @Test
    void validate_invalidPhone_error() {
        List<String> errors = ShipmentInputValidator.validate(
                ShipmentService.GRAB,
                0,
                "Dave",
                "abc123",
                "",
                "Addr"
        );
        assertEquals(1, errors.size());
        assertEquals("• Valid customer phone is required.", errors.get(0));
    }

    @Test
    void validate_blankEmail_noEmailError() {
        List<String> errors = ShipmentInputValidator.validate(
                ShipmentService.GRAB,
                0,
                "Eve",
                "+1234567890",
                "   ",
                "Addr"
        );
        assertFalse(errors.contains("• Valid customer email is required."));
    }

    @Test
    void validate_invalidEmail_error() {
        List<String> errors = ShipmentInputValidator.validate(
                ShipmentService.GRAB,
                0,
                "Frank",
                "+1234567890",
                "not-an-email",
                "Addr"
        );
        assertEquals(1, errors.size());
        assertEquals("• Valid customer email is required.", errors.get(0));
    }

    @Test
    void validate_blankAddress_error() {
        List<String> errors = ShipmentInputValidator.validate(
                ShipmentService.GRAB,
                0,
                "Grace",
                "+1234567890",
                "",
                ""
        );
        assertEquals(1, errors.size());
        assertEquals("• Customer address is required.", errors.get(0));
    }

    @Test
    void validate_multipleIssues_allErrors() {
        List<String> errors = ShipmentInputValidator.validate(
                null,
                0,
                "",
                "bad",
                "also-bad-email",
                ""
        );
        assertEquals(5, errors.size());
        assertTrue(errors.contains("• Shipment service is required."));
        assertTrue(errors.contains("• Customer name is required."));
        assertTrue(errors.contains("• Valid customer phone is required."));
        assertTrue(errors.contains("• Valid customer email is required."));
        assertTrue(errors.contains("• Customer address is required."));
    }
}
