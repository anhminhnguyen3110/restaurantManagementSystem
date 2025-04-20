package com.restaurant.order;

import com.restaurant.constants.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderStatusTest {

    @Test
    @DisplayName("getStatus() and toString() return lowercase name")
    void getStatusAndToString() {
        for (OrderStatus os : OrderStatus.values()) {
            String expected = os.name().toLowerCase();
            assertEquals(expected, os.getStatus().toLowerCase());
            assertEquals(expected, os.toString().toLowerCase());
        }
    }

    @Test
    @DisplayName("fromString() returns correct enum ignoring case")
    void fromString_valid() {
        for (OrderStatus os : OrderStatus.values()) {
            String raw = os.getStatus();
            assertSame(os, OrderStatus.fromString(raw));
            assertSame(os, OrderStatus.fromString(raw.toUpperCase()));
        }
    }

    @Test
    @DisplayName("fromString() throws on unknown status")
    void fromString_invalid() {
        String bad = "not_a_status";
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> OrderStatus.fromString(bad)
        );
        assertEquals("No constant with status " + bad + " found", ex.getMessage());
    }
}