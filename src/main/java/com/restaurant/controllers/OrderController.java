package com.restaurant.controllers;

import com.restaurant.models.Order;

public interface OrderController {
    void createOrder();
    void updateOrder();
    void deleteOrder();
    Order findOrders(String name);
}
