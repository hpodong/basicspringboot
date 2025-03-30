package com.basicspringboot.models.admin;

import com.basicspringboot.annotations.BSColumn;
import com.basicspringboot.annotations.BSTable;
import com.basicspringboot.annotations.BSValidation;
import com.basicspringboot.enums.APStatus;
import com.basicspringboot.models._BSModel;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;

import java.sql.ResultSet;
import java.sql.Timestamp;

@Getter
@Setter
@BSTable(name = "admin_role", primaryKey = "ar_idx", createdAt = "ar_crdt", updatedAt = "ar_updt", deletedAt = "ar_dldt", status = "ar_status")
public class AdminRole extends _BSModel {

    @BSColumn(name = "ar_idx")
    private Long idx;
    @BSValidation(label = "권한명", min = 1, max = 20)
    @BSColumn(name = "ar_name")
    private String name;
    @BSColumn(name = "ar_sort")
    private Integer sort;
    @BSColumn(name = "ar_status", reqName = "role_status")
    private APStatus status;
    @BSColumn(name = "ar_can_show")
    private Boolean can_show;
    @BSColumn(name = "ar_crdt")
    private Timestamp created_at;
    @BSColumn(name = "ar_updt")
    private Timestamp updated_at;
    @BSColumn(name = "ar_dldt")
    private Timestamp deleted_at;

    public AdminRole(ResultSet rs, int row_num) {
        super(rs, row_num);
    }

    public AdminRole(HttpServletRequest req) {
        super(req);
    }

    public AdminRole(Integer offset, long count, ResultSet rs, int rowNum) {
        super(offset, count, rs, rowNum);
    }
}
