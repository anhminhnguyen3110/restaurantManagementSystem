package com.restaurant.dtos.menuItem;

import com.restaurant.dtos.PaginationDto;

public class GetMenuItemsDto extends PaginationDto {
    private String name;
    private double moreThanPrice;
    private double lessThanPrice;
    private int restaurantId;
    private int menuId;

    public GetMenuItemsDto() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMoreThanPrice() {
        return moreThanPrice;
    }

    public void setMoreThanPrice(double moreThanPrice) {
        this.moreThanPrice = moreThanPrice;
    }

    public double getLessThanPrice() {
        return lessThanPrice;
    }

    public void setLessThanPrice(double lessThanPrice) {
        this.lessThanPrice = lessThanPrice;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }
}
