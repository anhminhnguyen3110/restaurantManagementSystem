package com.restaurant.dtos.booking;

import com.restaurant.constants.BookingDuration;

import java.time.LocalDateTime;

public class CreateBookingDto {
    private LocalDateTime start;
    private BookingDuration duration;
    private Long tableId;
    private String customerName;
    private String customerPhoneNumber;
    private String customerEmail;

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

    public Long getTableId() {
        return tableId;
    }

    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    public void setCustomerPhoneNumber(String customerPhoneNumber) {
        this.customerPhoneNumber = customerPhoneNumber;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
}
