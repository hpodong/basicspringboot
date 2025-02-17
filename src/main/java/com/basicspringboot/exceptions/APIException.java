package com.basicspringboot.exceptions;

public class APIException extends RuntimeException{
    public APIException(String msg) {
        super(msg);
    }
}
