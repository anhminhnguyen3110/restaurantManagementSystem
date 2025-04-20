package com.restaurant.daos;

import com.restaurant.models.MenuItem;

import java.util.List;

public interface MenuItemDAO {
    void add(MenuItem item);

    MenuItem getById(int id);

    List<MenuItem> findAll();

    MenuItem findByName(String name);

    List<MenuItem> findByMenu(int menuId);

    void update(MenuItem item);

    void delete(int id);
}