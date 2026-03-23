package com.example.crud2.exception;

public class OrderItemNotFoundException extends RuntimeException {

  public OrderItemNotFoundException(Long orderId, Long productId) {
    super(String.format("Товар с id %d не найден в заказе с id %d", productId, orderId));
  }

  public OrderItemNotFoundException(Long id) {
    super(String.format("Позиция заказа с id %d не найдена", id));
  }

  public OrderItemNotFoundException(String message) {
    super(message);
  }
}