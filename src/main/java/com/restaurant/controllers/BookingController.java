package com.restaurant.controllers;

import com.restaurant.dtos.booking.CreateBookingDto;
import com.restaurant.dtos.booking.GetBookingsDto;
import com.restaurant.dtos.booking.UpdateBookingDto;
import com.restaurant.models.Booking;

import java.util.List;

public interface BookingController {
    void createBooking(CreateBookingDto createBookingDto);

    Booking getBooking(int id);

    List<Booking> findBookings(GetBookingsDto dto);

    void updateBooking(UpdateBookingDto updateBookingDto);

    void deleteBooking(int id);
}