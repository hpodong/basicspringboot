package com.basicspringboot.filters.exceptions;


public class BSValidationException extends RuntimeException{
    public BSValidationException(String msg) {
        super(msg);
    }
}
