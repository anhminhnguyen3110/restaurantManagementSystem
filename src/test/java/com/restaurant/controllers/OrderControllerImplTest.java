package com.restaurant.controllers;

import com.restaurant.constants.OrderStatus;
import com.restaurant.constants.OrderType;
import com.restaurant.controllers.impl.OrderControllerImpl;
import com.restaurant.daos.OrderDAO;
import com.restaurant.daos.RestaurantDAO;
import com.restaurant.daos.RestaurantTableDAO;
import com.restaurant.daos.ShipmentDAO;
import com.restaurant.dtos.order.CreateOrderDto;
import com.restaurant.dtos.order.GetOrderDto;
import com.restaurant.dtos.order.UpdateOrderDto;
import com.restaurant.models.Order;
import com.restaurant.models.Restaurant;
import com.restaurant.models.RestaurantTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerImplTest {
    @Mock
    OrderDAO orderDAO;
    @Mock
    RestaurantTableDAO tableDAO;
    @Mock
    RestaurantDAO restaurantDAO;
    @Mock
    ShipmentDAO shipmentDAO;
    @InjectMocks
    OrderControllerImpl controller;

    CreateOrderDto createDto;
    UpdateOrderDto updateDto;
    GetOrderDto getDto;

    @BeforeEach
    void setUp() {
        createDto = new CreateOrderDto();
        updateDto = new UpdateOrderDto();
        getDto = new GetOrderDto();
    }

    @Test
    void createOrder_pendingExists_returnsNull() {
        createDto.setRestaurantTableId(1);
        createDto.setOrderType(OrderType.DINE_IN);
        when(orderDAO.hasPendingForTableAndType(1, OrderType.DINE_IN)).thenReturn(true);
        assertNull(controller.createOrder(createDto));
        verify(orderDAO).hasPendingForTableAndType(1, OrderType.DINE_IN);
        verifyNoMoreInteractions(orderDAO, tableDAO, restaurantDAO);
    }

    @Test
    void createOrder_dineIn_tableReserved_andOrderAdded() {
        createDto.setRestaurantTableId(2);
        createDto.setOrderType(OrderType.DINE_IN);
        createDto.setRestaurantId(3);
        when(orderDAO.hasPendingForTableAndType(2, OrderType.DINE_IN)).thenReturn(false);
        RestaurantTable table = new RestaurantTable();
        when(tableDAO.getById(2)).thenReturn(table);
        Restaurant restaurant = new Restaurant();
        when(restaurantDAO.getById(3)).thenReturn(restaurant);
        Order saved = new Order();
        when(orderDAO.add(any())).thenReturn(saved);

        Order result = controller.createOrder(createDto);

        assertSame(saved, result);
        assertFalse(table.isAvailable());
        verify(tableDAO).update(table);
        ArgumentCaptor<Order> capt = ArgumentCaptor.forClass(Order.class);
        verify(orderDAO).add(capt.capture());
        Order o = capt.getValue();
        assertSame(table, o.getRestaurantTable());
        assertSame(restaurant, o.getRestaurant());
    }

    @Test
    void createOrder_restaurantNotFound_returnsNull() {
        createDto.setOrderType(OrderType.DELIVERY);
        createDto.setRestaurantId(4);
        when(orderDAO.hasPendingForTableAndType(0, OrderType.DELIVERY)).thenReturn(false);
        when(restaurantDAO.getById(4)).thenReturn(null);
        assertNull(controller.createOrder(createDto));
        verify(restaurantDAO).getById(4);
        verifyNoMoreInteractions(orderDAO, tableDAO, restaurantDAO);
    }

    @Test
    void createOrder_delivery_noTable_andOrderAdded() {
        createDto.setOrderType(OrderType.DELIVERY);
        createDto.setRestaurantId(5);
        when(orderDAO.hasPendingForTableAndType(0, OrderType.DELIVERY)).thenReturn(false);
        Restaurant restaurant = new Restaurant();
        when(restaurantDAO.getById(5)).thenReturn(restaurant);
        Order saved = new Order();
        when(orderDAO.add(any())).thenReturn(saved);

        Order result = controller.createOrder(createDto);

        assertSame(saved, result);
        ArgumentCaptor<Order> capt = ArgumentCaptor.forClass(Order.class);
        verify(orderDAO).add(capt.capture());
        Order o = capt.getValue();
        assertNull(o.getRestaurantTable());
        assertSame(restaurant, o.getRestaurant());
    }

    @Test
    void updateOrder_notFound_noInteraction() {
        updateDto.setId(6);
        when(orderDAO.getById(6)).thenReturn(null);
        controller.updateOrder(updateDto);
        verify(orderDAO).getById(6);
        verifyNoMoreInteractions(orderDAO, tableDAO, shipmentDAO);
    }

    @Test
    void updateOrder_deliveryToDineIn_setsTableUnavailable_andNoShipment() {
        Order order = new Order();
        order.setOrderType(OrderType.DELIVERY);
        updateDto.setId(7);
        updateDto.setOrderType(OrderType.DINE_IN);
        updateDto.setRestaurantTableId(8);
        updateDto.setStatus(OrderStatus.PENDING);
        when(orderDAO.getById(7)).thenReturn(order);
        RestaurantTable newTable = new RestaurantTable();
        when(tableDAO.getById(8)).thenReturn(newTable);

        controller.updateOrder(updateDto);

        assertFalse(newTable.isAvailable());
        verify(tableDAO).update(newTable);
        verify(orderDAO).update(order);
        assertEquals(OrderType.DINE_IN, order.getOrderType());
        assertEquals(OrderStatus.PENDING, order.getStatus());
    }

    @Test
    void updateOrder_completeDineIn_releasesTable_andUpdatesTwice() {
        Order order = new Order();
        order.setOrderType(OrderType.DINE_IN);
        RestaurantTable tbl = new RestaurantTable();
        order.setRestaurantTable(tbl);
        updateDto.setId(13);
        updateDto.setOrderType(OrderType.DINE_IN);
        updateDto.setStatus(OrderStatus.COMPLETED);
        when(orderDAO.getById(13)).thenReturn(order);

        controller.updateOrder(updateDto);

        assertTrue(tbl.isAvailable());
        InOrder in = inOrder(tableDAO, orderDAO);
        in.verify(tableDAO).update(tbl);
        in.verify(orderDAO).update(order);
    }

    @Test
    void deleteOrder_delegatesToDao() {
        controller.deleteOrder(14);
        verify(orderDAO).delete(14);
    }

    @Test
    void findOrders_delegatesToDao() {
        List<Order> list = List.of(new Order());
        when(orderDAO.find(getDto)).thenReturn(list);
        assertEquals(list, controller.findOrders(getDto));
    }

    @Test
    void getOrder_delegatesToDao() {
        Order o = new Order();
        when(orderDAO.getById(15)).thenReturn(o);
        assertSame(o, controller.getOrder(15));
    }
}
