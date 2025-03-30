package com.basicspringboot.models.board;

import com.basicspringboot.annotations.BSColumn;
import com.basicspringboot.annotations.BSTable;
import com.basicspringboot.annotations.BSValidation;
import com.basicspringboot.enums.APStatus;
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
        name = "notice",
        primaryKey = "n_idx",
        createdAt = "n_crdt",
        updatedAt = "n_updt",
        deletedAt = "n_dldt",
        status = "n_status"
)
public class Notice extends _BSModel {
    @BSColumn(name = "n_idx")
    private Long idx;

    @BSColumn(name = "n_is_top")
    private Boolean is_top;

    @BSValidation(label = "제목", min = 1, max = 100)
    @BSColumn(name = "n_title")
    private String title;

    @BSValidation(label = "내용", min = 1)
    @BSColumn(name = "n_desc")
    private String desc;

    @BSColumn(name = "n_status", reqName = "notice_status")
    private APStatus status;

    @BSColumn(name = "n_hit_count")
    private Integer hit_count;

    @BSColumn(name = "n_url")
    private String url;

    @BSColumn(name = "n_has_file")
    private Boolean has_file;

    @BSColumn(name = "n_crdt")
    private Timestamp created_at;

    @BSColumn(name = "n_updt")
    private Timestamp updated_at;

    @BSColumn(name = "n_dldt")
    private Timestamp deleted_at;

    public Notice(ResultSet rs, int row_num) {
        super(rs, row_num);
    }
    public Notice(Integer offset, Long count, ResultSet rs, int row_num) {
        super(offset, count, rs, row_num);
    }
    public Notice(HttpServletRequest req) {
        super(req);
    }

    public String hasFileToString() {
        return !has_file ? "N" : "Y";
    }

    public String hasUrlToString() {
        return url == null || url.isBlank() ? "N" : "Y";
    }
}
