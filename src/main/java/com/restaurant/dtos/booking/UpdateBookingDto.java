package com.restaurant.dtos.booking;

public class UpdateBookingDto extends CreateBookingDto {
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
