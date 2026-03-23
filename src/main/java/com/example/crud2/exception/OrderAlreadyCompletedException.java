package com.example.crud2.exception;

import com.example.crud2.entity.OrderStatus;

public class OrderAlreadyCompletedException extends RuntimeException {

    public OrderAlreadyCompletedException(Long orderId) {
        super(String.format("Заказ с id %d уже завершен и не может быть изменен", orderId));
    }

    public OrderAlreadyCompletedException(Long orderId, OrderStatus status) {
        super(String.format("Заказ с id %d имеет статус %s и не может быть изменен", orderId, status));
    }
}