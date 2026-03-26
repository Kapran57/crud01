package com.example.crud2.exception;

public class ClientNotFoundException extends RuntimeException {

    public ClientNotFoundException(Long id) {
        super(String.format("Пользователь с id %d не найден", id));
    }


}
