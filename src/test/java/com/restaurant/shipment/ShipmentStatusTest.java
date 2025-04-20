package com.restaurant.shipment;

import com.restaurant.constants.ShipmentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShipmentStatusTest {

    @Test
    @DisplayName("getStatus() and toString() return the same text")
    void getStatusAndToString() {
        for (ShipmentStatus s : ShipmentStatus.values()) {
            assertEquals(s.getStatus(), s.toString(),
                    "toString must match getStatus for " + s.name());
        }
    }

    @Test
    @DisplayName("fromString() returns correct enum ignoring case")
    void fromStringValid() {
        for (ShipmentStatus s : ShipmentStatus.values()) {
            String raw = s.getStatus();
            assertSame(s, ShipmentStatus.fromString(raw));
            assertSame(s, ShipmentStatus.fromString(raw.toUpperCase()));
            assertSame(s, ShipmentStatus.fromString(raw.toLowerCase()));
        }
    }

    @Test
    @DisplayName("fromString() throws on unknown text")
    void fromStringInvalid() {
        String bad = "no_such_status";
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> ShipmentStatus.fromString(bad)
        );
        assertEquals("No constant with text " + bad + " found", ex.getMessage());
    }
}