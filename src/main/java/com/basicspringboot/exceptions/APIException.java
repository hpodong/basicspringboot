package com.basicspringboot.exceptions;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class APIException extends RuntimeException{

    private String error;
    private Map<String, Object> metadata;

    public APIException(String msg) {
        this(msg, null, null);
    }

    public APIException(String msg, String error) {
        this(msg, error, null);
    }

    public APIException(String msg, String error, Object metadata) {
        super(msg);
        this.error = error;
        this.metadata = new HashMap<>();
        this.metadata.put("data", metadata);
    }
}
