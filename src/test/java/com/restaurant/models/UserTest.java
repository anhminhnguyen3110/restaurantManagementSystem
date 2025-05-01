package com.restaurant.models;

import com.restaurant.constants.UserRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void defaultConstructor_andDefaults() {
        User u = new User();
        assertNull(u.getUsername());
        assertNull(u.getPasswordHash());
        assertNull(u.getRole());
        assertNull(u.getEmail());
        assertTrue(u.isActive());
        assertNull(u.getName());
    }

    @Test
    void threeArgConstructor_setsUsernamePasswordAndRole() {
        User u = new User("bob", "hash", UserRole.MANAGER);
        assertEquals("bob", u.getUsername());
        assertEquals("hash", u.getPasswordHash());
        assertEquals(UserRole.MANAGER, u.getRole());
    }

    @Test
    void settersAndGetters_work() {
        User u = new User();
        u.setUsername("alice");
        u.setPasswordHash("h");
        u.setRole(UserRole.COOK);
        u.setEmail("e@mail");
        u.setActive(false);
        u.setName("Alice");

        assertEquals("alice", u.getUsername());
        assertEquals("h", u.getPasswordHash());
        assertEquals(UserRole.COOK, u.getRole());
        assertEquals("e@mail", u.getEmail());
        assertFalse(u.isActive());
        assertEquals("Alice", u.getName());
    }
}
