package com.basicspringboot.filters.exceptions;

public class NotFoundIdxException extends RuntimeException{

    public NotFoundIdxException() {
        super("아이디값이 없습니다.");
    }
}
