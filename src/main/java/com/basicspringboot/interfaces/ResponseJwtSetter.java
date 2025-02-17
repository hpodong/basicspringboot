package com.basicspringboot.interfaces;

import com.basicspringboot.dto.ResponseDTO;

public interface ResponseJwtSetter {
    void setResponse(ResponseDTO res, Long memberId);
}
