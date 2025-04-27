package com.restaurant.dtos.restaurant;

import com.restaurant.constants.RestaurantStatus;

public class UpdateRestaurantDto extends CreateRestaurantDto {
    private int id;

    private RestaurantStatus status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public RestaurantStatus getStatus() {
        return status;
    }

    public void setStatus(RestaurantStatus status) {
        this.status = status;
    }
}
