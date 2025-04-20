package com.restaurant.daos;

import com.restaurant.models.RestaurantTables;

import java.util.List;

public interface TableDAO {
    void addTable(RestaurantTables table);

    RestaurantTables getTableById(int id);

    List<RestaurantTables> getAllTables();

    void updateTable(RestaurantTables table);                // capacity/coords/availability

    void updateTableAvailability(int id, boolean available);

    void deleteTable(int id);
}