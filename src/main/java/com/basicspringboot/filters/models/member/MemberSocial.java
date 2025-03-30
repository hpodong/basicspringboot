package com.basicspringboot.filters.models.member;

import com.basicspringboot.filters.annotations.BSColumn;
import com.basicspringboot.filters.annotations.BSTable;
import com.basicspringboot.filters.dto.KakaoUserDTO;
import com.basicspringboot.filters.dto.NaverUserDTO;
import com.basicspringboot.filters.enums.MemberGender;
import com.basicspringboot.filters.enums.SocialType;
import com.basicspringboot.filters.models._BSModel;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Timestamp;

@Getter
@Setter
@ToString
@NoArgsConstructor
@BSTable(
        name = "member_social",
        primaryKey = "ms_idx",
        createdAt = "ms_crdt"
)
public class MemberSocial extends _BSModel {

    @BSColumn(name = "ms_idx")
    private Long idx;

    @BSColumn(name = "m_idx")
    private Long member_idx;

    @BSColumn(name = "ms_type")
    private SocialType type;

    @BSColumn(name = "ms_id")
    private String id;

    @BSColumn(name = "ms_name")
    private String name;

    @BSColumn(name = "ms_email")
    private String email;

    @BSColumn(name = "ms_cell")
    private String cell;

    @BSColumn(name = "ms_gender")
    private MemberGender gender;

    @BSColumn(name = "ms_birthday")
    private Date birthday;

    @BSColumn(name = "ms_crdt")
    private Timestamp created_at;

    public MemberSocial(ResultSet rs, int rn) {
        super(rs, rn);
    }

    public MemberSocial(HttpServletRequest request) {
        super(request);
    }

    public MemberSocial(Integer offset, Long count, ResultSet rs, int row_num) {
        super(offset, count, rs, row_num);
    }

    public MemberSocial(KakaoUserDTO user) {
        this.type = SocialType.KAKAO;
        this.id = user.getId().toString();
        final KakaoUserDTO.KakaoAccount account = user.getKakao_account();
        if(account != null) {
            this.email = account.getEmail();
            this.gender = MemberGender.fronKakao(account.getGender());
        }
    }
    public MemberSocial(NaverUserDTO user) {
        this.type = SocialType.NAVER;
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.cell = user.getMobile();
        this.gender = MemberGender.fromNaver(user.getGender());
        if(user.getBirthyear() != null && user.getBirthday() != null) {
            this.birthday = Date.valueOf(user.getBirthyear()+"-"+user.getBirthday());
        }
    }
}
