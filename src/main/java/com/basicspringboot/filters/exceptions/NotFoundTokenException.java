package com.basicspringboot.filters.exceptions;

public class NotFoundTokenException extends RuntimeException{

    public NotFoundTokenException() {
        super("액세스 토큰이 없습니다.");
    }
}
