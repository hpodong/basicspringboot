package com.travplan.models.admin;

import com.travplan.annotations.BSColumn;
import com.travplan.annotations.BSTable;
import com.travplan.models._BSModel;
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
        name = "admin_push_log",
        primaryKey = "apl_idx",
        createdAt = "apl_crdt",
        updatedAt = "apl_updt",
        deletedAt = "apl_dldt"
)
public class AdminPushLog extends _BSModel {

    @BSColumn(name = "apl_idx")
    private Long idx;

    @BSColumn(name = "apl_url")
    private String url;

    @BSColumn(name = "apl_title")
    private String title;

    @BSColumn(name = "apl_is_read")
    private Boolean is_read;

    @BSColumn(name = "apl_description")
    private String description;

    @BSColumn(name = "apl_type")
    private String type;

    @BSColumn(name = "apl_status_text")
    private String status_text;

    @BSColumn(name = "apl_status_class")
    private String status_class;

    @BSColumn(name = "apl_crdt")
    private Timestamp created_at;

    @BSColumn(name = "apl_updt")
    private Timestamp updated_at;

    @BSColumn(name = "apl_dldt")
    private Timestamp deleted_at;

    public AdminPushLog(ResultSet rs, int rn) {
        super(rs, rn);
    }

    public AdminPushLog(HttpServletRequest request) {
        super(request);
    }

    public AdminPushLog(Integer offset, Long count, ResultSet rs, int row_num) {
        super(offset, count, rs, row_num);
    }
}
