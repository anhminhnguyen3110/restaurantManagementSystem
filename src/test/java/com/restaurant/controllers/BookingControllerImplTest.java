package com.restaurant.controllers;

import com.restaurant.constants.BookingTimeSlot;
import com.restaurant.controllers.impl.BookingControllerImpl;
import com.restaurant.daos.BookingDAO;
import com.restaurant.daos.CustomerDAO;
import com.restaurant.daos.RestaurantTableDAO;
import com.restaurant.dtos.booking.CreateBookingDto;
import com.restaurant.dtos.booking.GetBookingsDto;
import com.restaurant.dtos.booking.UpdateBookingDto;
import com.restaurant.models.Booking;
import com.restaurant.models.Customer;
import com.restaurant.models.RestaurantTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerImplTest {
    @Mock
    BookingDAO bookingDAO;
    @Mock
    CustomerDAO customerDAO;
    @Mock
    RestaurantTableDAO tableDAO;
    @InjectMocks
    BookingControllerImpl controller;

    private LocalDate today;
    private CreateBookingDto createDto;
    private UpdateBookingDto updateDto;
    private GetBookingsDto filterDto;

    @BeforeEach
    void setup() {
        today = LocalDate.now();
        createDto = new CreateBookingDto();
        updateDto = new UpdateBookingDto();
        filterDto = new GetBookingsDto();
    }

    @Test
    void createBooking_pastDate_noCalls() {
        createDto.setDate(today.minusDays(1));
        createDto.setStartTime(BookingTimeSlot.SLOT_10_00);
        createDto.setEndTime(BookingTimeSlot.SLOT_11_00);
        controller.createBooking(createDto);
        verifyNoInteractions(tableDAO, bookingDAO, customerDAO);
    }

    @Test
    void createBooking_endBeforeOrEqualStart_noCalls() {
        createDto.setDate(today.plusDays(1));
        createDto.setStartTime(BookingTimeSlot.SLOT_11_00);
        createDto.setEndTime(BookingTimeSlot.SLOT_10_00);
        controller.createBooking(createDto);
        verifyNoInteractions(tableDAO, bookingDAO, customerDAO);
    }

    @Test
    void createBooking_overlappingBooking_noCalls() {
        createDto.setDate(today.plusDays(1));
        createDto.setStartTime(BookingTimeSlot.SLOT_10_00);
        createDto.setEndTime(BookingTimeSlot.SLOT_11_00);
        createDto.setTableId(5);
        RestaurantTable table = new RestaurantTable();
        table.setId(5);
        table.setNumber(3);
        when(tableDAO.getById(5)).thenReturn(table);
        GetBookingsDto f = new GetBookingsDto();
        f.setTableNumber(3);
        f.setDate(today.plusDays(1));
        Booking existing = new Booking(today.plusDays(1), BookingTimeSlot.SLOT_10_30, BookingTimeSlot.SLOT_11_30, table);
        existing.setId(1);
        when(bookingDAO.find(any())).thenReturn(List.of(existing));
        controller.createBooking(createDto);
        verify(tableDAO).getById(5);
        verify(bookingDAO).find(any());
        verifyNoMoreInteractions(bookingDAO, customerDAO);
    }

    @Test
    void createBooking_newCustomer_andBookingAdded() {
        createDto.setDate(today.plusDays(1));
        createDto.setStartTime(BookingTimeSlot.SLOT_10_00);
        createDto.setEndTime(BookingTimeSlot.SLOT_11_00);
        createDto.setTableId(2);
        createDto.setCustomerName("Alice");
        createDto.setCustomerPhoneNumber("123");
        createDto.setCustomerEmail("a@b.com");
        RestaurantTable table = new RestaurantTable();
        table.setId(2);
        table.setNumber(4);
        when(tableDAO.getById(2)).thenReturn(table);
        when(bookingDAO.find(any())).thenReturn(List.of());
        when(customerDAO.getByPhoneNumber("123")).thenReturn(null);
        controller.createBooking(createDto);
        verify(customerDAO).add(any(Customer.class));
        verify(bookingDAO).add(any(Booking.class));
    }

    @Test
    void createBooking_existingCustomer_andBookingAdded() {
        createDto.setDate(today.plusDays(1));
        createDto.setStartTime(BookingTimeSlot.SLOT_12_00);
        createDto.setEndTime(BookingTimeSlot.SLOT_13_00);
        createDto.setTableId(7);
        createDto.setCustomerPhoneNumber("456");
        Customer c = new Customer();
        when(tableDAO.getById(7)).thenReturn(new RestaurantTable());
        when(bookingDAO.find(any())).thenReturn(List.of());
        when(customerDAO.getByPhoneNumber("456")).thenReturn(c);
        controller.createBooking(createDto);
        verify(customerDAO, never()).add(any());
        verify(bookingDAO).add(any());
    }

    @Test
    void getBooking_returnsFromDao() {
        Booking b = new Booking();
        when(bookingDAO.getById(9)).thenReturn(b);
        assertSame(b, controller.getBooking(9));
    }

    @Test
    void findBookings_returnsFromDao() {
        List<Booking> list = List.of(new Booking(), new Booking());
        when(bookingDAO.find(filterDto)).thenReturn(list);
        assertEquals(list, controller.findBookings(filterDto));
    }

    @Test
    void updateBooking_notFound_noCalls() {
        updateDto.setId(10);
        when(bookingDAO.getById(10)).thenReturn(null);
        controller.updateBooking(updateDto);
        verifyNoMoreInteractions(tableDAO, customerDAO, bookingDAO);
    }

    @Test
    void updateBooking_pastDate_noUpdate() {
        Booking b = new Booking();
        b.setId(11);
        when(bookingDAO.getById(11)).thenReturn(b);
        updateDto.setId(11);
        updateDto.setDate(today.minusDays(1));
        controller.updateBooking(updateDto);
        verifyNoMoreInteractions(tableDAO, customerDAO, bookingDAO);
    }

    @Test
    void updateBooking_endBeforeOrEqualStart_noUpdate() {
        Booking b = new Booking();
        b.setId(12);
        when(bookingDAO.getById(12)).thenReturn(b);
        updateDto.setId(12);
        updateDto.setDate(today.plusDays(1));
        updateDto.setStartTime(BookingTimeSlot.SLOT_14_00);
        updateDto.setEndTime(BookingTimeSlot.SLOT_14_00);
        controller.updateBooking(updateDto);
        verifyNoMoreInteractions(tableDAO, customerDAO, bookingDAO);
    }

    @Test
    void updateBooking_success_noCustomerChange() {
        RestaurantTable t = new RestaurantTable();
        t.setId(20);
        Customer c = new Customer();
        c.setName("N");
        c.setPhoneNumber("P");
        c.setEmail("E");
        Booking b = new Booking(today.plusDays(1), BookingTimeSlot.SLOT_15_00, BookingTimeSlot.SLOT_16_00, t);
        b.setId(13);
        b.setCustomer(c);
        when(bookingDAO.getById(13)).thenReturn(b);
        when(tableDAO.getById(20)).thenReturn(t);
        updateDto.setId(13);
        updateDto.setDate(today.plusDays(2));
        updateDto.setStartTime(BookingTimeSlot.SLOT_17_00);
        updateDto.setEndTime(BookingTimeSlot.SLOT_18_00);
        updateDto.setTableId(20);
        controller.updateBooking(updateDto);
        verify(customerDAO, never()).update(any());
        verify(bookingDAO).update(b);
    }

    @Test
    void updateBooking_tableChanged_andUpdated() {
        RestaurantTable old = new RestaurantTable();
        old.setId(30);
        RestaurantTable nw = new RestaurantTable();
        nw.setId(31);
        Customer c = new Customer();
        Booking b = new Booking(today.plusDays(1), BookingTimeSlot.SLOT_10_00, BookingTimeSlot.SLOT_11_00, old);
        b.setId(14);
        b.setCustomer(c);
        when(bookingDAO.getById(14)).thenReturn(b);
        when(tableDAO.getById(31)).thenReturn(nw);
        updateDto.setId(14);
        updateDto.setDate(today.plusDays(2));
        updateDto.setStartTime(BookingTimeSlot.SLOT_12_00);
        updateDto.setEndTime(BookingTimeSlot.SLOT_13_00);
        updateDto.setTableId(31);
        controller.updateBooking(updateDto);
        assertEquals(nw, b.getTable());
        verify(bookingDAO).update(b);
    }

    @Test
    void updateBooking_customerChanged_andUpdated() {
        RestaurantTable t = new RestaurantTable();
        t.setId(40);
        Customer c = new Customer();
        c.setName("A");
        c.setPhoneNumber("1");
        c.setEmail("a");
        Booking b = new Booking(today.plusDays(1), BookingTimeSlot.SLOT_09_00, BookingTimeSlot.SLOT_10_00, t);
        b.setId(15);
        b.setCustomer(c);
        when(bookingDAO.getById(15)).thenReturn(b);
        when(tableDAO.getById(40)).thenReturn(t);
        updateDto.setId(15);
        updateDto.setDate(today.plusDays(2));
        updateDto.setStartTime(BookingTimeSlot.SLOT_11_00);
        updateDto.setEndTime(BookingTimeSlot.SLOT_12_00);
        updateDto.setTableId(40);
        updateDto.setCustomerName("B");
        updateDto.setCustomerPhoneNumber("2");
        updateDto.setCustomerEmail("b");
        controller.updateBooking(updateDto);
        verify(customerDAO).update(c);
        verify(bookingDAO).update(b);
    }

    @Test
    void deleteBooking_invokesDao() {
        controller.deleteBooking(99);
        verify(bookingDAO).delete(99);
    }
}
