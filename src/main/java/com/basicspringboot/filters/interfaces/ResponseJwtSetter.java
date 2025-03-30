package com.basicspringboot.filters.interfaces;

import com.basicspringboot.filters.dto.ResponseDTO;

public interface ResponseJwtSetter {
    void setResponse(ResponseDTO res, Long memberId);
}
