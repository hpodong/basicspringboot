package com.basicspringboot.exceptions;


public class BSValidationException extends RuntimeException{
    public BSValidationException(String msg) {
        super(msg);
    }
}
