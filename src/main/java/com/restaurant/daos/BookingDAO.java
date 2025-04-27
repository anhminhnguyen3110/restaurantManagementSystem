package com.restaurant.daos;

import com.restaurant.dtos.booking.GetBookingsDto;
import com.restaurant.models.Booking;

import java.util.List;

public interface BookingDAO {
    void add(Booking booking);

    Booking getById(int id);

    List<Booking> find(GetBookingsDto dto);

    void update(Booking booking);

    void delete(int id);
}