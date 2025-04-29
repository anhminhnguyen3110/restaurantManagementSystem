package com.restaurant.daos;

import com.restaurant.dtos.menuItem.GetMenuItemsDto;
import com.restaurant.models.MenuItem;

import java.util.List;

public interface MenuItemDAO {
    void add(MenuItem item);

    MenuItem getById(int id);

    List<MenuItem> find(GetMenuItemsDto dto);

    void update(MenuItem item);

    void delete(int id);

    boolean existsByName(String name);

    boolean existsByName(String name, Integer excludeId);

    List<MenuItem> findByRestaurantId(int restaurantId);
}