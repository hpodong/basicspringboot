package com.travplan.exceptions;

public class NotFoundApiKeyException extends RuntimeException{

    public NotFoundApiKeyException() {
        super("API 키를 찾을 수 없습니다.");
    }
}
