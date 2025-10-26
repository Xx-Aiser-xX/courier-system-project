package org.example.courierscontract.exception;

public class NonUniqueDataException extends RuntimeException{
    public NonUniqueDataException(String message){
        super("Данные не уникальны: " + message);
    }
}
