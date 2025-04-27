package com.restaurant.daos;

import com.restaurant.dtos.orderItem.GetOrderItemDto;
import com.restaurant.models.OrderItem;

import java.util.List;

public interface OrderItemDAO {
    void add(OrderItem item);

    OrderItem getById(int id);

    List<OrderItem> find(GetOrderItemDto dto);

    void update(OrderItem item);

    void delete(int id);

    boolean existsByOrderAndMenuItem(int orderId, int menuItemId, String customization);
}