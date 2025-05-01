package com.restaurant.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    @Test
    void defaultConstructor_allFieldsNull() {
        Customer c = new Customer();
        assertNull(c.getName());
        assertNull(c.getPhoneNumber());
        assertNull(c.getEmail());
        assertNull(c.getAddress());
    }

    @Test
    void twoArgConstructor_setsNameAndPhone() {
        Customer c = new Customer("Alice", "12345");
        assertEquals("Alice", c.getName());
        assertEquals("12345", c.getPhoneNumber());
        assertNull(c.getEmail());
        assertNull(c.getAddress());
    }

    @Test
    void threeArgConstructor_setsNamePhoneAndAddress() {
        Customer c = new Customer("Bob", "67890", "Wonderland");
        assertEquals("Bob", c.getName());
        assertEquals("67890", c.getPhoneNumber());
        assertEquals("Wonderland", c.getAddress());
        assertNull(c.getEmail());
    }

    @Test
    void settersAndGetters_work() {
        Customer c = new Customer();
        c.setName("Carol");
        c.setPhoneNumber("54321");
        c.setEmail("carol@example.com");
        c.setAddress("Dreamland");
        assertEquals("Carol", c.getName());
        assertEquals("54321", c.getPhoneNumber());
        assertEquals("carol@example.com", c.getEmail());
        assertEquals("Dreamland", c.getAddress());
    }
}
