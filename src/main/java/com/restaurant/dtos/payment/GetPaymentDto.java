package com.restaurant.dtos.payment;

import com.restaurant.constants.PaymentMethod;
import com.restaurant.constants.PaymentStatus;
import com.restaurant.dtos.PaginationDto;

public class GetPaymentDto extends PaginationDto {
    private int orderId;
    private PaymentMethod method;
    private PaymentStatus status;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
}
