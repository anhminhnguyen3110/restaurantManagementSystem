package com.restaurant.dtos.menu;

import com.restaurant.dtos.PaginationDto;

public class GetMenuDto extends PaginationDto {
    private String name;
    private String restaurantName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }
}
