package com.restaurant.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "restaurant_tables",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"restaurant_id", "start_x", "start_y"}),
                @UniqueConstraint(columnNames = {"restaurant_id", "end_x", "end_y"}),
                @UniqueConstraint(columnNames = {"restaurant_id", "table_number"})
        },
        indexes = {
                @Index(name = "restaurant_idx", columnList = "restaurant_id"),
        }
)
public class RestaurantTable extends BaseModel {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(name = "table_number", nullable = false)
    private int number;

    @Column(name = "capacity", nullable = false)
    private int capacity;

    @Column(name = "start_x", nullable = false)
    private int startX;

    @Column(name = "start_y", nullable = false)
    private int startY;

    @Column(name = "end_x", nullable = false)
    private int endX;

    @Column(name = "end_y", nullable = false)
    private int endY;

    @Column(name = "available", nullable = false)
    private boolean available = true;

    @OneToMany(mappedBy = "table", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings = new ArrayList<>();

    @OneToMany(
            mappedBy = "restaurantTable",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Order> orders = new ArrayList<>();

    public RestaurantTable() {
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public int getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public int getEndX() {
        return endX;
    }

    public void setEndX(int endX) {
        this.endX = endX;
    }

    public int getEndY() {
        return endY;
    }

    public void setEndY(int endY) {
        this.endY = endY;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    @Override
    public String toString() {
        return "Table #" + number +
                " (number of people: " + capacity +
                ", restaurant: " + restaurant.getName() +
                ")";
    }
}
