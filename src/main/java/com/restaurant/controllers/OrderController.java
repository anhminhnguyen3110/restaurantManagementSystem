package com.restaurant.controllers;

import com.restaurant.dtos.order.CreateOrderDto;
import com.restaurant.dtos.order.GetOrderDto;
import com.restaurant.dtos.order.UpdateOrderDto;
import com.restaurant.models.Order;

import java.util.List;

public interface OrderController {
    void createOrder(CreateOrderDto createOrderDto);

    void updateOrder(UpdateOrderDto updateOrderDto);

    void deleteOrder(int id);

    List<Order> findOrders(GetOrderDto getOrderDto);

    Order getOrder(int id);
}
