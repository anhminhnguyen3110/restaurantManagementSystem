package com.restaurant.orderItem;

import com.restaurant.constants.OrderItemStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemStatusTest {

    @Test
    @DisplayName("getStatus() and toString() return the same human‑readable text")
    void getStatusAndToString() {
        for (OrderItemStatus s : OrderItemStatus.values()) {
            assertEquals(s.getStatus(), s.toString(),
                    "toString must match getStatus for " + s.name());
        }
    }

    @Test
    @DisplayName("fromString() returns correct enum ignoring case")
    void fromString_valid() {
        for (OrderItemStatus s : OrderItemStatus.values()) {
            String raw = s.getStatus();
            assertSame(s, OrderItemStatus.fromString(raw));
            assertSame(s, OrderItemStatus.fromString(raw.toUpperCase()));
        }
    }

    @Test
    @DisplayName("fromString() throws on unknown text")
    void fromString_invalid() {
        String bad = "not a real status";
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> OrderItemStatus.fromString(bad)
        );
        assertEquals("No constant with text " + bad + " found", ex.getMessage());
    }
}