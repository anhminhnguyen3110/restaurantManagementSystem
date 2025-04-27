package com.restaurant.dtos.shipment;

import com.restaurant.constants.ShipmentService;
import com.restaurant.constants.ShipmentStatus;
import com.restaurant.dtos.PaginationDto;

public class GetShipmentDto extends PaginationDto {
    private ShipmentService serviceType;
    private int orderId;
    private String shipperName;
    private String customerName;
    private ShipmentStatus status;
    private String trackingNumber;

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

    public String getShipperName() {
        return shipperName;
    }

    public void setShipperName(String shipperName) {
        this.shipperName = shipperName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public ShipmentStatus getStatus() {
        return status;
    }

    public void setStatus(ShipmentStatus status) {
        this.status = status;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }
}
