package com.restaurant.controllers;

import com.restaurant.dtos.restaurant.CreateRestaurantDto;
import com.restaurant.dtos.restaurant.GetRestaurantDto;
import com.restaurant.dtos.restaurant.UpdateRestaurantDto;
import com.restaurant.models.Restaurant;

public interface RestaurantController {
    void createRestaurant(CreateRestaurantDto createRestaurantDto);

    void updateRestaurant(UpdateRestaurantDto updateRestaurantDto);

    Restaurant findRestaurants(GetRestaurantDto getRestaurantDto);
}
