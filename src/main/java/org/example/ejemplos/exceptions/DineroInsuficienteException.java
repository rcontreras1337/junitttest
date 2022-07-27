package org.example.ejemplos.exceptions;

public class DineroInsuficienteException extends RuntimeException {
    public DineroInsuficienteException(String msg) {
        super(msg);
    }
}
