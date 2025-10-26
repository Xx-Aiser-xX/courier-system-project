package org.example.couriers.exception;

public class IncorrectDataException extends RuntimeException{
    public IncorrectDataException(String message){
        super("Некорректные данные: " + message);
    }
}