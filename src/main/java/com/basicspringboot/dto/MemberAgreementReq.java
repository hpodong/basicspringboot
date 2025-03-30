package com.basicspringboot.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
public class MemberAgreementReq {
    private Long agreement_idx;
    private boolean isAllow;
}
