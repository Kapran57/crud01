package com.example.crud2.exception;

import com.example.crud2.entity.OrderStatus;

public class InvalidOrderStatusException extends RuntimeException {

    public InvalidOrderStatusException(String status) {
        super(String.format("Некорректный статус заказа: '%s'. Допустимые значения: NEW, PROCESSING, COMPLETED, CANCELED", status));
    }

    public InvalidOrderStatusException(OrderStatus currentStatus, OrderStatus newStatus) {
        super(String.format("Невозможно изменить статус заказа с %s на %s", currentStatus, newStatus));
    }
}