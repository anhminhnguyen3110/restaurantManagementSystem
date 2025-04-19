package com.restaurant.daos;

import com.restaurant.models.Order;

import java.util.List;

public interface OrderDAO {
    void addOrder(Order order);

    Order getOrderById(int id);

    List<Order> getOrdersByBookingId(int bookingId);

    List<Order> getAllOrders();

    void updateOrderStatus(int orderId, String status);

    void deleteOrder(int id);
}