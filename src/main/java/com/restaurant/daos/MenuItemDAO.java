package com.restaurant.daos;

import com.restaurant.models.MenuItem;

import java.util.List;

public interface MenuItemDAO {
    void addMenuItem(MenuItem item);

    MenuItem getMenuItemById(int id);

    List<MenuItem> getAllMenuItems();

    void updateMenuItem(MenuItem item);

    void deleteMenuItem(int id);
}