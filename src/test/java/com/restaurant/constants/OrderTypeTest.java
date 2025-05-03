package com.restaurant.constants;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OrderTypeTest {

    @ParameterizedTest
    @CsvSource({
            "dine_in, DINE_IN",
            "DINE_IN, DINE_IN",
            "DiNe_In, DINE_IN",
            "delivery, DELIVERY",
            "DELIVERY, DELIVERY",
            "DeLiVeRy, DELIVERY"
    })
    void fromString_shouldReturnCorrectEnum(String input, String expectedName) {
        OrderType expected = OrderType.valueOf(expectedName);
        assertEquals(expected, OrderType.fromString(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"pickup", "", "dine in", " dine_in", "delivery ", "unknown"})
    void fromString_shouldThrowIllegalArgumentException_forInvalidInput(String input) {
        assertThrows(IllegalArgumentException.class, () -> OrderType.fromString(input));
    }

    @ParameterizedTest
    @CsvSource({
            "DINE_IN, dine in",
            "DELIVERY, delivery"
    })
    void getType_shouldReturnFormattedString(String name, String expected) {
        OrderType orderType = OrderType.valueOf(name);
        assertEquals(expected, orderType.getType());
    }

    @ParameterizedTest
    @CsvSource({
            "DINE_IN, dine in",
            "DELIVERY, delivery"
    })
    void toString_shouldReturnSameAsGetType(String name, String expected) {
        OrderType orderType = OrderType.valueOf(name);
        assertEquals(expected, orderType.toString());
    }

    @Test
    void values_lengthShouldBe2() {
        assertEquals(2, OrderType.values().length);
    }
}
