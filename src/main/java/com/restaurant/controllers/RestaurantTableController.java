package com.restaurant.controllers;

import com.restaurant.dtos.restaurantTable.*;
import com.restaurant.models.RestaurantTable;

import java.util.List;

public interface RestaurantTableController {
    void createTable(CreateRestaurantTableDto createRestaurantDto);

    void updateTable(UpdateRestaurantTableDto updateRestaurantDto);

    void deleteTable(int id);

    List<RestaurantTable> findTables(GetRestaurantTableDto getRestaurantTableDto);

    List<RestaurantTable> findTablesForBooking(GetRestaurantTableForBookingDto getRestaurantTableForBookingDto);

    RestaurantTable getTable(int id);
}
