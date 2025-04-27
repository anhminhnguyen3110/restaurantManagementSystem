package com.restaurant.utils.validators;

import com.restaurant.dtos.booking.CreateBookingDto;
import com.restaurant.dtos.booking.UpdateBookingDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class BookingInputValidator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9\\- ]{7,15}$");

    public static List<String> validate(CreateBookingDto dto) {
        List<String> errors = new ArrayList<>();
        if (dto.getCustomerName() == null || dto.getCustomerName().trim().isEmpty()) {
            errors.add("• Name is required.");
        }
        if (dto.getCustomerPhoneNumber() == null || dto.getCustomerPhoneNumber().trim().isEmpty()) {
            errors.add("• Phone number is required.");
        } else if (!PHONE_PATTERN.matcher(dto.getCustomerPhoneNumber().trim()).matches()) {
            errors.add("• Phone number is invalid.");
        }
        if (dto.getCustomerEmail() == null || dto.getCustomerEmail().trim().isEmpty()) {
            errors.add("• Email is required.");
        } else if (!EMAIL_PATTERN.matcher(dto.getCustomerEmail().trim()).matches()) {
            errors.add("• Email is invalid.");
        }
        LocalDate date = dto.getDate();
        if (date == null) {
            errors.add("• Date is required.");
        } else if (date.isBefore(LocalDate.now())) {
            errors.add("• You cannot book a date in the past.");
        }
        if (dto.getStartTime() == null) {
            errors.add("• Start time is required.");
        }
        if (dto.getEndTime() == null) {
            errors.add("• End time is required.");
        }
        if (dto.getStartTime() != null && dto.getEndTime() != null &&
                dto.getEndTime().ordinal() <= dto.getStartTime().ordinal()) {
            errors.add("• End time must be after start time.");
        }
        if (dto.getTableId() > 0) {
            errors.add("• You must select a table.");
        }
        return errors;
    }

    public static List<String> validate(UpdateBookingDto dto) {
        return validate((CreateBookingDto) dto);
    }
}