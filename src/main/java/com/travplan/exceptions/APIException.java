package com.travplan.exceptions;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class APIException extends RuntimeException{

    private Map<String, Object> metadata;

    public APIException(String msg) {
        this(msg, null);
    }

    public APIException(String msg, Object metadata) {
        super(msg);
        this.metadata = new HashMap<>();
        this.metadata.put("data", metadata);
    }
}
