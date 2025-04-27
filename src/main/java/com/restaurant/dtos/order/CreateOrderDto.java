package com.restaurant.dtos.order;

import com.restaurant.constants.OrderType;

public class CreateOrderDto {
    private int restaurantTableId;
    private OrderType orderType;

    public CreateOrderDto() {
    }

    public int getRestaurantTableId() {
        return restaurantTableId;
    }

    public void setRestaurantTableId(int restaurantTableId) {
        this.restaurantTableId = restaurantTableId;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }
}
