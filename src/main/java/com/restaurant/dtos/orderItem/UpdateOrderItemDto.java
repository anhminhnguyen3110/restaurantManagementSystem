package com.restaurant.dtos.orderItem;

import com.restaurant.constants.OrderItemStatus;

public class UpdateOrderItemDto extends CreateOrderItemDto {
    private int id;
    private OrderItemStatus status;

    public UpdateOrderItemDto() {
        super();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public OrderItemStatus getStatus() {
        return status;
    }

    public void setStatus(OrderItemStatus status) {
        this.status = status;
    }
}
