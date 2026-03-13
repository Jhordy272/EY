package com.ey.EY.exception;

public class TokenGenerationException extends RuntimeException {
    public TokenGenerationException() {
        super("Error al generar el token de acceso");
    }
}
