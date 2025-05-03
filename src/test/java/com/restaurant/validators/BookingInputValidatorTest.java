package com.restaurant.validators;

import com.restaurant.constants.BookingStatus;
import com.restaurant.constants.BookingTimeSlot;
import com.restaurant.dtos.booking.CreateBookingDto;
import com.restaurant.dtos.booking.UpdateBookingDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookingInputValidatorTest {

    private BookingInputValidator validator;
    private CreateBookingDto createDto;

    @BeforeEach
    void setUp() {
        validator = BookingInputValidator.INSTANCE;
        createDto = new CreateBookingDto();
        createDto.setCustomerName("Alice");
        createDto.setCustomerPhoneNumber("+1234567890");
        createDto.setCustomerEmail("alice@example.com");
        createDto.setDate(LocalDate.now().plusDays(1));
        createDto.setStartTime(BookingTimeSlot.SLOT_10_00);
        createDto.setEndTime(BookingTimeSlot.SLOT_11_00);
        createDto.setTableId(5);
    }

    @Test
    void validateCreate_validInput_noErrors() {
        List<String> errors = validator.validateCreate(createDto);
        assertTrue(errors.isEmpty());
    }

    @Test
    void validateCreate_missingAllRequired_collectsAllErrors() {
        CreateBookingDto empty = new CreateBookingDto();
        List<String> errors = validator.validateCreate(empty);
        assertEquals(6, errors.size());
        assertEquals("• Name is required.", errors.get(0));
        assertEquals("• Phone number is required.", errors.get(1));
        assertEquals("• Date is required.", errors.get(2));
        assertEquals("• Start time is required.", errors.get(3));
        assertEquals("• End time is required.", errors.get(4));
        assertEquals("• You must select a table.", errors.get(5));
    }

    @Test
    void validateCreate_invalidPhone_reportsError() {
        createDto.setCustomerPhoneNumber("bad");
        List<String> errors = validator.validateCreate(createDto);
        assertEquals(1, errors.size());
        assertEquals("• Phone number is invalid.", errors.get(0));
    }

    @Test
    void validateCreate_blankPhone_reportsRequired() {
        createDto.setCustomerPhoneNumber("   ");
        List<String> errors = validator.validateCreate(createDto);
        assertEquals(1, errors.size());
        assertEquals("• Phone number is required.", errors.get(0));
    }

    @Test
    void validateCreate_invalidEmail_reportsError() {
        createDto.setCustomerEmail("bad-email");
        List<String> errors = validator.validateCreate(createDto);
        assertEquals(1, errors.size());
        assertEquals("• Email is invalid.", errors.get(0));
    }

    @Test
    void validateCreate_nullOrBlankEmail_noError() {
        createDto.setCustomerEmail(null);
        List<String> e1 = validator.validateCreate(createDto);
        assertFalse(e1.contains("• Email is invalid."));
        createDto.setCustomerEmail("   ");
        List<String> e2 = validator.validateCreate(createDto);
        assertFalse(e2.contains("• Email is invalid."));
    }

    @Test
    void validateCreate_pastDate_reportsError() {
        createDto.setDate(LocalDate.now().minusDays(1));
        List<String> errors = validator.validateCreate(createDto);
        assertTrue(errors.contains("• You cannot book a date in the past."));
    }

    @Test
    void validateCreate_todayDate_noPastError() {
        createDto.setDate(LocalDate.now());
        List<String> errors = validator.validateCreate(createDto);
        assertFalse(errors.contains("• You cannot book a date in the past."));
    }

    @Test
    void validateCreate_nullDate_reportsRequired() {
        createDto.setDate(null);
        List<String> errors = validator.validateCreate(createDto);
        assertTrue(errors.contains("• Date is required."));
    }

    @Test
    void validateCreate_endBeforeStart_reportsError() {
        createDto.setStartTime(BookingTimeSlot.SLOT_11_00);
        createDto.setEndTime(BookingTimeSlot.SLOT_10_00);
        List<String> errors = validator.validateCreate(createDto);
        assertTrue(errors.contains("• End time must be after start time."));
    }

    @Test
    void validateCreate_nullStartOrEnd_reportsErrors() {
        createDto.setStartTime(null);
        createDto.setEndTime(null);
        List<String> errors = validator.validateCreate(createDto);
        assertTrue(errors.contains("• Start time is required."));
        assertTrue(errors.contains("• End time is required."));
    }

    @Test
    void validateUpdate_validInput_noErrors() {
        UpdateBookingDto u = new UpdateBookingDto();
        u.setCustomerName("Bob");
        u.setCustomerPhoneNumber("+1987654321");
        u.setCustomerEmail("bob@test.com");
        u.setDate(LocalDate.now().plusDays(2));
        u.setStartTime(BookingTimeSlot.SLOT_12_00);
        u.setEndTime(BookingTimeSlot.SLOT_13_00);
        u.setTableId(3);
        u.setId(42);
        u.setStatus(BookingStatus.BOOKED);
        List<String> errors = validator.validateUpdate(u);
        assertTrue(errors.isEmpty());
    }

    @Test
    void validateUpdate_missingId_reportsError() {
        UpdateBookingDto u = new UpdateBookingDto();
        u.setCustomerName("Carol");
        u.setCustomerPhoneNumber("+1234567");
        u.setCustomerEmail("carol@example.com");
        u.setDate(LocalDate.now().plusDays(3));
        u.setStartTime(BookingTimeSlot.SLOT_09_00);
        u.setEndTime(BookingTimeSlot.SLOT_10_00);
        u.setTableId(1);
        List<String> errors = validator.validateUpdate(u);
        assertTrue(errors.contains("• Booking ID is required."));
    }

    @Test
    void validateUpdate_missingStatus_reportsError() {
        UpdateBookingDto u = new UpdateBookingDto();
        u.setCustomerName("Dave");
        u.setCustomerPhoneNumber("+1234567");
        u.setCustomerEmail("dave@example.com");
        u.setDate(LocalDate.now().plusDays(4));
        u.setStartTime(BookingTimeSlot.SLOT_14_00);
        u.setEndTime(BookingTimeSlot.SLOT_15_00);
        u.setTableId(2);
        u.setId(7);
        List<String> errors = validator.validateUpdate(u);
        assertTrue(errors.contains("• Booking status is required."));
    }

    @Test
    void validateUpdate_inheritCreateErrors_andAddTwo() {
        UpdateBookingDto u = new UpdateBookingDto();
        List<String> errors = validator.validateUpdate(u);
        assertEquals(8, errors.size());
    }
}
