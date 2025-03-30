package com.basicspringboot.filters.dto;

import com.basicspringboot.filters.models.member.Member;
import com.basicspringboot.filters.models.member.MemberSocial;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

@RequiredArgsConstructor
@ToString
@Getter
public class SignupDTO {

    private Member member;
    private List<MemberAgreementReq> agreements;
    private MemberSocial social;
}
