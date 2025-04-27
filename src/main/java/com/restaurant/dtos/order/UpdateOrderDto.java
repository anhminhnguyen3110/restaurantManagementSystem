package com.restaurant.dtos.order;

import com.restaurant.constants.OrderStatus;

public class UpdateOrderDto extends CreateOrderDto {
    private int orderId;
    private OrderStatus status = OrderStatus.PENDING;

    public UpdateOrderDto() {
        super();
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
