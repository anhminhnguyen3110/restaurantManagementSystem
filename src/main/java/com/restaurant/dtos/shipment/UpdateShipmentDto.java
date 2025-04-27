package com.restaurant.dtos.shipment;

import com.restaurant.constants.ShipmentStatus;

public class UpdateShipmentDto extends CreateShipmentDto {
    private int id;
    private ShipmentStatus status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ShipmentStatus getStatus() {
        return status;
    }

    public void setStatus(ShipmentStatus status) {
        this.status = status;
    }
}
