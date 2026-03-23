package com.example.crud2.exception;

public class ProductDeletionException extends RuntimeException {

    public ProductDeletionException(Long productId) {
        super(String.format("Невозможно удалить товар с id %d: он используется в заказах", productId));
    }

    public ProductDeletionException(Long productId, int orderCount) {
        super(String.format("Невозможно удалить товар с id %d: он используется в %d заказах", productId, orderCount));
    }
}
