package com.restaurant.controllers;

import com.restaurant.constants.OrderItemStatus;
import com.restaurant.controllers.impl.OrderItemControllerImpl;
import com.restaurant.daos.MenuItemDAO;
import com.restaurant.daos.OrderDAO;
import com.restaurant.daos.OrderItemDAO;
import com.restaurant.dtos.orderItem.CreateOrderItemDto;
import com.restaurant.dtos.orderItem.GetOrderItemDto;
import com.restaurant.dtos.orderItem.UpdateOrderItemDto;
import com.restaurant.models.MenuItem;
import com.restaurant.models.Order;
import com.restaurant.models.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderItemControllerImplTest {
    @Mock
    OrderItemDAO orderItemDAO;
    @Mock
    OrderDAO orderDAO;
    @Mock
    MenuItemDAO menuItemDAO;
    @InjectMocks
    OrderItemControllerImpl controller;

    CreateOrderItemDto createDto;
    UpdateOrderItemDto updateDto;
    GetOrderItemDto getDto;

    @BeforeEach
    void setUp() {
        createDto = new CreateOrderItemDto();
        updateDto = new UpdateOrderItemDto();
        getDto = new GetOrderItemDto();
    }

    @Test
    void createOrderItem_duplicate_noAdd() {
        createDto.setOrderId(1);
        createDto.setMenuItemId(2);
        createDto.setCustomization("X");
        when(orderItemDAO.existsByOrderAndMenuItem(1, 2, "X")).thenReturn(true);

        controller.createOrderItem(createDto);

        verify(orderItemDAO).existsByOrderAndMenuItem(1, 2, "X");
        verifyNoMoreInteractions(orderItemDAO, orderDAO, menuItemDAO);
    }

    @Test
    void createOrderItem_success_updatesOrderAndAddsItem() {
        createDto.setOrderId(10);
        createDto.setMenuItemId(20);
        createDto.setQuantity(3);
        createDto.setCustomization("No onions");
        when(orderItemDAO.existsByOrderAndMenuItem(10, 20, "No onions")).thenReturn(false);

        Order order = new Order();
        order.setTotalPrice(10.0);
        when(orderDAO.getById(10)).thenReturn(order);

        MenuItem mi = new MenuItem();
        mi.setId(20);
        mi.setPrice(5.0);
        when(menuItemDAO.getById(20)).thenReturn(mi);

        controller.createOrderItem(createDto);

        assertEquals(25.0, order.getTotalPrice());
        verify(orderDAO).update(order);

        ArgumentCaptor<OrderItem> capt = ArgumentCaptor.forClass(OrderItem.class);
        verify(orderItemDAO).add(capt.capture());
        OrderItem added = capt.getValue();
        assertSame(order, added.getOrder());
        assertSame(mi, added.getMenuItem());
        assertEquals(3, added.getQuantity());
        assertEquals("No onions", added.getCustomization());
    }

    @Test
    void updateOrderItem_notFound_noUpdate() {
        updateDto.setId(5);
        when(orderItemDAO.getById(5)).thenReturn(null);

        controller.updateOrderItem(updateDto);

        verify(orderItemDAO).getById(5);
        verifyNoMoreInteractions(orderItemDAO, orderDAO);
    }

    @Test
    void updateOrderItem_quantityAndStatus_updatesOrderAndItem() {
        updateDto.setId(6);
        updateDto.setOrderId(7);
        updateDto.setMenuItemId(8);
        updateDto.setCustomization("C");

        OrderItem oi = new OrderItem();
        oi.setId(6);
        oi.setQuantity(2);
        MenuItem mi = new MenuItem();
        mi.setPrice(4.0);
        oi.setMenuItem(mi);
        Order order = new Order();
        order.setTotalPrice(100.0);
        oi.setOrder(order);
        oi.setStatus(null);
        when(orderItemDAO.getById(6)).thenReturn(oi);

        updateDto.setQuantity(5);
        updateDto.setStatus(null);

        controller.updateOrderItem(updateDto);

        assertEquals(112.0, order.getTotalPrice());
        verify(orderDAO).update(order);
        assertEquals(5, oi.getQuantity());
        verify(orderItemDAO).update(oi);
    }

    @Test
    void updateOrderItem_statusOnly_updatesItem() {
        updateDto.setId(9);
        updateDto.setStatus(OrderItemStatus.SERVED);

        OrderItem oi = new OrderItem();
        oi.setId(9);
        oi.setQuantity(0);
        oi.setMenuItem(new MenuItem());
        oi.setOrder(new Order());
        when(orderItemDAO.getById(9)).thenReturn(oi);

        controller.updateOrderItem(updateDto);

        assertEquals(OrderItemStatus.SERVED, oi.getStatus());
        verify(orderDAO, never()).update(any());
        verify(orderItemDAO).update(oi);
    }

    @Test
    void findOrderItems_delegatesToDao() {
        List<OrderItem> list = List.of(new OrderItem(), new OrderItem());
        when(orderItemDAO.find(getDto)).thenReturn(list);
        assertEquals(list, controller.findOrderItems(getDto));
    }

    @Test
    void deleteOrderItem_notFound_noDelete() {
        when(orderItemDAO.getById(5)).thenReturn(null);

        controller.deleteOrderItem(5);

        verify(orderItemDAO).getById(5);
        verifyNoMoreInteractions(orderItemDAO, orderDAO);
    }

    @Test
    void deleteOrderItem_updatesOrderAndDeletesItem() {
        OrderItem oi = new OrderItem();
        oi.setId(4);
        oi.setQuantity(2);
        MenuItem mi = new MenuItem();
        mi.setPrice(3.0);
        oi.setMenuItem(mi);
        Order order = new Order();
        order.setTotalPrice(50.0);
        oi.setOrder(order);
        when(orderItemDAO.getById(4)).thenReturn(oi);

        controller.deleteOrderItem(4);

        assertEquals(44.0, order.getTotalPrice());
        verify(orderDAO).update(order);
        verify(orderItemDAO).delete(4);
    }

    @Test
    void getOrderItem_delegatesToDao() {
        OrderItem oi = new OrderItem();
        when(orderItemDAO.getById(11)).thenReturn(oi);
        assertSame(oi, controller.getOrderItem(11));
    }
}
