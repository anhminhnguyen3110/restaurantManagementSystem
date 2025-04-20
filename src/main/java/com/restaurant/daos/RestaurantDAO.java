package com.restaurant.daos;

import com.restaurant.models.Restaurant;

import java.util.List;

public interface RestaurantDAO {
    void add(Restaurant restaurant);

    Restaurant getById(int id);

    List<Restaurant> findAll();

    Restaurant findByName(String name);

    List<Restaurant> findByAddress(String address);

    void update(Restaurant restaurant);

    void delete(int id);
}