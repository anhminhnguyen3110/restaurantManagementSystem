package com.restaurant.models;

import java.util.List;

public class Order {
    private int id;
    private int bookingId;
    private List<Integer> menuItemIds; // Assume this maps to a junction table in DB
    private String status; // e.g., "PENDING", "PROCESSING", "COMPLETED", "CANCELLED"
    private double totalPrice;

    // Constructors
    public Order() {}

    public Order(int id, int bookingId, List<Integer> menuItemIds, String status, double totalPrice) {
        this.id = id;
        this.bookingId = bookingId;
        this.menuItemIds = menuItemIds;
        this.status = status;
        this.totalPrice = totalPrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public List<Integer> getMenuItemIds() {
        return menuItemIds;
    }

    public void setMenuItemIds(List<Integer> menuItemIds) {
        this.menuItemIds = menuItemIds;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}