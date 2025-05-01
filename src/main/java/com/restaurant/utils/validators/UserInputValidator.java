package com.restaurant.utils.validators;

import com.restaurant.dtos.user.CreateUserDto;
import com.restaurant.dtos.user.UpdateUserDto;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class UserInputValidator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z][A-Za-z0-9_]{2,19}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{6,}$");

    public static List<String> validate(CreateUserDto dto) {
        List<String> errors = new ArrayList<>();
        if (dto.getUsername() == null || dto.getUsername().trim().isEmpty()) {
            errors.add("• Username is required.");
        } else if (!USERNAME_PATTERN.matcher(dto.getUsername().trim()).matches()) {
            errors.add("• Username must start with a letter and be 3–20 characters (letters, digits, underscore).");
        }
        if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
            errors.add("• Password is required.");
        } else if (!PASSWORD_PATTERN.matcher(dto.getPassword()).matches()) {
            errors.add("• Password must be at least 6 characters and include uppercase, lowercase, and a digit.");
        }
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            errors.add("• Email is required.");
        } else if (!EMAIL_PATTERN.matcher(dto.getEmail().trim()).matches()) {
            errors.add("• Email is invalid.");
        }
        if (dto.getRole() == null) {
            errors.add("• Role is required.");
        }
        return errors;
    }

    public static List<String> validate(UpdateUserDto dto) {
        List<String> errors = new ArrayList<>();
        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            if (!PASSWORD_PATTERN.matcher(dto.getPassword().trim()).matches()) {
                errors.add("• Password must be at least 6 characters and include uppercase, lowercase, and a digit.");
            }
        }
        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
            if (!EMAIL_PATTERN.matcher(dto.getEmail().trim()).matches()) {
                errors.add("• Email is invalid.");
            }
        }
        if (dto.getRole() == null) {
            errors.add("• Role is required.");
        }
        if (dto.isActive() == null) {
            errors.add("• Active status is required.");
        }
        return errors;
    }
}
