package com.example.crud2.exception;

import com.example.crud2.entity.OrderStatus;

public class InvalidOrderStatusException extends RuntimeException {

    public InvalidOrderStatusException(OrderStatus currentStatus, OrderStatus newStatus) {
        super(String.format("Невозможно изменить статус заказа с %s на %s", currentStatus, newStatus));
    }
}