package com.restaurant.daos;

import com.restaurant.models.Booking;

import java.time.LocalDateTime;
import java.util.List;
import com.restaurant.dtos.booking.*;

public interface BookingDAO {
    void add(Booking booking);

    Booking getById(int id);

    List<Booking> findAll(GetBookingsDto dto);

    void update(Booking booking);

    void delete(int id);
}