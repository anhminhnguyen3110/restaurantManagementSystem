package com.restaurant.utils.validators;

import com.restaurant.dtos.booking.CreateBookingDto;
import com.restaurant.dtos.booking.UpdateBookingDto;
import com.restaurant.constants.BookingTimeSlot;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookingInputValidatorTest {

    private CreateBookingDto baseDto() {
        CreateBookingDto dto = new CreateBookingDto();
        dto.setCustomerName("Alice");
        dto.setCustomerPhoneNumber("+1234567890");
        dto.setCustomerEmail("alice@example.com");
        dto.setDate(LocalDate.now().plusDays(1));
        dto.setStartTime(BookingTimeSlot.SLOT_10_00);
        dto.setEndTime(BookingTimeSlot.SLOT_11_00);
        dto.setTableId(5);
        return dto;
    }

    @Test
    void validInput_yieldsNoErrors() {
        CreateBookingDto dto = baseDto();
        List<String> errors = BookingInputValidator.validate(dto);
        assertTrue(errors.isEmpty(), "Expected no validation errors for valid input");
    }

    @Test
    void updateDto_delegatesToCreate_validateSameBehavior() {
        UpdateBookingDto udto = new UpdateBookingDto();
        udto.setCustomerName("Bob");
        udto.setCustomerPhoneNumber("+1987654321");
        udto.setCustomerEmail("bob@test.com");
        udto.setDate(LocalDate.now().plusDays(2));
        udto.setStartTime(BookingTimeSlot.SLOT_12_00);
        udto.setEndTime(BookingTimeSlot.SLOT_13_00);
        udto.setTableId(3);

        List<String> errs = BookingInputValidator.validate(udto);
        assertTrue(errs.isEmpty(), "UpdateBookingDto should validate same as CreateBookingDto");
    }

    @Test
    void missingAllRequiredFields_collectsAllErrors() {
        CreateBookingDto dto = new CreateBookingDto();
        List<String> errors = BookingInputValidator.validate(dto);
        assertEquals(6, errors.size());
        assertEquals("• Name is required.", errors.get(0));
        assertEquals("• Phone number is required.", errors.get(1));
        assertEquals("• Date is required.", errors.get(2));
        assertEquals("• Start time is required.", errors.get(3));
        assertEquals("• End time is required.", errors.get(4));
        assertEquals("• You must select a table.", errors.get(5));
    }

    @Test
    void invalidPhoneFormat_reportsPhoneInvalid() {
        CreateBookingDto dto = baseDto();
        dto.setCustomerPhoneNumber("bad-phone!");
        List<String> errors = BookingInputValidator.validate(dto);
        assertEquals(1, errors.size());
        assertEquals("• Phone number is invalid.", errors.get(0));
    }

    @Test
    void invalidEmailFormat_reportsEmailInvalid() {
        CreateBookingDto dto = baseDto();
        dto.setCustomerEmail("no-at-symbol");
        List<String> errors = BookingInputValidator.validate(dto);
        assertEquals(1, errors.size());
        assertEquals("• Email is invalid.", errors.get(0));
    }

    @Test
    void pastDate_reportsCannotBookPast() {
        CreateBookingDto dto = baseDto();
        dto.setDate(LocalDate.now().minusDays(1));
        List<String> errors = BookingInputValidator.validate(dto);
        assertTrue(errors.contains("• You cannot book a date in the past."));
    }

    @Test
    void missingStartOrEndTime_reportsIndividually() {
        CreateBookingDto dto = baseDto();
        dto.setStartTime(null);
        dto.setEndTime(null);
        List<String> errors = BookingInputValidator.validate(dto);
        assertTrue(errors.contains("• Start time is required."));
        assertTrue(errors.contains("• End time is required."));
    }

    @Test
    void endTimeNotAfterStartTime_reportsOrderingError() {
        CreateBookingDto dto = baseDto();
        dto.setStartTime(BookingTimeSlot.SLOT_14_00);
        dto.setEndTime(BookingTimeSlot.SLOT_13_30);
        List<String> errors = BookingInputValidator.validate(dto);
        assertTrue(errors.contains("• End time must be after start time."));
    }

    @Test
    void nonPositiveTableId_reportsTableSelectionError() {
        CreateBookingDto dto = baseDto();
        dto.setTableId(0);
        List<String> errors = BookingInputValidator.validate(dto);
        assertEquals(1, errors.size());
        assertEquals("• You must select a table.", errors.get(0));
    }
}
