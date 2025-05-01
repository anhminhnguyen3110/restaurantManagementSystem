package com.restaurant.controllers;

import com.restaurant.dtos.restaurantTable.CreateRestaurantTableDto;
import com.restaurant.dtos.restaurantTable.GetRestaurantTableDto;
import com.restaurant.dtos.restaurantTable.GetRestaurantTableForBookingDto;
import com.restaurant.dtos.restaurantTable.UpdateRestaurantTableDto;
import com.restaurant.models.RestaurantTable;

import java.util.List;

public interface RestaurantTableController {
    void createTable(CreateRestaurantTableDto createRestaurantDto);

    void updateTable(UpdateRestaurantTableDto updateRestaurantDto);

    List<RestaurantTable> findTables(GetRestaurantTableDto getRestaurantTableDto);

    List<RestaurantTable> findTablesForBooking(GetRestaurantTableForBookingDto getRestaurantTableForBookingDto);

    List<RestaurantTable> findAllTablesForOrder(int restaurantId);
}
