package com.restaurant.daos;

import com.restaurant.models.Table;

import java.util.List;

public interface TableDAO {
    void addTable(Table table);

    Table getTableById(int id);

    List<Table> getAllTables();

    void updateTable(Table table);                // capacity/coords/availability

    void updateTableAvailability(int id, boolean available);

    void deleteTable(int id);
}