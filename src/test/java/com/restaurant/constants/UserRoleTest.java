package com.restaurant.constants;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class UserRoleTest {

    @ParameterizedTest
    @CsvSource({
            "wait_staff, WAIT_STAFF",
            "WAIT_STAFF, WAIT_STAFF",
            "WaIt_StAfF, WAIT_STAFF",
            "manager, MANAGER",
            "MANAGER, MANAGER",
            "MaNaGeR, MANAGER",
            "owner, OWNER",
            "OWNER, OWNER",
            "OwNeR, OWNER",
            "cook, COOK",
            "COOK, COOK",
            "CoOk, COOK",
            "shipper, SHIPPER",
            "SHIPPER, SHIPPER",
            "ShIpPeR, SHIPPER"
    })
    void fromString_shouldReturnCorrectEnum(String input, String expectedName) {
        assertEquals(UserRole.valueOf(expectedName), UserRole.fromString(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "wait staff", " wait_staff", "shipper ", "driver", "unknown"})
    void fromString_shouldThrowIllegalArgumentException_forInvalidInput(String input) {
        assertThrows(IllegalArgumentException.class, () -> UserRole.fromString(input));
    }

    @ParameterizedTest
    @CsvSource({
            "WAIT_STAFF, wait staff",
            "MANAGER, manager",
            "OWNER, owner",
            "COOK, cook",
            "SHIPPER, shipper"
    })
    void getRole_shouldReturnFormattedString(String name, String expected) {
        assertEquals(expected, UserRole.valueOf(name).getRole());
    }

    @ParameterizedTest
    @CsvSource({
            "WAIT_STAFF, wait staff",
            "MANAGER, manager",
            "OWNER, owner",
            "COOK, cook",
            "SHIPPER, shipper"
    })
    void toString_shouldReturnSameAsGetRole(String name, String expected) {
        assertEquals(expected, UserRole.valueOf(name).toString());
    }

    @Test
    void values_lengthShouldBe5() {
        assertEquals(5, UserRole.values().length);
    }
}
