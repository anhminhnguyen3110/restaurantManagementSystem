package com.restaurant.utils.validators;

import com.restaurant.models.MenuItem;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemInputValidatorTest {

    @Test
    void bothNullMenuItemAndZeroQuantity_reportBothErrors() {
        List<String> errors = OrderItemInputValidator.validate(null, 0);
        assertEquals(2, errors.size());
        assertTrue(errors.contains("• Menu item must be selected."));
        assertTrue(errors.contains("• Quantity must be at least 1."));
    }

    @Test
    void nullMenuItem_positiveQuantity_reportsMenuItemErrorOnly() {
        List<String> errors = OrderItemInputValidator.validate(null, 5);
        assertEquals(1, errors.size());
        assertEquals("• Menu item must be selected.", errors.get(0));
    }

    @Test
    void nonNullMenuItem_zeroQuantity_reportsQuantityErrorOnly() {
        MenuItem item = new MenuItem();
        List<String> errors = OrderItemInputValidator.validate(item, 0);
        assertEquals(1, errors.size());
        assertEquals("• Quantity must be at least 1.", errors.get(0));
    }

    @Test
    void nonNullMenuItem_positiveQuantity_returnsNoErrors() {
        MenuItem item = new MenuItem();
        List<String> errors = OrderItemInputValidator.validate(item, 3);
        assertTrue(errors.isEmpty());
    }
}
