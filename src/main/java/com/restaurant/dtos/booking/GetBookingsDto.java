package com.restaurant.dtos.booking;

import com.restaurant.constants.BookingStatus;
import com.restaurant.constants.BookingTimeSlot;
import com.restaurant.dtos.PaginationDto;

import java.time.LocalDate;

public class GetBookingsDto extends PaginationDto {
    private String customerName;
    private String phoneNumber;
    private Integer tableNumber;
    private BookingStatus status;
    private LocalDate date;
    private BookingTimeSlot startTime;
    private BookingTimeSlot endTime;

    public GetBookingsDto() {
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

    public Integer getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(Integer tableNumber) {
        this.tableNumber = tableNumber;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
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
}
