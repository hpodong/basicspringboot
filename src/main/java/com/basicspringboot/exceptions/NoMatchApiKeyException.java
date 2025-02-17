package com.basicspringboot.exceptions;

public class NoMatchApiKeyException extends RuntimeException{

    public NoMatchApiKeyException() {
        super("API 키값이 일치하지 않습니다.");
    }
}
