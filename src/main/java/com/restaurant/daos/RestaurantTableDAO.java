package com.restaurant.daos;

import com.restaurant.dtos.restaurantTable.GetRestaurantTableDto;
import com.restaurant.dtos.restaurantTable.GetRestaurantTableForBookingDto;
import com.restaurant.models.RestaurantTable;

import java.util.List;

public interface RestaurantTableDAO {
    void add(RestaurantTable restaurantTable);

    List<RestaurantTable> find(GetRestaurantTableDto dto);

    List<RestaurantTable> findForBooking(GetRestaurantTableForBookingDto dto);

    RestaurantTable getById(int id);

    void update(RestaurantTable restaurantTable);

    void delete(int id);

    boolean existsByRestaurantIdAndStartPosition(int restaurantId, int startX, int startY, Integer excludeId);

    boolean existsByRestaurantIdAndEndPosition(int restaurantId, int endX, int endY, Integer excludeId);

    boolean existsByRestaurantIdAndNumber(int restaurantId, int number, Integer excludeId);
}