package com.restaurant.controllers.impl;

import com.restaurant.controllers.BookingController;
import com.restaurant.daos.BookingDAO;
import com.restaurant.di.Inject;
import com.restaurant.di.Injectable;
import com.restaurant.models.Booking;

import java.time.LocalDateTime;
import java.util.List;
import com.restaurant.dtos.booking.*;

@Injectable
public class BookingControllerImpl implements BookingController {
    @Inject
    private final BookingDAO bookingDAO;

    public BookingControllerImpl(BookingDAO bookingDAO) {
        this.bookingDAO = bookingDAO;
    }

    @Override
    public void createBooking(Booking booking) {
        bookingDAO.add(booking);
    }

    @Override
    public Booking getBooking(int id) {
        return bookingDAO.getById(id);
    }

    @Override
    public List<Booking> findBookings(GetBookingsDto dto) {
        return bookingDAO.findAll(dto);
    }

    @Override
    public void updateBooking(Booking booking) {
        bookingDAO.update(booking);
    }

    @Override
    public void deleteBooking(int id) {
        bookingDAO.delete(id);
    }
}
