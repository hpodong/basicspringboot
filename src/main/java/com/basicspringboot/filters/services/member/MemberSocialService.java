package com.basicspringboot.filters.services.member;

import com.basicspringboot.filters.dto.BSQuery;
import com.basicspringboot.filters.enums.SocialType;
import com.basicspringboot.filters.models.member.MemberSocial;
import com.basicspringboot.filters.services._BSService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Getter
public class MemberSocialService extends _BSService<MemberSocial> {

    public MemberSocial findByTypeAndId(SocialType type, String id) {
        final BSQuery<MemberSocial> bsq = new BSQuery<>(MemberSocial.class);
        bsq.addWhere("ms_type = ?", "ms_id = ?");
        bsq.addArgs(type.getValue(), id);
        return findOne(bsq, MemberSocial::new);
    }
}
