package com.restaurant.validators;

import com.restaurant.dtos.user.CreateUserDto;
import com.restaurant.dtos.user.UpdateUserDto;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class UserInputValidator implements Validator<CreateUserDto, UpdateUserDto> {
    public static final UserInputValidator INSTANCE = new UserInputValidator();

    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern USERNAME = Pattern.compile("^[A-Za-z][A-Za-z0-9_]{2,19}$");
    private static final Pattern PASSWORD = Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{6,}$");

    private UserInputValidator() {
    }

    @Override
    public List<String> validateCreate(CreateUserDto dto) {
        List<String> errors = new ArrayList<>();

        if (dto.getUsername() == null || dto.getUsername().trim().isEmpty())
            errors.add("• Username is required.");
        else if (!USERNAME.matcher(dto.getUsername().trim()).matches())
            errors.add("• Username must start with a letter and be 3–20 chars.");

        if (dto.getPassword() == null || !PASSWORD.matcher(dto.getPassword()).matches())
            errors.add("• Password must be at least 6 chars & include upper, lower, digit.");

        if (dto.getEmail() == null || !EMAIL.matcher(dto.getEmail().trim()).matches())
            errors.add("• Email is invalid.");

        if (dto.getRole() == null)
            errors.add("• Role is required.");

        if (dto.getName() == null || dto.getName().trim().isEmpty())
            errors.add("• Name is required.");

        return errors;
    }

    @Override
    public List<String> validateUpdate(UpdateUserDto dto) {
        List<String> errors = new ArrayList<>();

        if (dto.getId() <= 0)
            errors.add("• User ID is required.");

        if (dto.getName() == null || dto.getName().trim().isEmpty())
            errors.add("• Name is required.");

        String pwd = dto.getPassword();
        if (pwd != null && !pwd.trim().isEmpty() && !PASSWORD.matcher(pwd).matches())
            errors.add("• Password must be at least 6 chars & include upper, lower, digit.");

        if (dto.getEmail() == null || !EMAIL.matcher(dto.getEmail().trim()).matches())
            errors.add("• Email is invalid.");

        if (dto.getRole() == null)
            errors.add("• Role is required.");

        if (dto.isActive() == null)
            errors.add("• Active status is required.");

        return errors;
    }
}
