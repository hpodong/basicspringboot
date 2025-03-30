package com.basicspringboot.exceptions;

public class NotFoundWhereException extends RuntimeException{

    public NotFoundWhereException() {
        super("WHERE 절이 비어있습니다.");
    }
}
