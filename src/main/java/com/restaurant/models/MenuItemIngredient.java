package com.restaurant.models;

import com.restaurant.constants.IngredientUnit;
import jakarta.persistence.*;

@Entity
@Table(name = "menu_item_ingredients", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"menu_item_id", "stock_id"})
})
public class MenuItemIngredient extends BaseModel {

    @ManyToOne(optional = false)
    @JoinColumn(name = "menu_item_id")
    private MenuItem menuItem;

    @ManyToOne(optional = false)
    @JoinColumn(name = "stock_id")
    private Stock stock;

    @Column(nullable = false)
    private int quantityRequired; // e.g. 200 grams, 1 unit

    @Column(nullable = false)
    private IngredientUnit unit; // e.g. grams, units, liters

    public MenuItemIngredient() {
    }

    public MenuItemIngredient(MenuItem menuItem, Stock stock, int quantityRequired) {
        this.menuItem = menuItem;
        this.stock = stock;
        this.quantityRequired = quantityRequired;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public int getQuantityRequired() {
        return quantityRequired;
    }

    public void setQuantityRequired(int quantityRequired) {
        this.quantityRequired = quantityRequired;
    }

    public IngredientUnit getUnit() {
        return unit;
    }

    public void setUnit(IngredientUnit unit) {
        this.unit = unit;
    }
}
