package com.restaurant.dtos.order;

import com.restaurant.constants.OrderStatus;

public class UpdateOrderDto extends CreateOrderDto {
    private int id;
    private OrderStatus status = OrderStatus.PENDING;

    public UpdateOrderDto() {
        super();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
