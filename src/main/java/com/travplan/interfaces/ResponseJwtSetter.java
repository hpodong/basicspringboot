package com.travplan.interfaces;

import com.travplan.dto.ResponseDTO;

public interface ResponseJwtSetter {
    void setResponse(ResponseDTO res, Long memberId);
}
