package com.example.crud2.exception;

public class DuplicateProductNameException extends RuntimeException {

    public DuplicateProductNameException(String name) {
        super(String.format("Товар с названием '%s' уже существует", name));
    }
}