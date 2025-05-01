package com.restaurant.models;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantTableTest {

    @Test
    void defaultConstructor_initialValues() {
        RestaurantTable t = new RestaurantTable();
        assertEquals(0, t.getNumber());
        assertEquals(0, t.getCapacity());
        assertEquals(0, t.getStartX());
        assertEquals(0, t.getStartY());
        assertEquals(0, t.getEndX());
        assertEquals(0, t.getEndY());
        assertTrue(t.isAvailable());
        assertNotNull(t.getBookings());
        assertTrue(t.getBookings().isEmpty());
        assertNotNull(t.getOrders());
        assertTrue(t.getOrders().isEmpty());
        assertNull(t.getRestaurant());
    }

    @Test
    void gettersAndSetters_work() {
        RestaurantTable t = new RestaurantTable();
        Restaurant r = new Restaurant("Café", "Addr");
        List<Booking> bookings = new ArrayList<>();
        Booking b = new Booking();
        bookings.add(b);
        List<Order> orders = new ArrayList<>();
        Order o = new Order();
        orders.add(o);

        t.setNumber(7);
        t.setCapacity(4);
        t.setStartX(1);
        t.setStartY(2);
        t.setEndX(3);
        t.setEndY(4);
        t.setAvailable(false);
        t.setRestaurant(r);
        t.setBookings(bookings);
        t.setOrders(orders);

        assertEquals(7, t.getNumber());
        assertEquals(4, t.getCapacity());
        assertEquals(1, t.getStartX());
        assertEquals(2, t.getStartY());
        assertEquals(3, t.getEndX());
        assertEquals(4, t.getEndY());
        assertFalse(t.isAvailable());
        assertSame(r, t.getRestaurant());
        assertSame(bookings, t.getBookings());
        assertSame(orders, t.getOrders());
    }

    @Test
    void toString_includesNumberCapacityAndRestaurantName() {
        RestaurantTable t = new RestaurantTable();
        Restaurant r = new Restaurant("Bistro", "Somewhere");
        t.setRestaurant(r);
        t.setNumber(12);
        t.setCapacity(6);
        String expected = "Table #12 (number of people: 6, restaurant: Bistro)";
        assertEquals(expected, t.toString());
    }
}
