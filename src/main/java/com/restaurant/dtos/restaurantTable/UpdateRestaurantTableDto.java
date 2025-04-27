package com.restaurant.dtos.restaurantTable;

public class UpdateRestaurantTableDto extends CreateRestaurantTableDto {
    private int id;

    public UpdateRestaurantTableDto() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
