package com.restaurant.controllers;

import com.restaurant.dtos.restaurant.CreateRestaurantDto;
import com.restaurant.models.Booking;
import com.restaurant.dtos.booking.*;

import java.util.List;

public interface BookingController {
    void createBooking(CreateRestaurantDto createRestaurantDto);

    Booking getBooking(int id);

    List<Booking> findBookings(GetBookingsDto dto);

    void updateBooking(UpdateBookingDto updateBookingDto);
}