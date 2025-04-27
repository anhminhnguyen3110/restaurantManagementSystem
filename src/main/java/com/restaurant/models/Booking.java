package com.restaurant.models;

import com.restaurant.constants.BookingStatus;
import com.restaurant.constants.BookingTimeSlot;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "bookings", indexes = @Index(columnList = "date"))
public class Booking extends BaseModel {
    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "start_time", length = 5, nullable = false)
    private BookingTimeSlot startTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "end_time",   length = 5, nullable = false)
    private BookingTimeSlot endTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "table_id",    nullable = false)
    private RestaurantTable table;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private BookingStatus status = BookingStatus.BOOKED;

    public Booking() {}

    public Booking(LocalDate date, BookingTimeSlot startTime, BookingTimeSlot endTime, RestaurantTable table) {
        this.date      = date;
        this.startTime = startTime;
        this.endTime   = endTime;
        this.table     = table;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BookingTimeSlot getStartTime() {
        return startTime;
    }

    public void setStartTime(BookingTimeSlot startTime) {
        this.startTime = startTime;
    }

    public BookingTimeSlot getEndTime() {
        return endTime;
    }

    public void setEndTime(BookingTimeSlot endTime) {
        this.endTime = endTime;
    }

    public RestaurantTable getTable() {
        return table;
    }

    public void setTable(RestaurantTable table) {
        this.table = table;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }
}