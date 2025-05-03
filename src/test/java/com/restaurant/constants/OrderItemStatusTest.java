package com.restaurant.constants;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OrderItemStatusTest {

    @ParameterizedTest
    @CsvSource({
            "pending, PENDING",
            "PENDING, PENDING",
            "PeNdInG, PENDING",
            "ready, READY",
            "READY, READY",
            "ReAdY, READY",
            "served, SERVED",
            "SERVED, SERVED",
            "SeRvEd, SERVED",
            "cancelled, CANCELLED",
            "CANCELLED, CANCELLED",
            "CaNcElLeD, CANCELLED"
    })
    void fromString_shouldReturnCorrectEnum(String input, String expectedName) {
        OrderItemStatus expected = OrderItemStatus.valueOf(expectedName);
        assertEquals(expected, OrderItemStatus.fromString(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "pend", " pending", "served ", "done", "unknown"})
    void fromString_shouldThrowIllegalArgumentException_forInvalidInput(String input) {
        assertThrows(IllegalArgumentException.class, () -> OrderItemStatus.fromString(input));
    }

    @ParameterizedTest
    @CsvSource({
            "PENDING, pending",
            "READY, ready",
            "SERVED, served",
            "CANCELLED, cancelled"
    })
    void getStatus_shouldReturnFormattedString(String name, String expected) {
        OrderItemStatus status = OrderItemStatus.valueOf(name);
        assertEquals(expected, status.getStatus());
    }

    @ParameterizedTest
    @CsvSource({
            "PENDING, pending",
            "READY, ready",
            "SERVED, served",
            "CANCELLED, cancelled"
    })
    void toString_shouldReturnSameAsGetStatus(String name, String expected) {
        OrderItemStatus status = OrderItemStatus.valueOf(name);
        assertEquals(expected, status.toString());
    }

    @Test
    void values_lengthShouldBe4() {
        assertEquals(4, OrderItemStatus.values().length);
    }
}
