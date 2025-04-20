package com.restaurant.booking;

import com.restaurant.constants.BookingStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookingStatusTest {

    @Test
    void getStatus_shouldReturnCorrectString() {
        assertEquals("Booked", BookingStatus.BOOKED.getStatus());
        assertEquals("Cancelled", BookingStatus.CANCELLED.getStatus());
        assertEquals("Completed", BookingStatus.COMPLETED.getStatus());
    }

    @Test
    void toString_shouldReturnStatusString() {
        assertEquals("Booked", BookingStatus.BOOKED.toString());
        assertEquals("Cancelled", BookingStatus.CANCELLED.toString());
        assertEquals("Completed", BookingStatus.COMPLETED.toString());
    }
}