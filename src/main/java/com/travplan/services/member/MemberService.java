package com.travplan.services.member;

import com.google.common.net.HttpHeaders;
import com.travplan.dto.BSQuery;
import com.travplan.dto.MemberAgreementReq;
import com.travplan.dto.SignupDTO;
import com.travplan.enums.MemberStatus;
import com.travplan.exceptions.APIException;
import com.travplan.exceptions.InsertException;
import com.travplan.interfaces.InsertSetter;
import com.travplan.interfaces.SheetSetter;
import com.travplan.models.member.Member;
import com.travplan.models.member.MemberSocial;
import com.travplan.providers.JwtProvider;
import com.travplan.services._BSService;
import com.travplan.utils.Utils;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

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

    public Map<String, Object> reissueToken(String refreshToken) throws ExpiredJwtException {
        final BSQuery<Member> bsq = new BSQuery<>(Member.class);
        jwtProvider.getTokenData(refreshToken);
        bsq.setSelect("m_idx");
        bsq.setWhere("m_dldt IS NULL", "m_rftk = ?");
        bsq.setLimit(1);
        final Long idx = slave.queryForObject(bsq.toSql(), Long.class, refreshToken);
        return loginHandler(idx, refreshToken);
    }

    public Member findByEmail(String id) {
        final BSQuery<Member> bsq = new BSQuery<>(Member.class);
        bsq.setWhere("m_email = ?", "m_dldt IS NULL");
        bsq.addArgs(id);
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
            throw new APIException("연결된 회원 데이터가 없습니다.");
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
        req.getMember().setPassword(passwordEncoder.encode(req.getMember().getPassword()));
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

    public void downloadExcel(BSQuery bsq) throws IOException {
        bsq.setLimit(null);
        final List<Member> list = findAll(bsq, Member::new);
        downloadExcel(list, new SheetSetter() {
            int rowIndex = 1;

            @Override
            public String setFileName() {
                return "개인 회원 관리";
            }

            @Override
            public void setHeaders(List<String> headers) {
                headers.add("번호");
                headers.add("가입경로");
                headers.add("아이디");
                headers.add("이름");
                headers.add("연락처");
                headers.add("최근 로그인일자");
                headers.add("가입일");
            }

            @Override
            public void setRows(int index, Row row, Workbook workbook) {
                final Member member = list.get(index);
                final CellStyle centerStyle = getCenterStyle(workbook);
                final CellStyle leftStyle = getLeftStyle(workbook);

                row.createCell(0).setCellValue(rowIndex++);
                row.createCell(1).setCellValue(member.socialsToString());
                row.createCell(2).setCellValue(member.getId());
                row.createCell(3).setCellValue(member.getName());
                row.createCell(4).setCellValue(member.getCell());
                row.createCell(5).setCellValue(Utils.formatTimestamp(member.getLatest_logged_at(), "yyyy-MM-dd HH:mm:ss"));
                row.createCell(6).setCellValue(Utils.formatTimestamp(member.getCreated_at(), "yyyy-MM-dd HH:mm:ss"));

                row.getCell(0).setCellStyle(centerStyle);
                row.getCell(1).setCellStyle(centerStyle);
                row.getCell(2).setCellStyle(leftStyle);
                row.getCell(3).setCellStyle(centerStyle);
                row.getCell(4).setCellStyle(centerStyle);
                row.getCell(5).setCellStyle(centerStyle);
                row.getCell(6).setCellStyle(centerStyle);
            }

            @Override
            public void setSheet(Sheet sheet) {
                sheet.setColumnWidth(0, 10 * BASIC_WIDTH);
                sheet.setColumnWidth(1, 15 * BASIC_WIDTH);
                sheet.setColumnWidth(2, 30 * BASIC_WIDTH);
                sheet.setColumnWidth(3, 20 * BASIC_WIDTH);
                sheet.setColumnWidth(4, 20 * BASIC_WIDTH);
                sheet.setColumnWidth(5, 20 * BASIC_WIDTH);
                sheet.setColumnWidth(6, 20 * BASIC_WIDTH);
            }

            @Override
            public void onSuccess(Workbook workbook, String filename) throws IOException {
                response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
                response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename + ".xlsx");
                response.setCharacterEncoding("UTF-8");

                workbook.write(response.getOutputStream());
                workbook.close();
            }
        });
    }
}
