package com.restaurant.dtos.restaurantTable;

import com.restaurant.constants.BookingTimeSlot;

import java.time.LocalDate;

public class GetRestaurantTableForBookingDto extends GetRestaurantTableDto {
    private LocalDate date;
    private BookingTimeSlot startTime;
    private BookingTimeSlot endTime;

    public GetRestaurantTableForBookingDto() {
        super();
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