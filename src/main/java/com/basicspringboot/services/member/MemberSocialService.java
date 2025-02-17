package com.basicspringboot.services.member;

import com.basicspringboot.dto.BSQuery;
import com.basicspringboot.enums.SocialType;
import com.basicspringboot.models.member.MemberSocial;
import com.basicspringboot.services._BSService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Getter
public class MemberSocialService extends _BSService<MemberSocial> {

    private final String MEMBER_SOCIAL_SESSION_KEY = "member_social";

    public boolean insert(MemberSocial data) {
        return super.insert(MemberSocial.class, setter -> setter.putAll(data.toInput()));
    }

    public boolean update(MemberSocial data) {
        return update(data, false);
    }

    public boolean update(MemberSocial data, boolean nullable) {
        final String where = "WHERE ms_idx = "+data.getIdx();
        return super.update(MemberSocial.class, where, setter -> setter.putAll(data.toInput(nullable)));
    }

    public void updateDataFromMemberIdx(Long member_social_idx, Long member_idx) {
        final String sql = "UPDATE member_social SET m_idx = ? WHERE ms_idx = ?";
        jt.update(sql, member_idx, member_social_idx);
    }

    public MemberSocial findFromSocial(SocialType type, String id) {
        final BSQuery bsq = new BSQuery(MemberSocial.class);
        bsq.setWhere("ms_type = ?", "ms_id = ?");
        try {
            return jt.queryForObject(bsq.toSql(), MemberSocial::new, type.getValue(), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Transactional
    public MemberSocial getSocialInfo(MemberSocial requestSocial) {
        final MemberSocial social = findFromSocial(requestSocial.getType(), requestSocial.getId());
        if(social == null) {
            final Long member_social_idx = insertReturnKey(requestSocial);
            requestSocial.setIdx(member_social_idx);
            return requestSocial;
        } else {
            update(requestSocial);
            return social;
        }
    }

    public List<MemberSocial> getSocialListFromMemberIdx(Long idx){
        final BSQuery bsq = new BSQuery(MemberSocial.class);
        bsq.addWhere("m_idx = " + idx);
        return jt.query(bsq.toSql(),(rs, rn) -> new MemberSocial(rs,rn));
    }
}
