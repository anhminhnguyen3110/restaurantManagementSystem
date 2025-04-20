package com.restaurant.payment;

import com.restaurant.constants.PaymentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentStatusTest {

    @Test
    @DisplayName("getStatus() and toString() return the same text")
    void getStatusAndToString() {
        for (PaymentStatus s : PaymentStatus.values()) {
            assertEquals(s.getStatus(), s.toString(),
                    "toString must match getStatus for " + s.name());
        }
    }

    @Test
    @DisplayName("fromString() returns correct enum ignoring case")
    void fromStringValid() {
        for (PaymentStatus s : PaymentStatus.values()) {
            String raw = s.getStatus();
            assertSame(s, PaymentStatus.fromString(raw));
            assertSame(s, PaymentStatus.fromString(raw.toLowerCase()));
            assertSame(s, PaymentStatus.fromString(raw.toUpperCase()));
        }
    }

    @Test
    @DisplayName("fromString() throws on unknown text")
    void fromStringInvalid() {
        String bad = "not_a_status";
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> PaymentStatus.fromString(bad)
        );
        assertEquals("No constant with text " + bad + " found", ex.getMessage());
    }
}
