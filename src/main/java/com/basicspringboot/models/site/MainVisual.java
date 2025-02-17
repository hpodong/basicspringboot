package com.basicspringboot.models.site;

import com.basicspringboot.annotations.BSColumn;
import com.basicspringboot.annotations.BSTable;
import com.basicspringboot.annotations.BSValidation;
import com.basicspringboot.enums.APStatus;
import com.basicspringboot.models._BSModel;
import com.basicspringboot.models.others.FileModel;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.Timestamp;

@BSTable(
        name = "main_visual",
        primaryKey = "mv_idx",
        createdAt = "mv_crdt",
        updatedAt = "mv_updt",
        deletedAt = "mv_dldt",
        status = "mv_status"
)
@Getter
@Setter
@NoArgsConstructor
@Slf4j
@ToString
public class MainVisual extends _BSModel {

    @BSColumn(name = "mv_idx")
    private Long idx;

    @BSColumn(name = "f_idx")
    private Long file_idx;

    @BSValidation(label = "제목", min = 1, max = 100)
    @BSColumn(name = "mv_title")
    private String title;

    @BSValidation(label = "자세히 보기 URL", min = 1)
    @BSColumn(name = "mv_url")
    private String url;

    @BSColumn(name = "mv_sort")
    private Integer sort;

    @BSColumn(name = "mv_status", reqName = "mv_status")
    private APStatus status;

    @BSColumn(name = "mv_crdt")
    private Timestamp created_at;

    @BSColumn(name = "mv_updt")
    private Timestamp updated_at;

    @BSColumn(name = "mv_dldt")
    private Timestamp deleted_at;

    private FileModel file;

    public MainVisual(HttpServletRequest req) {
        super(req);
    }

    public MainVisual(ResultSet rs, int row_num) {
        super(rs, row_num);
        file = new FileModel(rs, row_num);
    }

    public MainVisual(Integer offset, long count, ResultSet rs, int row_num) {
        super(offset, count, rs, row_num);
        file = new FileModel(rs, row_num);
    }

    public String sortToString() {
        return switch (sort) {
            case null -> "미노출";
            default -> sort+"번";
        };
    }

    public String titleToHtml() {
        return title.replace("\\n", "<br>");
    }
}
