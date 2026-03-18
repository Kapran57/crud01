package com.example.crud2.exception;

public class UserDeletionException extends RuntimeException {

    public UserDeletionException(Long userId, int ordersCount) {
        super(String.format("Невозможно удалить пользователя с id %d: у него есть %d активных заказов",
                userId, ordersCount));
    }
}