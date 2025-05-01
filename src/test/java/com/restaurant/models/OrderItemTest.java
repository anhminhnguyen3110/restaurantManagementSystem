package com.restaurant.models;

import com.restaurant.constants.OrderItemStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

    @Test
    void defaultConstructor_initializesDefaults() {
        OrderItem oi = new OrderItem();
        assertNull(oi.getOrder());
        assertNull(oi.getMenuItem());
        assertEquals(1, oi.getQuantity());
        assertNull(oi.getCustomization());
        assertEquals(OrderItemStatus.PENDING, oi.getStatus());
    }

    @Test
    void constructorWithoutCustomization_setsFieldsAndDefaults() {
        Order order = new Order();
        MenuItem menuItem = new MenuItem();
        OrderItem oi = new OrderItem(order, menuItem, 5);
        assertSame(order, oi.getOrder());
        assertSame(menuItem, oi.getMenuItem());
        assertEquals(5, oi.getQuantity());
        assertNull(oi.getCustomization());
        assertEquals(OrderItemStatus.PENDING, oi.getStatus());
    }

    @Test
    void constructorWithCustomization_setsAllFields() {
        Order order = new Order();
        MenuItem menuItem = new MenuItem();
        OrderItem oi = new OrderItem(order, menuItem, 2, "No onions");
        assertSame(order, oi.getOrder());
        assertSame(menuItem, oi.getMenuItem());
        assertEquals(2, oi.getQuantity());
        assertEquals("No onions", oi.getCustomization());
        assertEquals(OrderItemStatus.PENDING, oi.getStatus());
    }

    @Test
    void settersAndGetters_work() {
        OrderItem oi = new OrderItem();
        Order order = new Order();
        MenuItem menuItem = new MenuItem();

        oi.setOrder(order);
        assertSame(order, oi.getOrder());

        oi.setMenuItem(menuItem);
        assertSame(menuItem, oi.getMenuItem());

        oi.setQuantity(10);
        assertEquals(10, oi.getQuantity());

        oi.setCustomization("Extra spicy");
        assertEquals("Extra spicy", oi.getCustomization());

        oi.setStatus(OrderItemStatus.READY);
        assertEquals(OrderItemStatus.READY, oi.getStatus());
    }
}
