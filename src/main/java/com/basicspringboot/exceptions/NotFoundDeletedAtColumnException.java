package com.basicspringboot.exceptions;

public class NotFoundDeletedAtColumnException extends RuntimeException{

    public NotFoundDeletedAtColumnException() {
        super("deletedAt 컬럼이 없습니다.");
    }
}
