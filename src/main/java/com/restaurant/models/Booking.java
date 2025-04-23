package com.restaurant.models;

import com.restaurant.constants.BookingDuration;
import com.restaurant.constants.BookingStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings", indexes = @Index(columnList = "start_time"))
public class Booking extends BaseModel {
    @Column(name = "start_time", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime start;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingDuration duration = BookingDuration.ONE_HOUR;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "table_id", nullable = false)
    private RestaurantTable table;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private BookingStatus status = BookingStatus.BOOKED;

    public Booking() {
    }

    public Booking(LocalDateTime start, BookingDuration duration, RestaurantTable table) {
        this.start = start;
        this.duration = duration;
        this.table = table;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime s) {
        this.start = s;
    }

    public BookingDuration getDuration() {
        return duration;
    }

    public void setDuration(BookingDuration d) {
        this.duration = d;
    }

    public RestaurantTable getTable() {
        return table;
    }

    public void setTable(RestaurantTable t) {
        this.table = t;
    }

    public LocalDateTime getEnd() {
        return duration.addTo(start);
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