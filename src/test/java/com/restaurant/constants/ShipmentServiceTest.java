package com.restaurant.constants;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ShipmentServiceTest {

    @ParameterizedTest
    @CsvSource({
            "grab, GRAB",
            "GRAB, GRAB",
            "GrAb, GRAB",
            "didi, DIDI",
            "DIDI, DIDI",
            "DiDi, DIDI",
            "internal, INTERNAL",
            "INTERNAL, INTERNAL",
            "InTeRnAl, INTERNAL"
    })
    void fromString_shouldReturnCorrectEnum(String input, String expectedName) {
        assertEquals(ShipmentService.valueOf(expectedName), ShipmentService.fromString(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "graab", " grab", "didi ", "unknown"})
    void fromString_shouldThrowIllegalArgumentException_forInvalidInput(String input) {
        assertThrows(IllegalArgumentException.class, () -> ShipmentService.fromString(input));
    }

    @ParameterizedTest
    @CsvSource({
            "GRAB, grab",
            "DIDI, didi",
            "INTERNAL, internal"
    })
    void getService_shouldReturnFormattedString(String name, String expected) {
        assertEquals(expected, ShipmentService.valueOf(name).getService());
    }

    @ParameterizedTest
    @CsvSource({
            "GRAB, grab",
            "DIDI, didi",
            "INTERNAL, internal"
    })
    void toString_shouldReturnSameAsGetService(String name, String expected) {
        assertEquals(expected, ShipmentService.valueOf(name).toString());
    }

    @Test
    void values_lengthShouldBe3() {
        assertEquals(3, ShipmentService.values().length);
    }
}
