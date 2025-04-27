package com.restaurant.controllers.impl;

import com.restaurant.controllers.OrderController;
import com.restaurant.daos.OrderDAO;
import com.restaurant.daos.RestaurantTableDAO;
import com.restaurant.di.Inject;
import com.restaurant.di.Injectable;
import com.restaurant.dtos.order.CreateOrderDto;
import com.restaurant.dtos.order.GetOrderDto;
import com.restaurant.dtos.order.UpdateOrderDto;
import com.restaurant.models.Order;
import com.restaurant.models.RestaurantTable;

import java.util.List;

@Injectable
public class OrderControllerImpl implements OrderController {
    @Inject
    private OrderDAO orderDAO;

    @Inject
    private RestaurantTableDAO tableDAO;

    public OrderControllerImpl() {
        // Default constructor for DI
    }

    @Override
    public void createOrder(CreateOrderDto dto) {
        if (orderDAO.hasPendingForTableAndType(dto.getRestaurantTableId(), dto.getOrderType())) {
            System.out.println("Pending order already exists for table " + dto.getRestaurantTableId());
            return;
        }
        RestaurantTable table = tableDAO.getById(dto.getRestaurantTableId());
        Order order = new Order();
        order.setRestaurantTable(table);
        order.setOrderType(dto.getOrderType());
        orderDAO.add(order);
    }

    @Override
    public void updateOrder(UpdateOrderDto dto) {
        Order order = orderDAO.getById(dto.getOrderId());
        if (order == null) return;
        order.setOrderType(dto.getOrderType());
        order.setStatus(dto.getStatus());
        orderDAO.update(order);
    }

    @Override
    public void deleteOrder(int id) {
        orderDAO.delete(id);
    }

    @Override
    public List<Order> findOrders(GetOrderDto dto) {
        return orderDAO.find(dto);
    }

    @Override
    public Order getOrder(int id) {
        return orderDAO.getById(id);
    }
}