package com.basicspringboot.models.admin;

import com.basicspringboot.annotations.BSColumn;
import com.basicspringboot.annotations.BSTable;
import com.basicspringboot.annotations.BSValidation;
import com.basicspringboot.enums.AdminStatus;
import com.basicspringboot.models._BSModel;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.ResultSet;
import java.sql.Timestamp;

@BSTable(
        name = "admin",
        primaryKey = "a_idx",
        createdAt = "a_crdt",
        deletedAt = "a_dldt"
)
@Getter
@Setter
@ToString
public class Admin extends _BSModel {

    @BSColumn(name = "a_idx")
    private Long idx;

    @BSColumn(name = "ar_idx")
    private Long role_idx;

    @BSColumn(name = "a_name")
    @BSValidation(label = "이름", max = 20)
    private String name;

    @BSColumn(name = "a_id")
    @BSValidation(label = "아이디", max = 16, min = 4)
    private String id;

    @BSColumn(name = "a_password", isShow = false)
    @BSValidation(label = "비밀번호", max = 30, min = 8, required = false)
    private String password;

    @BSColumn(name = "a_cell")
    @BSValidation(label = "전화번호", max = 13)
    private String cell;

    @BSColumn(name = "a_email")
    @BSValidation(label = "이메일", max = 255)
    private String email;

    @BSColumn(name = "a_memo")
    private String memo;

    @BSColumn(name = "a_refresh_token")
    private String refresh_token;

    @BSColumn(name = "a_status", reqName = "admin_status")
    private AdminStatus status;

    @BSColumn(name = "a_lldt")
    private Timestamp last_logged_at;

    @BSColumn(name = "a_crdt")
    private Timestamp created_at;

    @BSColumn(name = "a_updt")
    private Timestamp updated_at;

    @BSColumn(name = "a_dldt")
    private Timestamp deleted_at;

    @BSColumn(name = "ar_name", isInput = false)
    private String role;

    public Admin(Integer offset, long count, ResultSet rs, int rowNum) {
        super(offset, count, rs, rowNum);
    }
    public Admin(ResultSet rs, int rowNum) {
        super(rs, rowNum);
    }

    public Admin(HttpServletRequest req) {
        super(req);
    }
}