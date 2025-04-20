package com.restaurant.daos;

import com.restaurant.models.RestaurantTable;

import java.util.List;

public interface RestaurantTableDAO {
    void add(RestaurantTable restaurantTable);

    RestaurantTable getById(int id);

    List<RestaurantTable> findAll();

    RestaurantTable findByNumber(int number);

    List<RestaurantTable> findByCapacity(int capacity);

    void update(RestaurantTable restaurantTable);

    void delete(int id);
}
