package com.ey.EY.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException() {
        super("El correo ya registrado");
    }
}
