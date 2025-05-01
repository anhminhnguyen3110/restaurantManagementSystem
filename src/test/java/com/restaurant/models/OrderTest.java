package com.restaurant.models;

import com.restaurant.constants.OrderStatus;
import com.restaurant.constants.OrderType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void defaultConstructor_initializesDefaults() {
        Order o = new Order();
        assertEquals(OrderStatus.PENDING, o.getStatus());
        assertEquals(0.0, o.getTotalPrice());
        assertNull(o.getOrderType());
        assertTrue(o.getItems().isEmpty());
        assertNull(o.getRestaurantTable());
        assertNull(o.getPayment());
        assertNull(o.getShipment());
        assertNull(o.getRestaurant());
    }

    @Test
    void twoArgConstructor_setsItemsAndOrderType_andCalculatesTotal() {
        MenuItem m1 = new MenuItem();
        m1.setPrice(10.0);
        OrderItem i1 = new OrderItem(null, m1, 2);

        MenuItem m2 = new MenuItem();
        m2.setPrice(5.0);
        OrderItem i2 = new OrderItem(null, m2, 3);

        Order o = new Order(List.of(i1, i2), OrderType.DELIVERY);
        assertEquals(OrderType.DELIVERY, o.getOrderType());
        assertEquals(2 * 10.0 + 3 * 5.0, o.getTotalPrice());
        assertEquals(2, o.getItems().size());
    }

    @Test
    void recalcTotal_updatesTotalPrice_correctly() {
        Order o = new Order();
        MenuItem m = new MenuItem();
        m.setPrice(7.5);
        OrderItem item = new OrderItem(null, m, 4);
        o.addItem(item);
        assertEquals(4 * 7.5, o.getTotalPrice());
        item.setQuantity(2);
        o.recalcTotal();
        assertEquals(2 * 7.5, o.getTotalPrice());
    }

    @Test
    void setItems_replacesList_andRecalculates() {
        Order o = new Order();
        MenuItem m = new MenuItem();
        m.setPrice(3.0);
        OrderItem item = new OrderItem(null, m, 5);
        o.setItems(List.of(item));
        assertEquals(5 * 3.0, o.getTotalPrice());
        assertEquals(1, o.getItems().size());
    }

    @Test
    void addItem_addsItem_andSetsOrder_andRecalculates() {
        Order o = new Order();
        MenuItem m = new MenuItem();
        m.setPrice(2.0);
        OrderItem item = new OrderItem();
        item.setMenuItem(m);
        item.setQuantity(3);
        o.addItem(item);
        assertSame(o, item.getOrder());
        assertEquals(3 * 2.0, o.getTotalPrice());
        assertTrue(o.getItems().contains(item));
    }

    @Test
    void settersAndGetters_work() {
        Order o = new Order();
        RestaurantTable tbl = new RestaurantTable();
        Payment p = new Payment();
        Shipment s = new Shipment();
        Restaurant r = new Restaurant("Name", "Addr");

        o.setRestaurantTable(tbl);
        o.setPayment(p);
        o.setShipment(s);
        o.setRestaurant(r);
        o.setStatus(OrderStatus.COMPLETED);
        o.setOrderType(OrderType.DINE_IN);
        o.setTotalPrice(99.9);

        assertSame(tbl, o.getRestaurantTable());
        assertSame(p, o.getPayment());
        assertSame(s, o.getShipment());
        assertSame(r, o.getRestaurant());
        assertEquals(OrderStatus.COMPLETED, o.getStatus());
        assertEquals(OrderType.DINE_IN, o.getOrderType());
        assertEquals(99.9, o.getTotalPrice());
    }
}
