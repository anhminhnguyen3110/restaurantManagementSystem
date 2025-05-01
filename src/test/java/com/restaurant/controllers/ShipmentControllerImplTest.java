package com.restaurant.controllers;

import com.restaurant.constants.ShipmentService;
import com.restaurant.constants.ShipmentStatus;
import com.restaurant.controllers.impl.ShipmentControllerImpl;
import com.restaurant.daos.CustomerDAO;
import com.restaurant.daos.OrderDAO;
import com.restaurant.daos.ShipmentDAO;
import com.restaurant.daos.UserDAO;
import com.restaurant.dtos.shipment.CreateShipmentDto;
import com.restaurant.dtos.shipment.GetShipmentDto;
import com.restaurant.dtos.shipment.UpdateShipmentDto;
import com.restaurant.models.Customer;
import com.restaurant.models.Order;
import com.restaurant.models.Shipment;
import com.restaurant.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShipmentControllerImplTest {
    @Mock
    ShipmentDAO shipmentDAO;
    @Mock
    OrderDAO orderDAO;
    @Mock
    UserDAO userDAO;
    @Mock
    CustomerDAO customerDAO;
    @InjectMocks
    ShipmentControllerImpl controller;

    CreateShipmentDto createDto;
    UpdateShipmentDto updateDto;
    GetShipmentDto getDto;

    @BeforeEach
    void setUp() {
        createDto = new CreateShipmentDto();
        updateDto = new UpdateShipmentDto();
        getDto = new GetShipmentDto();
    }

    @Test
    void createShipment_pendingExists_noAdd() {
        createDto.setOrderId(1);
        when(shipmentDAO.existsPendingByOrder(1)).thenReturn(true);
        controller.createShipment(createDto);
        verify(shipmentDAO).existsPendingByOrder(1);
        verifyNoMoreInteractions(shipmentDAO, orderDAO, userDAO, customerDAO);
    }

    @Test
    void createShipment_newCustomer_addsCustomerAndShipment() {
        createDto.setOrderId(2);
        createDto.setShipperId(10);
        createDto.setCustomerPhone("123");
        createDto.setCustomerName("Alice");
        createDto.setCustomerEmail("a@example.com");
        createDto.setCustomerAddress("Addr");
        createDto.setServiceType(ShipmentService.GRAB);

        when(shipmentDAO.existsPendingByOrder(2)).thenReturn(false);
        Order order = new Order();
        order.setId(2);
        when(orderDAO.getById(2)).thenReturn(order);
        User shipper = new User();
        shipper.setId(10);
        when(userDAO.getById(10)).thenReturn(shipper);
        when(customerDAO.getByPhoneNumber("123")).thenReturn(null);

        controller.createShipment(createDto);

        ArgumentCaptor<Customer> custCap = ArgumentCaptor.forClass(Customer.class);
        verify(customerDAO).add(custCap.capture());
        Customer addedCust = custCap.getValue();
        assertEquals("Alice", addedCust.getName());
        assertEquals("123", addedCust.getPhoneNumber());
        assertEquals("a@example.com", addedCust.getEmail());
        assertEquals("Addr", addedCust.getAddress());

        ArgumentCaptor<Shipment> shipCap = ArgumentCaptor.forClass(Shipment.class);
        verify(shipmentDAO).add(shipCap.capture());
        Shipment s = shipCap.getValue();
        assertEquals(ShipmentService.GRAB, s.getServiceType());
        assertSame(order, s.getOrder());
        assertSame(shipper, s.getShipper());
        assertSame(addedCust, s.getCustomer());
    }

    @Test
    void createShipment_existingCustomer_addsShipmentOnly() {
        createDto.setOrderId(3);
        createDto.setShipperId(11);
        createDto.setCustomerPhone("456");
        createDto.setServiceType(ShipmentService.DIDI);

        when(shipmentDAO.existsPendingByOrder(3)).thenReturn(false);
        Order order = new Order();
        order.setId(3);
        when(orderDAO.getById(3)).thenReturn(order);
        User shipper = new User();
        shipper.setId(11);
        when(userDAO.getById(11)).thenReturn(shipper);
        Customer cust = new Customer();
        cust.setPhoneNumber("456");
        when(customerDAO.getByPhoneNumber("456")).thenReturn(cust);

        controller.createShipment(createDto);

        verify(customerDAO, never()).add(any());
        ArgumentCaptor<Shipment> shipCap = ArgumentCaptor.forClass(Shipment.class);
        verify(shipmentDAO).add(shipCap.capture());
        Shipment s = shipCap.getValue();
        assertEquals(ShipmentService.DIDI, s.getServiceType());
        assertSame(order, s.getOrder());
        assertSame(shipper, s.getShipper());
        assertSame(cust, s.getCustomer());
    }

    @Test
    void updateShipment_notFound_noUpdate() {
        updateDto.setId(5);
        when(shipmentDAO.getById(5)).thenReturn(null);
        controller.updateShipment(updateDto);
        verify(shipmentDAO).getById(5);
        verifyNoMoreInteractions(shipmentDAO, userDAO, customerDAO);
    }

    @Test
    void updateShipment_nonInternal_clearsShipper_andUpdates() {
        Shipment s = new Shipment();
        Customer c = new Customer();
        s.setCustomer(c);
        s.setShipper(new User());
        when(shipmentDAO.getById(6)).thenReturn(s);

        updateDto.setId(6);
        updateDto.setServiceType(ShipmentService.GRAB);
        updateDto.setStatus(ShipmentStatus.SUCCESS);
        updateDto.setCustomerName("Bob");
        updateDto.setCustomerEmail("b@e.com");
        updateDto.setCustomerAddress("NewAddr");

        controller.updateShipment(updateDto);

        assertEquals(ShipmentService.GRAB, s.getServiceType());
        assertEquals(ShipmentStatus.SUCCESS, s.getStatus());
        assertEquals("NewAddr", c.getAddress());
        assertEquals("Bob", c.getName());
        assertEquals("b@e.com", c.getEmail());
        assertNull(s.getShipper());
        verify(shipmentDAO).update(s);
    }

    @Test
    void updateShipment_internal_setsShipper_andUpdates() {
        Shipment s = new Shipment();
        Customer c = new Customer();
        s.setCustomer(c);
        s.setShipper(null);
        when(shipmentDAO.getById(7)).thenReturn(s);
        User newShipper = new User();
        newShipper.setId(21);
        when(userDAO.getById(21)).thenReturn(newShipper);

        updateDto.setId(7);
        updateDto.setServiceType(ShipmentService.INTERNAL);
        updateDto.setStatus(ShipmentStatus.FAILED);
        updateDto.setShipperId(21);

        controller.updateShipment(updateDto);

        assertEquals(ShipmentService.INTERNAL, s.getServiceType());
        assertEquals(ShipmentStatus.FAILED, s.getStatus());
        assertSame(newShipper, s.getShipper());
        verify(shipmentDAO).update(s);
    }

    @Test
    void findShipments_delegatesToDao() {
        List<Shipment> list = List.of(new Shipment(), new Shipment());
        when(shipmentDAO.find(getDto)).thenReturn(list);
        assertEquals(list, controller.findShipments(getDto));
        verify(shipmentDAO).find(getDto);
    }
}
