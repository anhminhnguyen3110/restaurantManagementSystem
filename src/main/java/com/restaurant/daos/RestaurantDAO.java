package com.restaurant.daos;

import com.restaurant.dtos.restaurant.GetRestaurantDto;
import com.restaurant.models.Restaurant;

import java.util.List;

public interface RestaurantDAO {
    void add(Restaurant restaurant);

    List<Restaurant> find(GetRestaurantDto getRestaurantDto);

    List<Restaurant> findAll();

    void update(Restaurant restaurant);

    void delete(int id);

    Restaurant getById(int id);

    boolean existsByNameAndAddress(String name, String address);
}