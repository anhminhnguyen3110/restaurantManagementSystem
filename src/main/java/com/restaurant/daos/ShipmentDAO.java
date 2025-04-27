package com.restaurant.daos;

import com.restaurant.dtos.shipment.GetShipmentDto;
import com.restaurant.models.Shipment;

import java.util.List;

public interface ShipmentDAO {
    void add(Shipment shipment);

    Shipment getById(int id);

    List<Shipment> find(GetShipmentDto dto);

    void update(Shipment shipment);

    void delete(int id);

    boolean existsPendingByOrder(int orderId);
}