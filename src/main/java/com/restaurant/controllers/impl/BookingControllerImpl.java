package com.restaurant.controllers.impl;

import com.restaurant.constants.BookingTimeSlot;
import com.restaurant.controllers.BookingController;
import com.restaurant.daos.BookingDAO;
import com.restaurant.daos.CustomerDAO;
import com.restaurant.daos.RestaurantTableDAO;
import com.restaurant.di.Inject;
import com.restaurant.di.Injectable;
import com.restaurant.dtos.booking.CreateBookingDto;
import com.restaurant.dtos.booking.GetBookingsDto;
import com.restaurant.dtos.booking.UpdateBookingDto;
import com.restaurant.models.Booking;
import com.restaurant.models.Customer;
import com.restaurant.models.RestaurantTable;

import java.time.LocalDate;
import java.util.List;

@Injectable
public class BookingControllerImpl implements BookingController {
    @Inject
    private BookingDAO bookingDAO;
    @Inject
    private CustomerDAO customerDAO;
    @Inject
    private RestaurantTableDAO tableDAO;

    public BookingControllerImpl() {
        // Default constructor for DI
    }

    @Override
    public void createBooking(CreateBookingDto dto) {
        LocalDate today = LocalDate.now();
        if (dto.getDate().isBefore(today)) {
            System.out.println("Booking date must be in the future");
            return;
        }
        BookingTimeSlot start = dto.getStartTime();
        BookingTimeSlot end = dto.getEndTime();
        if (end.ordinal() <= start.ordinal()) {
            System.out.println("End time must be after start time");
            return;
        }
        RestaurantTable table = tableDAO.getById(dto.getTableId());
        GetBookingsDto filter = new GetBookingsDto();
        filter.setTableNumber(table.getNumber());
        filter.setDate(dto.getDate());
        List<Booking> existing = bookingDAO.find(filter);
        for (Booking b : existing) {
            if (b.getStartTime().ordinal() < end.ordinal()
                    && b.getEndTime().ordinal() > start.ordinal()) {
                System.out.println("Overlapping booking detected: id=" + b.getId());
                return;
            }
        }
        Customer customer = customerDAO.getByPhoneNumber(dto.getCustomerPhoneNumber());
        if (customer == null) {
            customer = new Customer();
            customer.setName(dto.getCustomerName());
            customer.setPhoneNumber(dto.getCustomerPhoneNumber());
            customer.setEmail(dto.getCustomerEmail());
            customerDAO.add(customer);
        }
        Booking booking = new Booking(dto.getDate(), start, end, table);
        booking.setCustomer(customer);
        bookingDAO.add(booking);
    }

    @Override
    public Booking getBooking(int id) {
        return bookingDAO.getById(id);
    }

    @Override
    public List<Booking> findBookings(GetBookingsDto dto) {
        return bookingDAO.find(dto);
    }

    @Override
    public void updateBooking(UpdateBookingDto dto) {
        Booking booking = bookingDAO.getById(dto.getId());
        if (booking == null) {
            return;
        }

        LocalDate today = LocalDate.now();
        if (dto.getDate().isBefore(today)) {
            System.out.println("Booking date must be in the future");
            return;
        }
        BookingTimeSlot start = dto.getStartTime();
        BookingTimeSlot end = dto.getEndTime();
        if (end.ordinal() <= start.ordinal()) {
            System.out.println("End time must be after start time");
            return;
        }
        RestaurantTable table = tableDAO.getById(dto.getTableId());
        booking.setDate(dto.getDate());
        booking.setStartTime(start);
        booking.setEndTime(end);
        if (dto.getStatus() != null) {
            booking.setStatus(dto.getStatus());
        }

        if (!(booking.getTable().getId() == table.getId())) {
            booking.setTable(table);
        }

        Customer c = booking.getCustomer();
        boolean changed = false;
        if (dto.getCustomerName() != null && !dto.getCustomerName().equals(c.getName())) {
            c.setName(dto.getCustomerName());
            changed = true;
        }
        if (dto.getCustomerPhoneNumber() != null && !dto.getCustomerPhoneNumber().equals(c.getPhoneNumber())) {
            c.setPhoneNumber(dto.getCustomerPhoneNumber());
            changed = true;
        }
        if (dto.getCustomerEmail() != null && !dto.getCustomerEmail().equals(c.getEmail())) {
            c.setEmail(dto.getCustomerEmail());
            changed = true;
        }
        if (changed) {
            customerDAO.update(c);
        }

        bookingDAO.update(booking);
    }

    @Override
    public void deleteBooking(int id) {
        bookingDAO.delete(id);
    }
}