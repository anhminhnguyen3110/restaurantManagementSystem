package com.restaurant.validators;

import com.restaurant.constants.UserRole;
import com.restaurant.dtos.user.CreateUserDto;
import com.restaurant.dtos.user.UpdateUserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserInputValidatorTest {

    private UserInputValidator validator;
    private CreateUserDto createDto;

    @BeforeEach
    void setUp() {
        validator = UserInputValidator.INSTANCE;
        createDto = new CreateUserDto();
        createDto.setUsername("Alice_01");
        createDto.setPassword("Password1");
        createDto.setEmail("alice@example.com");
        createDto.setRole(UserRole.MANAGER);
        createDto.setName("Alice");
    }

    @Test
    void validateCreate_valid_noErrors() {
        List<String> errors = validator.validateCreate(createDto);
        assertTrue(errors.isEmpty());
    }

    @Test
    void validateCreate_missingUsername_reportsError() {
        createDto.setUsername(null);
        List<String> e1 = validator.validateCreate(createDto);
        assertEquals(1, e1.size());
        assertEquals("• Username is required.", e1.get(0));

        createDto.setUsername("   ");
        List<String> e2 = validator.validateCreate(createDto);
        assertEquals(1, e2.size());
        assertEquals("• Username is required.", e2.get(0));
    }

    @Test
    void validateCreate_invalidUsername_reportsPatternError() {
        createDto.setUsername("1abc");
        List<String> errors = validator.validateCreate(createDto);
        assertEquals(1, errors.size());
        assertEquals("• Username must start with a letter and be 3–20 chars.", errors.get(0));
    }

    @Test
    void validateCreate_missingOrInvalidPassword_reportsError() {
        createDto.setPassword(null);
        assertTrue(validator.validateCreate(createDto).contains("• Password must be at least 6 chars & include upper, lower, digit."));

        createDto.setPassword("short1");
        assertTrue(validator.validateCreate(createDto).contains("• Password must be at least 6 chars & include upper, lower, digit."));
    }

    @Test
    void validateCreate_invalidEmail_reportsError() {
        createDto.setEmail("bad-email");
        List<String> errors = validator.validateCreate(createDto);
        assertEquals(1, errors.size());
        assertEquals("• Email is invalid.", errors.get(0));
    }

    @Test
    void validateCreate_missingRole_reportsError() {
        createDto.setRole(null);
        List<String> errors = validator.validateCreate(createDto);
        assertEquals(1, errors.size());
        assertEquals("• Role is required.", errors.get(0));
    }

    @Test
    void validateCreate_missingName_reportsError() {
        createDto.setName("");
        List<String> errors = validator.validateCreate(createDto);
        assertEquals(1, errors.size());
        assertEquals("• Name is required.", errors.get(0));
    }

    @Test
    void validateCreate_multipleErrors_reportsAll() {
        createDto.setUsername("");
        createDto.setPassword("bad");
        createDto.setEmail("bad@");
        createDto.setRole(null);
        createDto.setName("");
        List<String> errors = validator.validateCreate(createDto);
        assertEquals(5, errors.size());
        assertEquals("• Username is required.", errors.get(0));
        assertEquals("• Password must be at least 6 chars & include upper, lower, digit.", errors.get(1));
        assertEquals("• Email is invalid.", errors.get(2));
        assertEquals("• Role is required.", errors.get(3));
        assertEquals("• Name is required.", errors.get(4));
    }

    @Test
    void validateUpdate_valid_noErrors() {
        UpdateUserDto u = new UpdateUserDto();
        u.setId(100);
        u.setName("Alice");
        u.setPassword(null);
        u.setEmail("alice@x.com");
        u.setRole(UserRole.OWNER);
        u.setActive(true);
        List<String> errors = validator.validateUpdate(u);
        assertTrue(errors.isEmpty());
    }

    @Test
    void validateUpdate_missingId_reportsError() {
        UpdateUserDto u = new UpdateUserDto();
        u.setName("Bob");
        u.setRole(UserRole.COOK);
        u.setActive(false);
        List<String> errors = validator.validateUpdate(u);
        assertEquals(2, errors.size());
        assertEquals("• User ID is required.", errors.get(0));
        assertEquals("• Email is invalid.", errors.get(1));
    }

    @Test
    void validateUpdate_missingName_reportsError() {
        UpdateUserDto u = new UpdateUserDto();
        u.setId(1);
        u.setName("   ");
        u.setEmail("a@b.com");
        u.setRole(UserRole.WAIT_STAFF);
        u.setActive(true);
        List<String> errors = validator.validateUpdate(u);
        assertEquals(1, errors.size());
        assertEquals("• Name is required.", errors.get(0));
    }

    @Test
    void validateUpdate_invalidPassword_reportsError() {
        UpdateUserDto u = new UpdateUserDto();
        u.setId(2);
        u.setName("Carol");
        u.setPassword("short");
        u.setEmail("carol@x.com");
        u.setRole(UserRole.SHIPPER);
        u.setActive(true);
        List<String> errors = validator.validateUpdate(u);
        assertEquals(1, errors.size());
        assertEquals("• Password must be at least 6 chars & include upper, lower, digit.", errors.get(0));
    }

    @Test
    void validateUpdate_invalidEmail_reportsError() {
        UpdateUserDto u = new UpdateUserDto();
        u.setId(3);
        u.setName("Dan");
        u.setEmail("no-at");
        u.setRole(UserRole.MANAGER);
        u.setActive(true);
        List<String> errors = validator.validateUpdate(u);
        assertEquals(1, errors.size());
        assertEquals("• Email is invalid.", errors.get(0));
    }

    @Test
    void validateUpdate_missingRole_reportsError() {
        UpdateUserDto u = new UpdateUserDto();
        u.setId(4);
        u.setName("Eve");
        u.setEmail("eve@x.com");
        u.setRole(null);
        u.setActive(true);
        List<String> errors = validator.validateUpdate(u);
        assertEquals(1, errors.size());
        assertEquals("• Role is required.", errors.get(0));
    }

    @Test
    void validateUpdate_missingActive_reportsError() {
        UpdateUserDto u = new UpdateUserDto();
        u.setId(5);
        u.setName("Frank");
        u.setEmail("frank@x.com");
        u.setRole(UserRole.OWNER);
        u.setActive(null);
        List<String> errors = validator.validateUpdate(u);
        assertEquals(1, errors.size());
        assertEquals("• Active status is required.", errors.get(0));
    }

    @Test
    void validateUpdate_multipleErrors_reportsAll() {
        UpdateUserDto u = new UpdateUserDto();
        u.setId(0);
        u.setName("");
        u.setPassword("bad");
        u.setEmail("bad@");
        u.setRole(null);
        u.setActive(null);
        List<String> errors = validator.validateUpdate(u);
        assertEquals(6, errors.size());
        assertEquals("• User ID is required.", errors.get(0));
        assertEquals("• Name is required.", errors.get(1));
        assertEquals("• Password must be at least 6 chars & include upper, lower, digit.", errors.get(2));
        assertEquals("• Email is invalid.", errors.get(3));
        assertEquals("• Role is required.", errors.get(4));
        assertEquals("• Active status is required.", errors.get(5));
    }
}
