package com.restaurant.models;

import com.restaurant.constants.OrderItemStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem extends BaseModel {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    @Column(columnDefinition = "TEXT")
    private String customization;

    private int quantity = 1;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private OrderItemStatus status = OrderItemStatus.PENDING;

    public OrderItem() {
    }

    public OrderItem(Order order, MenuItem menuItem, int quantity) {
        this.order = order;
        this.menuItem = menuItem;
        this.quantity = quantity;
    }

    public OrderItem(Order order, MenuItem menuItem, int quantity, String customization) {
        this.order = order;
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.customization = customization;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCustomization() {
        return customization;
    }

    public void setCustomization(String customization) {
        this.customization = customization;
    }

    public OrderItemStatus getStatus() {
        return status;
    }

    public void setStatus(OrderItemStatus status) {
        this.status = status;
    }
}