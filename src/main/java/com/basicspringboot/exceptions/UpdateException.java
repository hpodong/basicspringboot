package com.basicspringboot.exceptions;

public class UpdateException extends RuntimeException{

    public UpdateException(String msg) {
        super(msg);
    }
    public UpdateException() {
        super("수정된 데이터가 없습니다.");
    }
}
