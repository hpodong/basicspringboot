package com.travplan.exceptions;

public class NotFoundDeletedAtColumnException extends RuntimeException{

    public NotFoundDeletedAtColumnException() {
        super("deletedAt 컬럼이 없습니다.");
    }
}
