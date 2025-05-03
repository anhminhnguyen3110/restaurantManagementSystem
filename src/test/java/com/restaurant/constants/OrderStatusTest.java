package com.restaurant.constants;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OrderStatusTest {

    @ParameterizedTest
    @CsvSource({
            "pending, PENDING",
            "PENDING, PENDING",
            "PeNdInG, PENDING",
            "ready, READY",
            "READY, READY",
            "ReAdY, READY",
            "processed, PROCESSED",
            "PROCESSED, PROCESSED",
            "PrOcEsSeD, PROCESSED",
            "completed, COMPLETED",
            "COMPLETED, COMPLETED",
            "CoMpLeTeD, COMPLETED",
            "cancelled, CANCELLED",
            "CANCELLED, CANCELLED",
            "CaNcElLeD, CANCELLED"
    })
    void fromString_shouldReturnCorrectEnum(String input, String expectedName) {
        OrderStatus expected = OrderStatus.valueOf(expectedName);
        assertEquals(expected, OrderStatus.fromString(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "pend", " pending", "processed ", "done", "unknown"})
    void fromString_shouldThrowIllegalArgumentException_forInvalidInput(String input) {
        assertThrows(IllegalArgumentException.class, () -> OrderStatus.fromString(input));
    }

    @ParameterizedTest
    @CsvSource({
            "PENDING, pending",
            "READY, ready",
            "PROCESSED, processed",
            "COMPLETED, completed",
            "CANCELLED, cancelled"
    })
    void getStatus_shouldReturnFormattedString(String name, String expected) {
        OrderStatus status = OrderStatus.valueOf(name);
        assertEquals(expected, status.getStatus());
    }

    @ParameterizedTest
    @CsvSource({
            "PENDING, pending",
            "READY, ready",
            "PROCESSED, processed",
            "COMPLETED, completed",
            "CANCELLED, cancelled"
    })
    void toString_shouldReturnSameAsGetStatus(String name, String expected) {
        OrderStatus status = OrderStatus.valueOf(name);
        assertEquals(expected, status.toString());
    }

    @Test
    void values_lengthShouldBe5() {
        assertEquals(5, OrderStatus.values().length);
    }
}
