package com.restaurant.dtos.shipment;

import com.restaurant.constants.ShipmentService;

public class CreateShipmentDto {
    private ShipmentService serviceType;
    private int orderId;
    private int shipperId;
    private String customerAddress;
    private String customerPhone;
    private String customerName;
    private String customerEmail;

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

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
}
