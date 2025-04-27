package com.restaurant.daos;

import com.restaurant.dtos.menu.GetMenuDto;
import com.restaurant.models.Menu;

import java.util.List;

public interface MenuDAO {
    void add(Menu menu);

    Menu getById(int id);

    List<Menu> find(GetMenuDto dto);

    void update(Menu menu);

    void delete(int id);

    boolean existsByNameAndRestaurant(String name, int restaurantId);

    boolean existsByNameAndRestaurant(String name, int restaurantId, Integer excludeId);
}