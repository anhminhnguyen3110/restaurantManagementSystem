package com.restaurant.daos;

import com.restaurant.models.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingDAO {
    void add(Booking booking);

    Booking getById(int id);

    List<Booking> findAll();

    List<Booking> findByCustomerPhone(String phone);

    List<Booking> findInRange(LocalDateTime from, LocalDateTime to);

    void update(Booking booking);

    void delete(int id);
}