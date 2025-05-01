package com.restaurant.constants;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentStatusTest {

    @ParameterizedTest
    @CsvSource({
            "pending, PENDING",
            "PENDING, PENDING",
            "PeNdInG, PENDING",
            "completed, COMPLETED",
            "COMPLETED, COMPLETED",
            "CoMpLeTeD, COMPLETED",
            "cancelled, CANCELLED",
            "CANCELLED, CANCELLED",
            "CaNcElLeD, CANCELLED",
            "failed, FAILED",
            "FAILED, FAILED",
            "FaIlEd, FAILED"
    })
    void fromString_shouldReturnCorrectEnum(String input, String expectedName) {
        PaymentStatus expected = PaymentStatus.valueOf(expectedName);
        assertEquals(expected, PaymentStatus.fromString(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "pend", " pending", "completed ", "canceled", "fail", "unknown"})
    void fromString_shouldThrowIllegalArgumentException_forInvalidInput(String input) {
        assertThrows(IllegalArgumentException.class, () -> PaymentStatus.fromString(input));
    }

    @ParameterizedTest
    @CsvSource({
            "PENDING, pending",
            "COMPLETED, completed",
            "CANCELLED, cancelled",
            "FAILED, failed"
    })
    void getStatus_shouldReturnFormattedString(String name, String expected) {
        PaymentStatus status = PaymentStatus.valueOf(name);
        assertEquals(expected, status.getStatus());
    }

    @ParameterizedTest
    @CsvSource({
            "PENDING, pending",
            "COMPLETED, completed",
            "CANCELLED, cancelled",
            "FAILED, failed"
    })
    void toString_shouldReturnSameAsGetStatus(String name, String expected) {
        PaymentStatus status = PaymentStatus.valueOf(name);
        assertEquals(expected, status.toString());
    }

    @Test
    void values_lengthShouldBe4() {
        assertEquals(4, PaymentStatus.values().length);
    }
}
