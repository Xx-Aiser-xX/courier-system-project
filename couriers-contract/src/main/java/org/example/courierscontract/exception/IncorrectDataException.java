package org.example.courierscontract.exception;

public class IncorrectDataException extends RuntimeException {
    public IncorrectDataException(String message) {
        super("Некорректные данные: " + message);
    }
}
