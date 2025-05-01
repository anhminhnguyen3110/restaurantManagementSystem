package com.restaurant.utils.validators;

import com.restaurant.constants.UserRole;
import com.restaurant.dtos.user.CreateUserDto;
import com.restaurant.dtos.user.UpdateUserDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserInputValidatorTest {

    @Test
    void validate_create_allValid_noErrors() {
        CreateUserDto dto = new CreateUserDto();
        dto.setUsername("User123");
        dto.setPassword("Passw0rd");
        dto.setEmail("user@example.com");
        dto.setRole(UserRole.MANAGER);
        List<String> errors = UserInputValidator.validate(dto);
        assertTrue(errors.isEmpty());
    }

    @Test
    void validate_create_missingUsername_error() {
        CreateUserDto dto = new CreateUserDto();
        dto.setUsername("");
        dto.setPassword("Passw0rd");
        dto.setEmail("user@example.com");
        dto.setRole(UserRole.OWNER);
        List<String> errors = UserInputValidator.validate(dto);
        assertEquals(1, errors.size());
        assertEquals("• Username is required.", errors.get(0));
    }

    @Test
    void validate_create_invalidUsername_error() {
        CreateUserDto dto = new CreateUserDto();
        dto.setUsername("1Invalid");
        dto.setPassword("Passw0rd");
        dto.setEmail("user@example.com");
        dto.setRole(UserRole.COOK);
        List<String> errors = UserInputValidator.validate(dto);
        assertEquals(1, errors.size());
        assertEquals("• Username must start with a letter and be 3–20 characters (letters, digits, underscore).", errors.get(0));
    }

    @Test
    void validate_create_missingPassword_error() {
        CreateUserDto dto = new CreateUserDto();
        dto.setUsername("ValidUser");
        dto.setPassword("");
        dto.setEmail("user@example.com");
        dto.setRole(UserRole.SHIPPER);
        List<String> errors = UserInputValidator.validate(dto);
        assertEquals(1, errors.size());
        assertEquals("• Password is required.", errors.get(0));
    }

    @Test
    void validate_create_invalidPassword_error() {
        CreateUserDto dto = new CreateUserDto();
        dto.setUsername("ValidUser");
        dto.setPassword("short");
        dto.setEmail("user@example.com");
        dto.setRole(UserRole.WAIT_STAFF);
        List<String> errors = UserInputValidator.validate(dto);
        assertEquals(1, errors.size());
        assertEquals("• Password must be at least 6 characters and include uppercase, lowercase, and a digit.", errors.get(0));
    }

    @Test
    void validate_create_missingEmail_error() {
        CreateUserDto dto = new CreateUserDto();
        dto.setUsername("ValidUser");
        dto.setPassword("Passw0rd");
        dto.setEmail("");
        dto.setRole(UserRole.OWNER);
        List<String> errors = UserInputValidator.validate(dto);
        assertEquals(1, errors.size());
        assertEquals("• Email is required.", errors.get(0));
    }

    @Test
    void validate_create_invalidEmail_error() {
        CreateUserDto dto = new CreateUserDto();
        dto.setUsername("ValidUser");
        dto.setPassword("Passw0rd");
        dto.setEmail("bad-email");
        dto.setRole(UserRole.MANAGER);
        List<String> errors = UserInputValidator.validate(dto);
        assertEquals(1, errors.size());
        assertEquals("• Email is invalid.", errors.get(0));
    }

    @Test
    void validate_create_missingRole_error() {
        CreateUserDto dto = new CreateUserDto();
        dto.setUsername("ValidUser");
        dto.setPassword("Passw0rd");
        dto.setEmail("user@example.com");
        dto.setRole(null);
        List<String> errors = UserInputValidator.validate(dto);
        assertEquals(1, errors.size());
        assertEquals("• Role is required.", errors.get(0));
    }

    @Test
    void validate_create_multipleErrors_allCollected() {
        CreateUserDto dto = new CreateUserDto();
        dto.setUsername("1");
        dto.setPassword("pw");
        dto.setEmail("no-at");
        dto.setRole(null);
        List<String> errors = UserInputValidator.validate(dto);
        assertEquals(4, errors.size());
        assertTrue(errors.contains("• Username must start with a letter and be 3–20 characters (letters, digits, underscore)."));
        assertTrue(errors.contains("• Password must be at least 6 characters and include uppercase, lowercase, and a digit."));
        assertTrue(errors.contains("• Email is invalid."));
        assertTrue(errors.contains("• Role is required."));
    }

    @Test
    void validate_update_allValid_noErrors() {
        UpdateUserDto dto = new UpdateUserDto();
        dto.setPassword("NewPass1");
        dto.setEmail("new@example.com");
        dto.setRole(UserRole.COOK);
        dto.setActive(true);
        List<String> errors = UserInputValidator.validate(dto);
        assertTrue(errors.isEmpty());
    }

    @Test
    void validate_update_invalidPassword_error() {
        UpdateUserDto dto = new UpdateUserDto();
        dto.setPassword("short");
        dto.setEmail("ok@example.com");
        dto.setRole(UserRole.MANAGER);
        dto.setActive(false);
        List<String> errors = UserInputValidator.validate(dto);
        assertEquals(1, errors.size());
        assertEquals("• Password must be at least 6 characters and include uppercase, lowercase, and a digit.", errors.get(0));
    }

    @Test
    void validate_update_invalidEmail_error() {
        UpdateUserDto dto = new UpdateUserDto();
        dto.setPassword(null);
        dto.setEmail("bademail");
        dto.setRole(UserRole.SHIPPER);
        dto.setActive(true);
        List<String> errors = UserInputValidator.validate(dto);
        assertEquals(1, errors.size());
        assertEquals("• Email is invalid.", errors.get(0));
    }

    @Test
    void validate_update_missingRole_error() {
        UpdateUserDto dto = new UpdateUserDto();
        dto.setPassword(null);
        dto.setEmail("ok@example.com");
        dto.setRole(null);
        dto.setActive(true);
        List<String> errors = UserInputValidator.validate(dto);
        assertEquals(1, errors.size());
        assertEquals("• Role is required.", errors.get(0));
    }

    @Test
    void validate_update_missingActive_error() {
        UpdateUserDto dto = new UpdateUserDto();
        dto.setPassword(null);
        dto.setEmail("ok@example.com");
        dto.setRole(UserRole.OWNER);
        dto.setActive(null);
        List<String> errors = UserInputValidator.validate(dto);
        assertEquals(1, errors.size());
        assertEquals("• Active status is required.", errors.get(0));
    }

    @Test
    void validate_update_multipleErrors_allCollected() {
        UpdateUserDto dto = new UpdateUserDto();
        dto.setPassword("pw");
        dto.setEmail("bad");
        dto.setRole(null);
        dto.setActive(null);
        List<String> errors = UserInputValidator.validate(dto);
        assertEquals(4, errors.size());
        assertTrue(errors.contains("• Password must be at least 6 characters and include uppercase, lowercase, and a digit."));
        assertTrue(errors.contains("• Email is invalid."));
        assertTrue(errors.contains("• Role is required."));
        assertTrue(errors.contains("• Active status is required."));
    }
}
