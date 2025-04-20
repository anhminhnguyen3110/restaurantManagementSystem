package com.restaurant.booking;

import com.restaurant.constants.BookingDuration;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingDurationTest {

    @Test
    void getMinutes_shouldReturnCorrectValue() {
        assertEquals(30, BookingDuration.HALF_HOUR.getMinutes());
        assertEquals(60, BookingDuration.ONE_HOUR.getMinutes());
        assertEquals(90, BookingDuration.ONE_AND_HALF_HOUR.getMinutes());
        assertEquals(120, BookingDuration.TWO_HOURS.getMinutes());
        assertEquals(150, BookingDuration.TWO_AND_HALF_HOUR.getMinutes());
        assertEquals(180, BookingDuration.THREE_HOURS.getMinutes());
        assertEquals(210, BookingDuration.THREE_AND_HALF_HOUR.getMinutes());
    }

    @Test
    void toDuration_shouldReturnCorrectDuration() {
        assertEquals(Duration.ofMinutes(30), BookingDuration.HALF_HOUR.toDuration());
        assertEquals(Duration.ofMinutes(60), BookingDuration.ONE_HOUR.toDuration());
        assertEquals(Duration.ofMinutes(90), BookingDuration.ONE_AND_HALF_HOUR.toDuration());
        assertEquals(Duration.ofMinutes(120), BookingDuration.TWO_HOURS.toDuration());
        assertEquals(Duration.ofMinutes(150), BookingDuration.TWO_AND_HALF_HOUR.toDuration());
        assertEquals(Duration.ofMinutes(180), BookingDuration.THREE_HOURS.toDuration());
        assertEquals(Duration.ofMinutes(210), BookingDuration.THREE_AND_HALF_HOUR.toDuration());
    }

    @Test
    void addTo_shouldAddCorrectDuration() {
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 12, 0);
        assertEquals(start.plusMinutes(30), BookingDuration.HALF_HOUR.addTo(start));
        assertEquals(start.plusMinutes(60), BookingDuration.ONE_HOUR.addTo(start));
        assertEquals(start.plusMinutes(90), BookingDuration.ONE_AND_HALF_HOUR.addTo(start));
        assertEquals(start.plusMinutes(120), BookingDuration.TWO_HOURS.addTo(start));
        assertEquals(start.plusMinutes(150), BookingDuration.TWO_AND_HALF_HOUR.addTo(start));
        assertEquals(start.plusMinutes(180), BookingDuration.THREE_HOURS.addTo(start));
        assertEquals(start.plusMinutes(210), BookingDuration.THREE_AND_HALF_HOUR.addTo(start));
    }

    @Test
    void fromMinutes_shouldReturnCorrectEnum() {
        assertEquals(BookingDuration.HALF_HOUR, BookingDuration.fromMinutes(30));
        assertEquals(BookingDuration.ONE_HOUR, BookingDuration.fromMinutes(60));
        assertEquals(BookingDuration.ONE_AND_HALF_HOUR, BookingDuration.fromMinutes(90));
        assertEquals(BookingDuration.TWO_HOURS, BookingDuration.fromMinutes(120));
        assertEquals(BookingDuration.TWO_AND_HALF_HOUR, BookingDuration.fromMinutes(150));
        assertEquals(BookingDuration.THREE_HOURS, BookingDuration.fromMinutes(180));
        assertEquals(BookingDuration.THREE_AND_HALF_HOUR, BookingDuration.fromMinutes(210));
    }

    @Test
    void fromMinutes_invalidMinutes_shouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> BookingDuration.fromMinutes(29));
        assertThrows(IllegalArgumentException.class, () -> BookingDuration.fromMinutes(211));
    }
}