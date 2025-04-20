package com.restaurant.payment;

import com.restaurant.constants.PaymentMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentMethodTest {

    @Test
    @DisplayName("getMethod() and toString() return the same text")
    void getMethodAndToString() {
        for (PaymentMethod m : PaymentMethod.values()) {
            assertEquals(m.getMethod(), m.toString(),
                    "toString must match getMethod for " + m.name());
        }
    }

    @Test
    @DisplayName("fromString() returns correct enum ignoring case")
    void fromStringValid() {
        for (PaymentMethod m : PaymentMethod.values()) {
            String raw = m.getMethod();
            assertSame(m, PaymentMethod.fromString(raw));
            assertSame(m, PaymentMethod.fromString(raw.toLowerCase()));
            assertSame(m, PaymentMethod.fromString(raw.toUpperCase()));
        }
    }

    @Test
    @DisplayName("fromString() throws on unknown text")
    void fromStringInvalid() {
        String bad = "Not_A_Method";
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> PaymentMethod.fromString(bad)
        );
        assertEquals("No constant with text " + bad + " found", ex.getMessage());
    }
}
