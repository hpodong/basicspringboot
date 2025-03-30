package com.basicspringboot.models.member;

import com.basicspringboot.annotations.BSColumn;
import com.basicspringboot.annotations.BSTable;
import com.basicspringboot.models._BSModel;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.sql.ResultSet;
import java.sql.Timestamp;

@Getter
@Setter
@ToString
@NoArgsConstructor
@BSTable(
        name = "member_push_log_category",
        primaryKey = "mpslc_idx",
        createdAt = "mpslc_crdt",
        updatedAt = "mpslc_updt",
        deletedAt = "mpslc_dldt"
)
public class MemberPushLogCategory extends _BSModel {

    @BSColumn(name = "mpslc_idx")
    private Long idx;

    @BSColumn(name = "mpslc_name")
    private String name;

    @BSColumn(name = "mpslc_sort")
    private int sort;

    @BSColumn(name = "mpslc_crdt")
    private Timestamp created_at;

    @BSColumn(name = "mpslc_updt")
    private Timestamp updated_at;

    @BSColumn(name = "mpslc_dldt")
    private Timestamp deleted_at;

    public MemberPushLogCategory(ResultSet rs, int rn) {
        super(rs, rn);
    }

    public MemberPushLogCategory(HttpServletRequest request) {
        super(request);
    }

    public MemberPushLogCategory(Integer offset, Long count, ResultSet rs, int row_num) {
        super(offset, count, rs, row_num);
    }
}
