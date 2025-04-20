package com.restaurant.daos;

import com.restaurant.models.Menu;

import java.util.List;

public interface MenuDAO {
    void add(Menu menu);

    Menu getById(int id);

    List<Menu> findAll();

    List<Menu> findByRestaurant(int restaurantId);

    void update(Menu menu);

    void delete(int id);
}
