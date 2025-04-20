package com.restaurant.shipment;

import com.restaurant.constants.ShipmentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShipmentServiceTest {

    @Test
    @DisplayName("getService() and toString() return the same text")
    void getServiceAndToString() {
        for (ShipmentService s : ShipmentService.values()) {
            assertEquals(s.getService(), s.toString(),
                    "toString must match getService for " + s.name());
        }
    }

    @Test
    @DisplayName("fromString() returns correct enum ignoring case")
    void fromStringValid() {
        for (ShipmentService s : ShipmentService.values()) {
            String raw = s.getService();
            assertSame(s, ShipmentService.fromString(raw));
            assertSame(s, ShipmentService.fromString(raw.toLowerCase()));
            assertSame(s, ShipmentService.fromString(raw.toUpperCase()));
        }
    }

    @Test
    @DisplayName("fromString() throws on unknown text")
    void fromStringInvalid() {
        String bad = "UnknownSvc";
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> ShipmentService.fromString(bad)
        );
        assertEquals("No constant with text " + bad + " found", ex.getMessage());
    }
}