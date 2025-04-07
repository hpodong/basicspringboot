package com.basicspringboot.exceptions;

public class DeleteException extends RuntimeException{

    public DeleteException(String msg) {
        super(msg);
    }
    public DeleteException() {
        super("삭제된 데이터가 없습니다.");
    }
}
