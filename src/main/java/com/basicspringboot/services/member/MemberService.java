package com.basicspringboot.services.member;

import com.basicspringboot.dto.BSQuery;
import com.basicspringboot.dto.MemberAgreementReq;
import com.basicspringboot.dto.SignupDTO;
import com.basicspringboot.enums.MemberStatus;
import com.basicspringboot.exceptions.APIException;
import com.basicspringboot.exceptions.InsertException;
import com.basicspringboot.interfaces.InsertSetter;
import com.basicspringboot.models.member.Member;
import com.basicspringboot.models.member.MemberSocial;
import com.basicspringboot.providers.JwtProvider;
import com.basicspringboot.services._BSService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService extends _BSService<Member> {

    private final MemberSocialService socialService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public Member findByIdx(Long idx) {
        final BSQuery<Member> bsq = new BSQuery<>(Member.class);
        bsq.setIdx(idx);
        bsq.setSelect("*");
        bsq.addJoin("LEFT JOIN file ON file.f_idx = mb.f_idx");
        return findOne(bsq, Member::new);
    }

    public Member findById(String id) {
        final BSQuery<Member> bsq = new BSQuery<>(Member.class);
        bsq.setWhere("m_id = ?", "m_dldt IS NULL");
        bsq.addArgs(id);
        return findOne(bsq, Member::new);
    }

    public List<Member> findByCell(String cell) {
        final BSQuery<Member> bsq = new BSQuery<>(Member.class);
        bsq.setSelect("*");
        bsq.addJoin("LEFT JOIN member_social ON member_social.m_idx = member.m_idx");
        bsq.setWhere("m_cell = ?", "m_dldt IS NULL");
        bsq.addArgs(cell);
        bsq.setLimit(null);
        return findAll(bsq, Member::new);
    }

    public Map<String, Object> reissueToken(String refreshToken) throws ExpiredJwtException {
        final BSQuery<Member> bsq = new BSQuery<>(Member.class);
        jwtProvider.getTokenData(refreshToken);
        bsq.setSelect("m_idx");
        bsq.setWhere("m_dldt IS NULL", "m_rftk = ?");
        bsq.setLimit(1);
        final Long idx = slave.queryForObject(bsq.toSql(), Long.class, refreshToken);
        return loginHandler(idx, refreshToken);
    }

    public Member findByEmail(String email) {
        final BSQuery<Member> bsq = new BSQuery<>(Member.class);
        bsq.setWhere("m_email = ?", "m_dldt IS NULL");
        bsq.addArgs(email);
        return findOne(bsq, Member::new);
    }

    @Transactional
    public Map<String, Object> login(String email, String password) throws APIException {
        final Member member = findByEmail(email);
        if(member == null) throw new APIException("이메일을 확인해주세요.");
        else if(!member.getStatus().equals(MemberStatus.ACTIVE)) {
            switch (member.getStatus()) {
                case MemberStatus.LEAVED -> throw new APIException("탈퇴한 계정입니다.");
                case MemberStatus.BANNED -> throw new APIException("정지된 계정입니다.");
            }
        }
        else if(!passwordEncoder.matches(password, member.getPassword())) throw new APIException("비밀번호가 일치하지 않습니다.");
        return loginHandler(member.getIdx());
    }

    @Transactional
    public Map<String, Object> login(MemberSocial social) throws APIException {
        final MemberSocial foundSocial = socialService.findByTypeAndId(social.getType(), social.getId());
        if(foundSocial == null || foundSocial.getMember_idx() == null) {
            throw new APIException("연결된 회원 데이터가 없습니다", "회원가입을 진행해주세요");
        }

        return loginHandler(foundSocial.getIdx());
    }

    private Map<String, Object> loginHandler(Long idx) {
        return loginHandler(idx, null);
    }

    private Map<String, Object> loginHandler(Long idx, String refreshToken) {
        final Map<String, Object> data = new LinkedHashMap<>();
        if(refreshToken == null) {
            refreshToken = jwtProvider.createRefreshToken();
            updateRefreshToken(idx, refreshToken);
        }
        data.put("accessToken", jwtProvider.createAccessToken(idx));
        data.put("refreshToken", refreshToken);
        return data;
    }

    @Transactional
    public Map<String, Object> signup(SignupDTO req) {
        if(req.getMember().getPassword() != null) req.getMember().setPassword(passwordEncoder.encode(req.getMember().getPassword()));
        final Long idx = insertReturnKey(req.getMember());
        if(idx == null) throw new InsertException();
        insertAgreements(req.getAgreements(), idx);
        if(req.getSocial() != null) {
            req.getSocial().setMember_idx(idx);
            socialService.insert(req.getSocial());
        }
        return loginHandler(idx);
    }

    public void insertAgreements(List<MemberAgreementReq> agreement, Long memberIdx) {
        final int length = agreement.size();
        insertMany("member_agreement", length, new InsertSetter() {
            @Override
            public void columnSetter(List<String> columns) {
                columns.add("agm_idx");
                columns.add("m_idx");
                columns.add("ma_is_allow");
            }

            @Override
            public void valueSetter(int index, List<Object> values) {
                final MemberAgreementReq req = agreement.get(index);
                values.add(req.getAgreement_idx());
                values.add(memberIdx);
                values.add(req.isAllow());
            }
        });
    }

    public boolean insert(Member member) {
        return super.insert(Member.class, data -> data.putAll(member.toInput()));
    }

    public boolean update(Member member) {
        final String where = "WHERE m_idx = " + member.getIdx();
        return super.update(Member.class, where, data -> data.putAll(member.toInput()));
    }

    private void updateRefreshToken(Long idx, String refreshToken) {
        update(Member.class, "WHERE m_idx = "+idx, setter -> setter.put("m_rftk", refreshToken));
    }

    @Transactional
    public boolean updateStatusLeavedFromMemberIdx(Long idx) {
        final String sql = "UPDATE member SET m_status = 'leaved' WHERE m_idx = ?";
        return master.update(sql, idx) > 0;
    }

    public String socialsToSql() {
        return """
                (
                SELECT GROUP_CONCAT(ms_type) FROM member_social
                WHERE member_social.m_idx = member.m_idx
                ) socials
                """;
    }
}
