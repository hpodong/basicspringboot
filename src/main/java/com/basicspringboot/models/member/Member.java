package com.basicspringboot.models.member;

import com.basicspringboot.annotations.BSColumn;
import com.basicspringboot.annotations.BSTable;
import com.basicspringboot.annotations.BSValidation;
import com.basicspringboot.enums.MemberGender;
import com.basicspringboot.enums.SocialType;
import com.basicspringboot.models._BSModel;
import jakarta.servlet.http.HttpServletRequest;
import lombok.*;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;

@BSTable(name = "member", primaryKey = "m_idx", createdAt = "m_crdt", deletedAt = "m_dldt")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Member extends _BSModel {

    @BSColumn(name = "m_idx")
    private Long idx;

    @BSValidation(label = "아이디", min = 6, max = 255)
    @BSColumn(name = "m_id")
    private String id;

    @ToString.Exclude
    @BSValidation(label = "비밀번호", min = 8, max = 60, required = false)
    @BSColumn(name = "m_password", isShow = false)
    private String password;

    @BSValidation(label = "이름", min = 1, max = 50)
    @BSColumn(name = "m_name")
    private String name;

    @BSValidation(label = "전화번호", min = 1, max = 13)
    @BSColumn(name = "m_cell")
    private String cell;

    @BSValidation(label = "생년월일", min = 10, max = 10)
    @BSColumn(name = "m_birthday")
    private Date birthday;

    @BSColumn(name = "m_status", reqName = "member_status")
    private String status;

    @BSColumn(name = "m_is_sms")
    private Boolean is_sms;

    @BSColumn(name = "m_gender")
    private MemberGender gender;

    @BSColumn(name = "m_lldt")
    private Timestamp latest_logged_at;

    @BSColumn(name = "m_crdt")
    private Timestamp created_at;

    @BSColumn(name = "m_updt")
    private Timestamp updated_at;

    @BSColumn(name = "m_dldt")
    private Timestamp deleted_at;

    @BSColumn(name = """
            (
                SELECT GROUP_CONCAT(ms_type) FROM member_social
                WHERE member_social.m_idx = member.m_idx
            )
            """, isQuerySelect = true)
    private String[] socials;

    @BSColumn(name = "access_marketing", isInput = false)
    private boolean access_marketing;

    @ToString.Exclude
    private String access_token;

    public Member(HttpServletRequest req) {
        super(req);
    }

    public Member(ResultSet rs, int row_num) {
        super(rs, row_num);
    }

    public Member(Integer offset, long count, ResultSet rs, int row_num) {
        super(offset, count, rs, row_num);
    }

    public Member(MemberSocial info) {
        this.id = info.getEmail();
        this.name = info.getName();
        this.cell = info.getCell();
        this.gender = info.getGender();
        this.birthday = info.getBirthday();
    }

    public String statusToString() {
        return switch (status) {
            case "waiting" -> "대기";
            case "active" -> "완료";
            case "canceled" -> "반려";
            case "leaved" -> "탈퇴";
            default -> "";
        };
    }

    public String accessMarketingToString() {
        if(access_marketing) {
            return "동의";
        } else {
            return "비동의";
        }
    }

    private SocialType[] socialsToEnums() {
        if(socials == null) return null;
        else return Arrays.stream(socials).map(SocialType::fromValue).toArray(SocialType[]::new);
    }

    public String socialsToString() {
        if(socials == null) {
            return "직접가입";
        } else {
            final SocialType[] types = Arrays.stream(socials).map(SocialType::fromValue).toArray(SocialType[]::new);
            return String.join(", ", Arrays.stream(types).map(SocialType::toHtml).toArray(String[]::new));
        }
    }

    public String birthdayToString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(birthday);
    }

    public Integer getAge() {
        if(birthday == null) return null;
        final LocalDate now = LocalDate.now();
        final Date currentDate = Date.valueOf(now);

        // 밀리초 차이를 연도로 변환
        long ageInMillis = currentDate.getTime() - birthday.getTime();
        // 1000ms * 60초 * 60분 * 24시간 * 365일
        ageInMillis = ageInMillis / (1000L * 60 * 60 * 24 * 365);

        return (int) ageInMillis;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return Objects.equals(idx, member.idx);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(idx);
    }

    public String nameToEncode() {
        if (name == null || name.length() <= 1) {
            return name;  // 이름이 한 글자 이하라면 그대로 반환
        }

        // 첫 글자 + 나머지 글자 수만큼 '*' 추가
        String maskedPart = "*".repeat(name.length() - 1);
        return name.charAt(0) + maskedPart;
    }
}
