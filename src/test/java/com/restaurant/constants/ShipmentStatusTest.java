package com.restaurant.constants;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ShipmentStatusTest {

    @ParameterizedTest
    @CsvSource({
            "shipping, SHIPPING",
            "SHIPPING, SHIPPING",
            "ShIpPiNg, SHIPPING",
            "success, SUCCESS",
            "SUCCESS, SUCCESS",
            "SuCcEsS, SUCCESS",
            "failed, FAILED",
            "FAILED, FAILED",
            "FaIlEd, FAILED"
    })
    void fromString_shouldReturnCorrectEnum(String input, String expectedName) {
        assertEquals(ShipmentStatus.valueOf(expectedName), ShipmentStatus.fromString(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "shippin", " shipping", "success ", "fail", "unknown"})
    void fromString_shouldThrowIllegalArgumentException_forInvalidInput(String input) {
        assertThrows(IllegalArgumentException.class, () -> ShipmentStatus.fromString(input));
    }

    @ParameterizedTest
    @CsvSource({
            "SHIPPING, shipping",
            "SUCCESS, success",
            "FAILED, failed"
    })
    void getStatus_shouldReturnFormattedString(String name, String expected) {
        assertEquals(expected, ShipmentStatus.valueOf(name).getStatus());
    }

    @ParameterizedTest
    @CsvSource({
            "SHIPPING, shipping",
            "SUCCESS, success",
            "FAILED, failed"
    })
    void toString_shouldReturnSameAsGetStatus(String name, String expected) {
        assertEquals(expected, ShipmentStatus.valueOf(name).toString());
    }

    @Test
    void values_lengthShouldBe3() {
        assertEquals(3, ShipmentStatus.values().length);
    }
}
