package com.restaurant.validators;

import com.restaurant.constants.BookingStatus;
import com.restaurant.dtos.booking.CreateBookingDto;
import com.restaurant.dtos.booking.UpdateBookingDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class BookingInputValidator implements Validator<CreateBookingDto, UpdateBookingDto> {
    public static final BookingInputValidator INSTANCE = new BookingInputValidator();

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9\\- ]{7,15}$");

    private BookingInputValidator() {
    }

    @Override
    public List<String> validateCreate(CreateBookingDto dto) {
        List<String> errors = new ArrayList<>();

        if (dto.getCustomerName() == null || dto.getCustomerName().trim().isEmpty()) {
            errors.add("• Name is required.");
        }

        String phone = dto.getCustomerPhoneNumber();
        if (phone == null || phone.trim().isEmpty()) {
            errors.add("• Phone number is required.");
        } else if (!PHONE_PATTERN.matcher(phone.trim()).matches()) {
            errors.add("• Phone number is invalid.");
        }

        String email = dto.getCustomerEmail();
        if (email != null && !email.trim().isEmpty() &&
                !EMAIL_PATTERN.matcher(email.trim()).matches()) {
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

        if (dto.getTableId() <= 0) {
            errors.add("• You must select a table.");
        }

        return errors;
    }

    @Override
    public List<String> validateUpdate(UpdateBookingDto dto) {
        List<String> errors = new ArrayList<>(validateCreate(dto));

        if (dto.getId() <= 0) {
            errors.add("• Booking ID is required.");
        }
        if (dto.getStatus() == null) {
            errors.add("• Booking status is required.");
        } else if (!(dto.getStatus() instanceof BookingStatus)) {
            errors.add("• Booking status is invalid.");
        }

        return errors;
    }
}
