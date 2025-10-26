package org.example.couriers.exception;

public class NonUniqueDataException extends RuntimeException{
    public NonUniqueDataException(String message){
        super("Данные не уникальны: " + message);
    }
}
