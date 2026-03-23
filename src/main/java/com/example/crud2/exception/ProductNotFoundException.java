package com.example.crud2.exception;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(Long id) {
        super(String.format("Товар с id %d не найден", id));
    }

    public ProductNotFoundException(String name) {
        super(String.format("Товар с названием '%s' не найден", name));
    }
}