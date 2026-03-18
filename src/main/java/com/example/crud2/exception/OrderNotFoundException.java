package com.example.crud2.exception;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(Long id) {
        super(String.format("Заказ с id %d не найден", id));
    }
}
