package com.restaurant.daos;

import com.restaurant.constants.OrderType;
import com.restaurant.dtos.order.GetOrderDto;
import com.restaurant.models.Order;

import java.util.List;

public interface OrderDAO {
    Order add(Order order);

    Order getById(int id);

    List<Order> find(GetOrderDto dto);

    void update(Order order);

    void delete(int id);

    boolean hasPendingForTableAndType(int tableId, OrderType type);

    List<Order> findAll();
}