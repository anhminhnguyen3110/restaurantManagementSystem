package com.restaurant.models;

import com.restaurant.constants.OrderStatus;
import com.restaurant.constants.OrderType;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order extends BaseModel {

    @OneToOne(
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            optional = true
    )
    @JoinColumn(name = "restaurant_table_id")
    private RestaurantTable restaurantTable;

    @ManyToMany
    @JoinTable(
            name = "order_menu_items",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "menu_item_id")
    )
    private List<OrderItem> items = new ArrayList<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Payment payment;

    @Column(length = 20, nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    private double totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private OrderType orderType;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Shipment shipment;

    public Order() {
    }

    public Order(List<OrderItem> items, OrderType orderType) {
        this.items = items;
        recalcTotal();
        this.orderType = orderType;
    }

    public void recalcTotal() {
        this.totalPrice = items.stream()
                .mapToDouble(item -> item.getMenuItem().getPrice())
                .sum();
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
        recalcTotal();
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double price) {
        this.totalPrice = price;
    }

    public RestaurantTable getRestaurantTable() {
        return restaurantTable;
    }

    public void setRestaurantTable(RestaurantTable restaurantTable) {
        this.restaurantTable = restaurantTable;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public Shipment getShipment() {
        return shipment;
    }

    public void setShipment(Shipment s) {
        this.shipment = s;
    }
}
