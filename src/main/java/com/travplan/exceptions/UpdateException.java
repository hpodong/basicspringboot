package com.travplan.exceptions;

public class UpdateException extends RuntimeException{
    public UpdateException() {
        super("수정된 데이터가 없습니다.");
    }
}
