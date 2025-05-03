package com.restaurant.controllers.impl;

import com.restaurant.constants.OrderStatus;
import com.restaurant.constants.OrderType;
import com.restaurant.controllers.OrderController;
import com.restaurant.daos.OrderDAO;
import com.restaurant.daos.RestaurantDAO;
import com.restaurant.daos.RestaurantTableDAO;
import com.restaurant.daos.ShipmentDAO;
import com.restaurant.di.Inject;
import com.restaurant.di.Injectable;
import com.restaurant.dtos.order.CreateOrderDto;
import com.restaurant.dtos.order.GetOrderDto;
import com.restaurant.dtos.order.UpdateOrderDto;
import com.restaurant.events.ErrorEvent;
import com.restaurant.models.Order;
import com.restaurant.models.Restaurant;
import com.restaurant.models.RestaurantTable;
import com.restaurant.models.Shipment;
import com.restaurant.pubsub.ErrorPubSubService;
import com.restaurant.pubsub.PubSubService;

import java.util.List;

@Injectable
public class OrderControllerImpl implements OrderController {
    private final PubSubService pubSubService = ErrorPubSubService.getInstance();
    @Inject
    private OrderDAO orderDAO;
    @Inject
    private RestaurantTableDAO tableDAO;
    @Inject
    private RestaurantDAO restaurantDAO;
    @Inject
    private ShipmentDAO shipmentDAO;

    public OrderControllerImpl() {
        // Default constructor for DI
    }

    @Override
    public Order createOrder(CreateOrderDto dto) {
        if (orderDAO.hasPendingForTableAndType(dto.getRestaurantTableId(), dto.getOrderType())) {
            pubSubService.publish(new ErrorEvent("Pending order already exists for table " + dto.getRestaurantTableId()));
            return null;
        }
        Order order = new Order();
        if (dto.getRestaurantTableId() != 0 && dto.getOrderType() == OrderType.DINE_IN) {
            RestaurantTable table = tableDAO.getById(dto.getRestaurantTableId());
            table.setAvailable(false);
            tableDAO.update(table);
            order.setRestaurantTable(table);
        }
        Restaurant restaurant = restaurantDAO.getById(dto.getRestaurantId());
        if (restaurant == null) {
            pubSubService.publish(new ErrorEvent("Restaurant not found for ID " + dto.getRestaurantId()));
            return null;
        }
        order.setRestaurant(restaurant);
        order.setOrderType(dto.getOrderType());
        return orderDAO.add(order);
    }

    @Override
    public void updateOrder(UpdateOrderDto dto) {
        Order order = orderDAO.getById(dto.getId());
        if (order == null) {
            pubSubService.publish(new ErrorEvent("Order not found for ID " + dto.getId()));
            return;
        }

        if (order.getStatus() == OrderStatus.COMPLETED) {
            pubSubService.publish(new ErrorEvent("Cannot update completed order " + dto.getId()));
            return;
        }

        RestaurantTable oldTable = order.getRestaurantTable();
        OrderType oldType = order.getOrderType();
        OrderType newType = dto.getOrderType();
        Integer newTableId = dto.getRestaurantTableId() > 0 ? dto.getRestaurantTableId() : null;
        RestaurantTable newTable = newTableId != null ? tableDAO.getById(newTableId) : null;
        if (oldType == OrderType.DINE_IN &&
                (newType != OrderType.DINE_IN ||
                        (newTable != null && oldTable.getId() != newTable.getId()))) {
            oldTable.setAvailable(true);
            tableDAO.update(oldTable);
            order.setRestaurantTable(null);
        }
        if (newType == OrderType.DINE_IN &&
                newTable != null &&
                (oldTable == null || oldTable.getId() != newTable.getId())) {
            newTable.setAvailable(false);
            tableDAO.update(newTable);
            order.setRestaurantTable(newTable);
        }
        if (oldType != OrderType.DINE_IN && newType == OrderType.DINE_IN) {
            Shipment toDelete = order.getShipment();
            if (toDelete != null) {
                order.setShipment(null);
                orderDAO.update(order);
                shipmentDAO.delete(toDelete.getId());
            }
        }
        if (oldType == OrderType.DINE_IN && newType != OrderType.DINE_IN) {
            oldTable.setAvailable(true);
            tableDAO.update(oldTable);
            order.setRestaurantTable(null);
        }
        order.setOrderType(newType);
        order.setStatus(dto.getStatus());
        orderDAO.update(order);
        if (newType == OrderType.DINE_IN &&
                (dto.getStatus() == OrderStatus.COMPLETED || dto.getStatus() == OrderStatus.CANCELLED) &&
                order.getRestaurantTable() != null) {
            RestaurantTable tbl = order.getRestaurantTable();
            tbl.setAvailable(true);
            tableDAO.update(tbl);
            order.setRestaurantTable(null);
            orderDAO.update(order);
        }
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
