package com.basicspringboot.services.member;

import com.google.common.net.HttpHeaders;
import com.basicspringboot.dto.BSQuery;
import com.basicspringboot.interfaces.SheetSetter;
import com.basicspringboot.models.member.Member;
import com.basicspringboot.services._BSService;
import com.basicspringboot.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService extends _BSService<Member> {

    public Member findByIdx(Long idx) {
        final BSQuery bsq = new BSQuery(Member.class);
        bsq.setIdx(idx);
        bsq.setSelect("*");
        bsq.addJoin("LEFT JOIN file ON file.f_idx = mb.f_idx");
        return findOne(bsq, Member::new);
    }

    public boolean insert(Member member) {
        return super.insert(Member.class, data -> data.putAll(member.toInput()));
    }

    public boolean update(Member member) {
        final String where = "WHERE m_idx = " + member.getIdx();
        return super.update(Member.class, where, data -> data.putAll(member.toInput()));
    }

    @Transactional
    public boolean updateStatusLeavedFromMemberIdx(Long idx) {
        final String sql = "UPDATE member SET m_status = 'leaved' WHERE m_idx = ?";
        return jt.update(sql, idx) > 0;
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
