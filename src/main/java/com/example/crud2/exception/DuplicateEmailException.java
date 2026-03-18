package com.example.crud2.exception;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super(String.format("Email %s уже зарегистрирован", email));
    }
}
