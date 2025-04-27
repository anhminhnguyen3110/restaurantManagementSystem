package com.restaurant.controllers;

import com.restaurant.dtos.orderItem.CreateOrderItemDto;
import com.restaurant.dtos.orderItem.GetOrderItemDto;
import com.restaurant.dtos.orderItem.UpdateOrderItemDto;
import com.restaurant.models.OrderItem;

import java.util.List;

public interface OrderItemController {
    void createOrderItem(CreateOrderItemDto createOrderItemDto);

    void updateOrderItem(UpdateOrderItemDto updateOrderItemDto);

    void deleteOrderItem(int id);

    OrderItem getOrderItem(int id);

    List<OrderItem> findOrderItems(GetOrderItemDto getOrderItemsDto);
}
