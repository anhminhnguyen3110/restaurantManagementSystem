package com.restaurant.constants;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BookingStatusTest {

    @ParameterizedTest
    @CsvSource({
            "booked, BOOKED",
            "BOOKED, BOOKED",
            "BoOkEd, BOOKED",
            "cancelled, CANCELLED",
            "CANCELLED, CANCELLED",
            "CaNcElLeD, CANCELLED",
            "completed, COMPLETED",
            "COMPLETED, COMPLETED",
            "CoMpLeTeD, COMPLETED"
    })
    void fromString_shouldReturnCorrectEnum(String input, String expectedName) {
        BookingStatus expected = BookingStatus.valueOf(expectedName);
        assertEquals(expected, BookingStatus.fromString(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "book", "booked ", " completed", "done", "unknown"})
    void fromString_shouldThrowIllegalArgumentException_forInvalidInput(String input) {
        assertThrows(IllegalArgumentException.class, () -> BookingStatus.fromString(input));
    }

    @ParameterizedTest
    @CsvSource({
            "BOOKED, booked",
            "CANCELLED, cancelled",
            "COMPLETED, completed"
    })
    void getStatus_shouldReturnFormattedString(String name, String expected) {
        BookingStatus status = BookingStatus.valueOf(name);
        assertEquals(expected, status.getStatus());
    }

    @ParameterizedTest
    @CsvSource({
            "BOOKED, booked",
            "CANCELLED, cancelled",
            "COMPLETED, completed"
    })
    void toString_shouldReturnSameAsGetStatus(String name, String expected) {
        BookingStatus status = BookingStatus.valueOf(name);
        assertEquals(expected, status.toString());
    }

    @Test
    void values_lengthShouldBe3() {
        assertEquals(3, BookingStatus.values().length);
    }
}
