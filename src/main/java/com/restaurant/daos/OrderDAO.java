package com.restaurant.daos;

import com.restaurant.models.Order;
import com.restaurant.constants.OrderType;

import java.util.List;

public interface OrderDAO {
    void add(Order order);

    Order getById(int id);

    List<Order> findAll();

    List<Order> findByStatus(String status);

    List<Order> findByType(OrderType type);

    List<Order> findByTable(int tableId);

    void update(Order order);

    void delete(int id);
}