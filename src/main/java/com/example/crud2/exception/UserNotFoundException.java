package com.example.crud2.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super(String.format("Пользователь с id %d не найден", id));
    }

    public UserNotFoundException(String email) {
        super(String.format("Пользователь с email %s не найден", email));
    }
}