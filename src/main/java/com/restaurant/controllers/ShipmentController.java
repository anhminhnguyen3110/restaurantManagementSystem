package com.restaurant.controllers;

import com.restaurant.dtos.shipment.CreateShipmentDto;
import com.restaurant.dtos.shipment.GetShipmentDto;
import com.restaurant.dtos.shipment.UpdateShipmentDto;
import com.restaurant.models.Shipment;

import java.util.List;

public interface ShipmentController {
    void createShipment(CreateShipmentDto createShipmentDto);

    void updateShipment(UpdateShipmentDto updateShipmentDto);

    List<Shipment> findShipments(GetShipmentDto getShipmentDto);
}
