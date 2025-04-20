package com.restaurant.user;

import com.restaurant.constants.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserRoleTest {

    @Test
    @DisplayName("getRole() and toString() return the same human‐readable text")
    void getRoleAndToString() {
        for (UserRole r : UserRole.values()) {
            assertEquals(r.getRole(), r.toString(),
                    "toString must match getRole for " + r.name());
        }
    }

    @Test
    @DisplayName("fromString() returns correct enum ignoring case")
    void fromStringValid() {
        for (UserRole r : UserRole.values()) {
            String raw = r.getRole();
            assertSame(r, UserRole.fromString(raw));
            assertSame(r, UserRole.fromString(raw.toLowerCase()));
            assertSame(r, UserRole.fromString(raw.toUpperCase()));
        }
    }

    @Test
    @DisplayName("fromString() throws on unknown role")
    void fromStringInvalid() {
        String bad = "NotARole";
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> UserRole.fromString(bad)
        );
        assertEquals("No constant with role " + bad + " found", ex.getMessage());
    }
}