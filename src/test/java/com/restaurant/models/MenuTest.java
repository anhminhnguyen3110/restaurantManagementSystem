package com.restaurant.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class MenuTest {

    @Test
    void defaultConstructor_itemsEmpty() {
        Menu m = new Menu();
        assertNull(m.getName());
        assertNull(m.getRestaurant());
        assertNotNull(m.getItems());
        assertTrue(m.getItems().isEmpty());
        assertNull(m.getDescription());
    }

    @Test
    void twoArgConstructor_setsNameAndRestaurant() {
        Restaurant r = new Restaurant();
        Menu m = new Menu("Lunch", r);
        assertEquals("Lunch", m.getName());
        assertSame(r, m.getRestaurant());
        assertTrue(m.getItems().isEmpty());
    }

    @Test
    void settersAndGetters_work() {
        Menu m = new Menu();
        Restaurant r = new Restaurant();
        m.setName("Dinner");
        m.setRestaurant(r);
        m.setDescription("Evening menu");
        assertEquals("Dinner", m.getName());
        assertSame(r, m.getRestaurant());
        assertEquals("Evening menu", m.getDescription());
    }

    @Test
    void addItem_addsToItemsAndSetsMenuOnItem() {
        Menu m = new Menu();
        MenuItem item = new MenuItem();
        m.addItem(item);
        List<MenuItem> items = m.getItems();
        assertEquals(1, items.size());
        assertSame(item, items.get(0));
        assertSame(m, item.getMenu());
    }

    @Test
    void setItems_replacesList() {
        Menu m = new Menu();
        MenuItem item1 = new MenuItem();
        MenuItem item2 = new MenuItem();
        m.setItems(List.of(item1, item2));
        assertEquals(2, m.getItems().size());
        assertTrue(m.getItems().contains(item1));
        assertTrue(m.getItems().contains(item2));
    }
}
