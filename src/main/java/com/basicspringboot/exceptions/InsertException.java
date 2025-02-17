package com.basicspringboot.exceptions;

public class InsertException extends RuntimeException{
    public InsertException() {
        super("입력된 데이터가 없습니다.");
    }
}
