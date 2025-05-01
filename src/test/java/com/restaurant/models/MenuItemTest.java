package com.restaurant.models;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MenuItemTest {

    @Test
    void defaultConstructor_allFieldsDefault() {
        MenuItem mi = new MenuItem();
        assertNull(mi.getName());
        assertNull(mi.getDescription());
        assertEquals(0.0, mi.getPrice());
        assertNotNull(mi.getOrderItems());
        assertTrue(mi.getOrderItems().isEmpty());
        assertNull(mi.getMenu());
    }

    @Test
    void settersAndGetters_work() {
        MenuItem mi = new MenuItem();
        mi.setName("Burger");
        mi.setDescription("Tasty");
        mi.setPrice(9.99);
        Menu menu = new Menu();
        mi.setMenu(menu);
        assertEquals("Burger", mi.getName());
        assertEquals("Tasty", mi.getDescription());
        assertEquals(9.99, mi.getPrice());
        assertSame(menu, mi.getMenu());
    }

    @Test
    void getTotalOrderedCount_zeroWhenNoOrderItems() {
        MenuItem mi = new MenuItem();
        assertEquals(0, mi.getTotalOrderedCount());
    }

    @Test
    void getTotalOrderedCount_sumsQuantities() {
        MenuItem mi = new MenuItem();
        OrderItem oi1 = new OrderItem();
        oi1.setQuantity(2);
        OrderItem oi2 = new OrderItem();
        oi2.setQuantity(3);
        mi.getOrderItems().add(oi1);
        mi.getOrderItems().add(oi2);
        assertEquals(5, mi.getTotalOrderedCount());
    }

    @Test
    void setOrderItems_replacesListUsedByTotal() {
        MenuItem mi = new MenuItem();
        OrderItem oi = new OrderItem();
        oi.setQuantity(7);
        mi.setOrderItems(List.of(oi));
        assertEquals(7, mi.getTotalOrderedCount());
    }
}
