package com.restaurant.models;

import com.restaurant.constants.RestaurantStatus;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantTest {

    @Test
    void defaultConstructor_initialState() {
        Restaurant r = new Restaurant();
        assertNull(r.getName());
        assertNull(r.getAddress());
        assertEquals(RestaurantStatus.INACTIVE, r.getStatus());
        assertTrue(r.getMenus().isEmpty());
        assertTrue(r.getTables().isEmpty());
        assertTrue(r.getRestaurantTables().isEmpty());
        assertEquals(0, r.getMaxX());
        assertEquals(0, r.getMaxY());
        assertNull(r.toString());
    }

    @Test
    void constructorWithNameAddress_setsFields() {
        Restaurant r = new Restaurant("MyCafe", "123 Main St");
        assertEquals("MyCafe", r.getName());
        assertEquals("123 Main St", r.getAddress());
        assertEquals(RestaurantStatus.INACTIVE, r.getStatus());
        assertEquals("MyCafe", r.toString());
    }

    @Test
    void gettersAndSetters_work() {
        Restaurant r = new Restaurant();
        r.setName("Bistro");
        r.setAddress("456 Elm Rd");
        r.setStatus(RestaurantStatus.ACTIVE);
        r.setMaxX(50);
        r.setMaxY(75);

        assertEquals("Bistro", r.getName());
        assertEquals("456 Elm Rd", r.getAddress());
        assertEquals(RestaurantStatus.ACTIVE, r.getStatus());
        assertEquals(50, r.getMaxX());
        assertEquals(75, r.getMaxY());

        List<Menu> menus = new ArrayList<>();
        Menu menu = new Menu();
        menus.add(menu);
        r.setMenus(menus);
        assertSame(menus, r.getMenus());

        List<RestaurantTable> tables = new ArrayList<>();
        RestaurantTable table = new RestaurantTable();
        tables.add(table);
        r.setTables(tables);
        assertSame(tables, r.getTables());

        List<RestaurantTable> rtables = new ArrayList<>();
        r.setRestaurantTables(rtables);
        assertSame(rtables, r.getRestaurantTables());
    }

    @Test
    void addMenu_setsRestaurantAndAdds() {
        Restaurant r = new Restaurant("CafeX", "789 Oak Ave");
        Menu m = new Menu();
        r.addMenu(m);
        assertTrue(r.getMenus().contains(m));
        assertSame(r, m.getRestaurant());
    }

    @Test
    void removeMenu_clearsRestaurantAndRemoves() {
        Restaurant r = new Restaurant("CafeY", "101 Pine St");
        Menu m = new Menu();
        r.addMenu(m);
        assertTrue(r.getMenus().contains(m));

        r.removeMenu(m);
        assertFalse(r.getMenus().contains(m));
        assertNull(m.getRestaurant());
    }

    @Test
    void toString_reflectsName() {
        Restaurant r = new Restaurant();
        r.setName("DeliZone");
        assertEquals("DeliZone", r.toString());
    }
}
