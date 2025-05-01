package com.restaurant.models;

import com.restaurant.constants.ShipmentService;
import com.restaurant.constants.ShipmentStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShipmentTest {

    @Test
    void defaultConstructor_initializesTrackingAndDefaultStatus() {
        Shipment s1 = new Shipment();
        Shipment s2 = new Shipment();
        assertNotNull(s1.getTrackingNumber());
        assertNotNull(s2.getTrackingNumber());
        assertNotEquals(s1.getTrackingNumber(), s2.getTrackingNumber());
        assertEquals(ShipmentStatus.SHIPPING, s1.getStatus());
    }

    @Test
    void threeArgConstructor_setsFieldsAndKeepsRandomTracking() {
        Order o = new Order();
        Customer c = new Customer("Name", "123");
        Shipment s = new Shipment(o, ShipmentService.GRAB, c);
        assertSame(o, s.getOrder());
        assertEquals(ShipmentService.GRAB, s.getServiceType());
        assertSame(c, s.getCustomer());
        assertNotNull(s.getTrackingNumber());
        assertEquals(ShipmentStatus.SHIPPING, s.getStatus());
    }

    @Test
    void settersAndGetters_work() {
        Shipment s = new Shipment();
        Order o = new Order();
        User u = new User("usr", "pwd", null);
        Customer c = new Customer("C", "456");
        s.setOrder(o);
        s.setServiceType(ShipmentService.INTERNAL);
        s.setShipper(u);
        s.setCustomer(c);
        s.setStatus(ShipmentStatus.SUCCESS);

        assertSame(o, s.getOrder());
        assertEquals(ShipmentService.INTERNAL, s.getServiceType());
        assertSame(u, s.getShipper());
        assertSame(c, s.getCustomer());
        assertEquals(ShipmentStatus.SUCCESS, s.getStatus());
    }
}
