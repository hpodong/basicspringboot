package com.basicspringboot.filters.exceptions;

public class RequiredLoginException extends Exception {

    public RequiredLoginException() {
        super("로그인이 필요한 서비스입니다.");
    }

    public RequiredLoginException(String message) {
        super(message);
    }
}
