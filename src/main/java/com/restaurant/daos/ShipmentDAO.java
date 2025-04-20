package com.restaurant.daos;

import com.restaurant.models.Shipment;
import com.restaurant.constants.ShipmentStatus;
import com.restaurant.constants.ShipmentService;
import com.restaurant.models.Order;
import com.restaurant.models.User;
import com.restaurant.models.Customer;

import java.util.List;

public interface ShipmentDAO {
    void add(Shipment shipment);

    Shipment getById(int id);

    List<Shipment> findAll();

    List<Shipment> findByStatus(ShipmentStatus status);

    List<Shipment> findByServiceType(ShipmentService serviceType);

    List<Shipment> findByOrder(Order order);

    List<Shipment> findByShipper(User shipper);

    List<Shipment> findByCustomer(Customer customer);

    void update(Shipment shipment);

    void delete(int id);
}