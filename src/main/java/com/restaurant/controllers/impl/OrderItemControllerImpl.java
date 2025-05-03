package com.restaurant.controllers.impl;

import com.restaurant.controllers.OrderItemController;
import com.restaurant.daos.MenuItemDAO;
import com.restaurant.daos.OrderDAO;
import com.restaurant.daos.OrderItemDAO;
import com.restaurant.di.Inject;
import com.restaurant.di.Injectable;
import com.restaurant.dtos.orderItem.CreateOrderItemDto;
import com.restaurant.dtos.orderItem.GetOrderItemDto;
import com.restaurant.dtos.orderItem.UpdateOrderItemDto;
import com.restaurant.events.ErrorEvent;
import com.restaurant.models.MenuItem;
import com.restaurant.models.Order;
import com.restaurant.models.OrderItem;
import com.restaurant.pubsub.ErrorPubSubService;
import com.restaurant.pubsub.PubSubService;

import java.util.List;

@Injectable
public class OrderItemControllerImpl implements OrderItemController {
    private final PubSubService pubSubService = ErrorPubSubService.getInstance();
    @Inject
    private OrderItemDAO orderItemDAO;
    @Inject
    private OrderDAO orderDAO;
    @Inject
    private MenuItemDAO menuItemDAO;

    public OrderItemControllerImpl() {
        // Default constructor for DI
    }

    @Override
    public void createOrderItem(CreateOrderItemDto dto) {
        if (orderItemDAO.existsByOrderAndMenuItem(dto.getOrderId(), dto.getMenuItemId(), dto.getCustomization())) {
            pubSubService.publish(new ErrorEvent("Duplicate order item for order " + dto.getOrderId()));
            return;
        }
        Order order = orderDAO.getById(dto.getOrderId());
        MenuItem mi = menuItemDAO.getById(dto.getMenuItemId());
        OrderItem oi = new OrderItem();
        oi.setOrder(order);
        oi.setMenuItem(mi);
        oi.setQuantity(dto.getQuantity());
        oi.setCustomization(dto.getCustomization());
        order.setTotalPrice(order.getTotalPrice() + (mi.getPrice() * dto.getQuantity()));
        orderDAO.update(order);
        orderItemDAO.add(oi);
    }

    @Override
    public void updateOrderItem(UpdateOrderItemDto dto) {
        OrderItem oi = orderItemDAO.getById(dto.getId());
        if (oi == null) {
            pubSubService.publish(new ErrorEvent("Order item not found: " + dto.getId()));
            return;
        }
        if (dto.getQuantity() != 0) {
            Order order = oi.getOrder();
            order.setTotalPrice(order.getTotalPrice() - oi.getQuantity() * oi.getMenuItem().getPrice());
            oi.setQuantity(dto.getQuantity());
            order.setTotalPrice(order.getTotalPrice() + oi.getQuantity() * oi.getMenuItem().getPrice());
            orderDAO.update(order);
        }
        oi.setStatus(dto.getStatus());
        orderItemDAO.update(oi);
    }

    @Override
    public List<OrderItem> findOrderItems(GetOrderItemDto dto) {
        return orderItemDAO.find(dto);
    }

    @Override
    public void deleteOrderItem(int id) {
        OrderItem oi = orderItemDAO.getById(id);
        if (oi == null) {
            return;
        }
        Order order = oi.getOrder();
        order.setTotalPrice(order.getTotalPrice() - oi.getQuantity() * oi.getMenuItem().getPrice());
        orderDAO.update(order);
        orderItemDAO.delete(id);
    }

    @Override
    public OrderItem getOrderItem(int id) {
        return orderItemDAO.getById(id);
    }
}
