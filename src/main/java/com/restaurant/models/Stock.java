package com.restaurant.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stocks")
public class Stock extends BaseModel {
    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MenuItemIngredient> usedIn = new ArrayList<>();

    @Column(nullable = false)
    private int quantity;

    @Column(name = "min_threshold", nullable = false)
    private int minThreshold = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    public Stock() {
    }

    public Stock(int quantity, int minThreshold) {
        this.quantity = quantity;
        this.minThreshold = minThreshold;
    }

    public List<MenuItemIngredient> getUsedIn() {
        return usedIn;
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

    public void setUsedIn(List<MenuItemIngredient> usedIn) {
        this.usedIn = usedIn;
    }
}
