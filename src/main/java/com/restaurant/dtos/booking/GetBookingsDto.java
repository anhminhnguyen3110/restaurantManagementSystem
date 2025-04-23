package com.restaurant.dtos.booking;

import com.restaurant.constants.BookingStatus;
import com.restaurant.dtos.PaginationDto;

import java.time.LocalDateTime;

public class GetBookingsDto extends PaginationDto {
    private String customerName;
    private String phoneNumber;
    private Integer tableNumber;
    private BookingStatus status;
    private LocalDateTime from;
    private LocalDateTime to;

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

    public LocalDateTime getFrom() {
        return from;
    }
    public void setFrom(LocalDateTime from) {
        this.from = from;
    }

    public LocalDateTime getTo() {
        return to;
    }
    public void setTo(LocalDateTime to) {
        this.to = to;
    }
}
