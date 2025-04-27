package com.restaurant.dtos.order;

import com.restaurant.constants.OrderType;
import com.restaurant.dtos.PaginationDto;
import com.restaurant.models.RestaurantTable;

public class GetOrderDto extends PaginationDto {
    private RestaurantTable restaurantTable;
    private OrderType orderType;
    private double totalPrice;
    private int totalItems;

    public GetOrderDto() {
        super();
    }

    public RestaurantTable getRestaurantTable() {
        return restaurantTable;
    }

    public void setRestaurantTable(RestaurantTable restaurantTable) {
        this.restaurantTable = restaurantTable;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }
}
