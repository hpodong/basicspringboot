package com.travplan.dto;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.LinkedHashMap;
import java.util.Map;

@ToString
@Getter
@Setter
@NoArgsConstructor
public class ResponseDTO {
    @Setter(AccessLevel.NONE)
    private HttpStatus status;
    private int statusCode;
    private String message;
    private String error;
    private Object data;
    private Map<String, Object> metadata;

    public void setStatusCode(HttpStatus status) {
        this.statusCode = status.value();
        this.status = status;
    }

    public void setStatusCode(Integer status) {
        this.statusCode = status;
        this.status = HttpStatus.valueOf(status);
    }

    public void addMetadata(String key, Object value) {
        if(metadata == null) metadata = new LinkedHashMap<>();
        metadata.put(key, value);
    }
}
