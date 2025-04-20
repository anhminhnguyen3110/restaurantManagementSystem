package com.restaurant.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stocks")
public class Stock extends BaseModel {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false, unique = true)
    private MenuItem menuItem;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "min_threshold", nullable = false)
    private int minThreshold = 0;

    @Column(name = "last_updated", columnDefinition = "TIMESTAMP")
    private LocalDateTime lastUpdated;

    public Stock() {}

    public Stock(MenuItem menuItem, int quantity, int minThreshold) {
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.minThreshold = minThreshold;
        this.lastUpdated = LocalDateTime.now();
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

    public int getMinThreshold() {
        return minThreshold;
    }

    public void setMinThreshold(int minThreshold) {
        this.minThreshold = minThreshold;
    }

    public boolean isLowStock() {
        return quantity <= minThreshold;
    }

    public void increase(int amount) {
        this.quantity += amount;
    }

    public void decrease(int amount) {
        this.quantity = Math.max(0, this.quantity - amount);
    }
}
