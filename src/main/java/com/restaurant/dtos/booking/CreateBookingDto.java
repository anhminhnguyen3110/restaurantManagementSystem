package com.restaurant.dtos.booking;

import com.restaurant.constants.BookingTimeSlot;

import java.time.LocalDate;

public class CreateBookingDto {
    private LocalDate date;
    private BookingTimeSlot startTime;
    private BookingTimeSlot endTime;
    private int tableId;
    private String customerName;
    private String customerPhoneNumber;
    private String customerEmail;

    public CreateBookingDto() {}

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

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
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