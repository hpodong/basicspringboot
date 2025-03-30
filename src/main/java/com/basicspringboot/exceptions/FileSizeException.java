package com.basicspringboot.exceptions;

public class FileSizeException extends RuntimeException{
    public FileSizeException(Integer maxSize) {
        super("최대 사이즈는 "+maxSize/(1024*1024)+"MB 입니다.");
    }
}
