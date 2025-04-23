package com.restaurant.models;

import com.restaurant.constants.ShipmentService;
import com.restaurant.constants.ShipmentStatus;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "shipments", indexes = {
        @Index(columnList = "order_id"),
        @Index(columnList = "status"),
        @Index(columnList = "service_type"),
        @Index(columnList = "shipper_id")
})
public class Shipment extends BaseModel {
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false)
    private ShipmentService serviceType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shipper_id")
    private User shipper;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentStatus status = ShipmentStatus.SHIPPING;

    @Column(unique = true)
    private String trackingNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    public Shipment() {
        this.trackingNumber = UUID.randomUUID().toString();
    }

    public Shipment(Order order, ShipmentService serviceType, Customer customer) {
        this();
        this.order = order;
        this.serviceType = serviceType;
        this.customer = customer;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public ShipmentService getServiceType() {
        return serviceType;
    }

    public void setServiceType(ShipmentService serviceType) {
        this.serviceType = serviceType;
    }

    public User getShipper() {
        return shipper;
    }

    public void setShipper(User shipper) {
        this.shipper = shipper;
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

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}