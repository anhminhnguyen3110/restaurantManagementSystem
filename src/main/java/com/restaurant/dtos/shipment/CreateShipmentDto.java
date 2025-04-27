package com.restaurant.dtos.shipment;

import com.restaurant.constants.ShipmentService;

public class CreateShipmentDto {
    private ShipmentService serviceType;
    private int orderId;
    private int shipperId;
    private int customerId;

    public CreateShipmentDto() {
    }

    public ShipmentService getServiceType() {
        return serviceType;
    }

    public void setServiceType(ShipmentService serviceType) {
        this.serviceType = serviceType;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getShipperId() {
        return shipperId;
    }

    public void setShipperId(int shipperId) {
        this.shipperId = shipperId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
}
