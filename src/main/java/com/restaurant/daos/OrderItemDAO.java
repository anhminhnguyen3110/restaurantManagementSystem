package com.restaurant.daos;

import com.restaurant.models.OrderItem;
import com.restaurant.constants.OrderItemStatus;

import java.util.List;

public interface OrderItemDAO {
    void add(OrderItem item);

    OrderItem getById(int id);

    List<OrderItem> findAll();

    List<OrderItem> findByOrder(int orderId);

    List<OrderItem> findByMenuItem(int menuItemId);

    List<OrderItem> findByStatus(OrderItemStatus status);

    void update(OrderItem item);

    void delete(int id);
}
