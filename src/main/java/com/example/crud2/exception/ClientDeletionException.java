package com.example.crud2.exception;

public class ClientDeletionException extends RuntimeException {

    public ClientDeletionException(Long clientId, int ordersCount) {
        super(String.format("Невозможно удалить пользователя с id %d: у него есть %d активных заказов",
                clientId, ordersCount));
    }
}