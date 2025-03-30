package com.basicspringboot.exceptions;

public class TokenNotEqualTypeException extends RuntimeException{

    public TokenNotEqualTypeException() {
        super("토큰 종류가 일치하지 않습니다.");
    }
}
