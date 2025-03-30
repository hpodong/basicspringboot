package com.basicspringboot.exceptions;

public class NotFoundAPIException extends RuntimeException {
    public NotFoundAPIException() {
        super("존재하지 않는 API 주소입니다.");
    }
}
