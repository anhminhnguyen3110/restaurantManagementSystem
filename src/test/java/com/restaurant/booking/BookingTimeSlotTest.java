package com.restaurant.booking;

import com.restaurant.constants.BookingTimeSlot;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

class BookingTimeSlotTest {

    @Test
    void getMinutes_shouldReturnCorrectValue() {
        assertEquals(30, BookingTimeSlot.HALF_HOUR.getMinutes());
        assertEquals(60, BookingTimeSlot.ONE_HOUR.getMinutes());
        assertEquals(90, BookingTimeSlot.ONE_AND_HALF_HOUR.getMinutes());
        assertEquals(120, BookingTimeSlot.TWO_HOURS.getMinutes());
        assertEquals(150, BookingTimeSlot.TWO_AND_HALF_HOUR.getMinutes());
        assertEquals(180, BookingTimeSlot.THREE_HOURS.getMinutes());
        assertEquals(210, BookingTimeSlot.THREE_AND_HALF_HOUR.getMinutes());
    }

    @Test
    void toDuration_shouldReturnCorrectDuration() {
        assertEquals(Duration.ofMinutes(30), BookingTimeSlot.HALF_HOUR.toDuration());
        assertEquals(Duration.ofMinutes(60), BookingTimeSlot.ONE_HOUR.toDuration());
        assertEquals(Duration.ofMinutes(90), BookingTimeSlot.ONE_AND_HALF_HOUR.toDuration());
        assertEquals(Duration.ofMinutes(120), BookingTimeSlot.TWO_HOURS.toDuration());
        assertEquals(Duration.ofMinutes(150), BookingTimeSlot.TWO_AND_HALF_HOUR.toDuration());
        assertEquals(Duration.ofMinutes(180), BookingTimeSlot.THREE_HOURS.toDuration());
        assertEquals(Duration.ofMinutes(210), BookingTimeSlot.THREE_AND_HALF_HOUR.toDuration());
    }

    @Test
    void addTo_shouldAddCorrectDuration() {
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 12, 0);
        assertEquals(start.plusMinutes(30), BookingTimeSlot.HALF_HOUR.addTo(start));
        assertEquals(start.plusMinutes(60), BookingTimeSlot.ONE_HOUR.addTo(start));
        assertEquals(start.plusMinutes(90), BookingTimeSlot.ONE_AND_HALF_HOUR.addTo(start));
        assertEquals(start.plusMinutes(120), BookingTimeSlot.TWO_HOURS.addTo(start));
        assertEquals(start.plusMinutes(150), BookingTimeSlot.TWO_AND_HALF_HOUR.addTo(start));
        assertEquals(start.plusMinutes(180), BookingTimeSlot.THREE_HOURS.addTo(start));
        assertEquals(start.plusMinutes(210), BookingTimeSlot.THREE_AND_HALF_HOUR.addTo(start));
    }

    @Test
    void fromMinutes_shouldReturnCorrectEnum() {
        assertEquals(BookingTimeSlot.HALF_HOUR, BookingTimeSlot.fromMinutes(30));
        assertEquals(BookingTimeSlot.ONE_HOUR, BookingTimeSlot.fromMinutes(60));
        assertEquals(BookingTimeSlot.ONE_AND_HALF_HOUR, BookingTimeSlot.fromMinutes(90));
        assertEquals(BookingTimeSlot.TWO_HOURS, BookingTimeSlot.fromMinutes(120));
        assertEquals(BookingTimeSlot.TWO_AND_HALF_HOUR, BookingTimeSlot.fromMinutes(150));
        assertEquals(BookingTimeSlot.THREE_HOURS, BookingTimeSlot.fromMinutes(180));
        assertEquals(BookingTimeSlot.THREE_AND_HALF_HOUR, BookingTimeSlot.fromMinutes(210));
    }

    @Test
    void fromMinutes_invalidMinutes_shouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> BookingTimeSlot.fromMinutes(29));
        assertThrows(IllegalArgumentException.class, () -> BookingTimeSlot.fromMinutes(211));
    }
}