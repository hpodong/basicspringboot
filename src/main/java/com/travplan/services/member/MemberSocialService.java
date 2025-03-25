package com.travplan.services.member;

import com.travplan.dto.BSQuery;
import com.travplan.enums.SocialType;
import com.travplan.models.member.MemberSocial;
import com.travplan.services._BSService;
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
