package com.travplan.models.site;

import com.travplan.annotations.BSColumn;
import com.travplan.annotations.BSTable;
import com.travplan.annotations.BSValidation;
import com.travplan.enums.APStatus;
import com.travplan.models._BSModel;
import com.travplan.models.others.FileModel;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.Timestamp;

@BSTable(
        name = "event_banner",
        primaryKey = "eb_idx",
        createdAt = "eb_crdt",
        updatedAt = "eb_updt",
        deletedAt = "eb_dldt",
        status = "eb_status"
)
@Getter
@Setter
@NoArgsConstructor
@Slf4j
@ToString
public class EventBanner extends _BSModel {

    @BSColumn(name = "eb_idx")
    private Long idx;

    @BSColumn(name = "f_idx")
    private Long file_idx;

    @BSValidation(label = "제목", min = 1, max = 100)
    @BSColumn(name = "eb_title")
    private String title;

    @BSValidation(label = "자세히 보기 URL", min = 1)
    @BSColumn(name = "eb_url")
    private String url;

    @BSColumn(name = "eb_type")
    private Integer type;

    @BSColumn(name = "eb_sort")
    private Integer sort;

    @BSColumn(name = "eb_status", reqName = "eb_status")
    private APStatus status;

    @BSColumn(name = "eb_crdt")
    private Timestamp created_at;

    @BSColumn(name = "eb_updt")
    private Timestamp updated_at;

    @BSColumn(name = "eb_dldt")
    private Timestamp deleted_at;

    private FileModel file;

    public EventBanner(HttpServletRequest req) {
        super(req);
    }

    public EventBanner(ResultSet rs, int row_num) {
        super(rs, row_num);
        file = new FileModel(rs, row_num);
    }

    public EventBanner(Integer offset, long count, ResultSet rs, int row_num) {
        super(offset, count, rs, row_num);
        file = new FileModel(rs, row_num);
    }

    public String sortToString() {
        return switch (sort) {
            case null -> "미노출";
            default -> sort+"번";
        };
    }
}
