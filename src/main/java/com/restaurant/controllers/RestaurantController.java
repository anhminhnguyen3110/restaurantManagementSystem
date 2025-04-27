package com.restaurant.controllers;

import com.restaurant.dtos.restaurant.CreateRestaurantDto;
import com.restaurant.dtos.restaurant.GetRestaurantDto;
import com.restaurant.dtos.restaurant.UpdateRestaurantDto;
import com.restaurant.models.Restaurant;

import java.util.List;

public interface RestaurantController {
    void createRestaurant(CreateRestaurantDto createRestaurantDto);

    void updateRestaurant(UpdateRestaurantDto updateRestaurantDto);

    List<Restaurant> findRestaurants(GetRestaurantDto getRestaurantDto);

    Restaurant getRestaurantById(int id);

    List<Restaurant> findAllRestaurants();
}
