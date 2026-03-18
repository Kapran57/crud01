package com.example.crud2.exception;

public class DatabaseConstraintException extends RuntimeException {

    public DatabaseConstraintException(String message) {
        super(message);
    }

    public DatabaseConstraintException(String constraint, String value) {
        super(String.format("Нарушение целостности базы данных: %s со значением '%s' уже существует",
                constraint, value));
    }
}
