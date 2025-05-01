package com.restaurant.models;

import com.restaurant.constants.BookingStatus;
import com.restaurant.constants.BookingTimeSlot;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {

    @Test
    void defaultConstructor_setsStatusToBooked() {
        Booking b = new Booking();
        assertEquals(BookingStatus.BOOKED, b.getStatus());
        assertNull(b.getDate());
        assertNull(b.getStartTime());
        assertNull(b.getEndTime());
        assertNull(b.getTable());
        assertNull(b.getCustomer());
    }

    @Test
    void allArgsConstructor_setsFields_andLeavesCustomerNull_andStatusBooked() {
        LocalDate date = LocalDate.of(2025, 5, 1);
        BookingTimeSlot start = BookingTimeSlot.SLOT_10_00;
        BookingTimeSlot end = BookingTimeSlot.SLOT_11_00;
        RestaurantTable tbl = new RestaurantTable();
        tbl.setId(42);
        Booking b = new Booking(date, start, end, tbl);
        assertEquals(date, b.getDate());
        assertEquals(start, b.getStartTime());
        assertEquals(end, b.getEndTime());
        assertSame(tbl, b.getTable());
        assertNull(b.getCustomer());
        assertEquals(BookingStatus.BOOKED, b.getStatus());
    }

    @Test
    void settersAndGetters_workAsExpected() {
        Booking b = new Booking();
        LocalDate date = LocalDate.now();
        b.setDate(date);
        assertEquals(date, b.getDate());

        b.setStartTime(BookingTimeSlot.SLOT_12_30);
        assertEquals(BookingTimeSlot.SLOT_12_30, b.getStartTime());

        b.setEndTime(BookingTimeSlot.SLOT_13_30);
        assertEquals(BookingTimeSlot.SLOT_13_30, b.getEndTime());

        RestaurantTable tbl = new RestaurantTable();
        tbl.setId(7);
        b.setTable(tbl);
        assertSame(tbl, b.getTable());

        Customer cust = new Customer();
        cust.setId(99);
        b.setCustomer(cust);
        assertSame(cust, b.getCustomer());

        b.setStatus(BookingStatus.CANCELLED);
        assertEquals(BookingStatus.CANCELLED, b.getStatus());
    }
}
