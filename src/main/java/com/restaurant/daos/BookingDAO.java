package com.restaurant.daos;

import com.restaurant.models.Booking;

import java.time.LocalDateTime;
import java.util.List;


public interface BookingDAO {
    void addBooking(Booking booking);

    Booking getBookingByPhone(String phone);

    Booking getBookingById(int id);

    List<Booking> getAllBookings();

    List<Booking> getBookingsInRange(LocalDateTime from, LocalDateTime to);

    void updateBooking(Booking booking);

    void deleteBooking(int id);
}
