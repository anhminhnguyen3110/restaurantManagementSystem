package com.restaurant.constants;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RestaurantStatusTest {

    @ParameterizedTest
    @CsvSource({
            "inactive, INACTIVE",
            "INACTIVE, INACTIVE",
            "InAcTiVe, INACTIVE",
            "active, ACTIVE",
            "ACTIVE, ACTIVE",
            "AcTiVe, ACTIVE",
            "maintenance, MAINTENANCE",
            "MAINTENANCE, MAINTENANCE",
            "MaInTeNaNcE, MAINTENANCE"
    })
    void fromString_shouldReturnCorrectEnum(String input, String expectedName) {
        assertEquals(RestaurantStatus.valueOf(expectedName), RestaurantStatus.fromString(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "inact", " inactive", "active ", "maintain", "unknown"})
    void fromString_shouldThrowIllegalArgumentException_forInvalidInput(String input) {
        assertThrows(IllegalArgumentException.class, () -> RestaurantStatus.fromString(input));
    }

    @ParameterizedTest
    @CsvSource({
            "INACTIVE, inactive",
            "ACTIVE, active",
            "MAINTENANCE, maintenance"
    })
    void getStatus_shouldReturnFormattedString(String name, String expected) {
        assertEquals(expected, RestaurantStatus.valueOf(name).getStatus());
    }

    @ParameterizedTest
    @CsvSource({
            "INACTIVE, inactive",
            "ACTIVE, active",
            "MAINTENANCE, maintenance"
    })
    void toString_shouldReturnSameAsGetStatus(String name, String expected) {
        assertEquals(expected, RestaurantStatus.valueOf(name).toString());
    }

    @Test
    void values_lengthShouldBe3() {
        assertEquals(3, RestaurantStatus.values().length);
    }
}
