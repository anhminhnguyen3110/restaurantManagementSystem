package com.restaurant.dtos.booking;

import com.restaurant.constants.BookingStatus;

public class UpdateBookingDto extends CreateBookingDto {
    private int id;
    private BookingStatus status;

    public UpdateBookingDto() {
        super();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }
}