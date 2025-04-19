package com.restaurant.models;

import com.restaurant.constants.BookingDuration;

import java.time.LocalDateTime;

public class Booking {
    private int id;
    private String customerName;
    private String phoneNumber;
    private LocalDateTime start;
    private BookingDuration duration;
    private int tableId;

    // Constructors
    public Booking() {
    }

    public Booking(int id, String customerName, String phoneNumber, LocalDateTime start, BookingDuration duration, int tableId) {
        this.id = id;
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
        this.start = start;
        this.duration = duration;
        this.tableId = tableId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public BookingDuration getDuration() {
        return duration;
    }

    public void setDuration(BookingDuration duration) {
        this.duration = duration;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public LocalDateTime getEnd() {
        return duration.addTo(start);
    }
}
