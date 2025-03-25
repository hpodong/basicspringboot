package com.travplan.exceptions;

public class DeleteException extends RuntimeException{
    public DeleteException() {
        super("삭제된 데이터가 없습니다.");
    }
}
