package com.basicspringboot.models.consults;

import com.basicspringboot.annotations.BSColumn;
import com.basicspringboot.annotations.BSTable;
import com.basicspringboot.models._BSModel;
import com.basicspringboot.models.others.FileModel;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.ResultSet;
import java.sql.Timestamp;

@ToString
@Getter
@Setter
@BSTable(
        name = "consult",
        primaryKey = "cs_idx",
        createdAt = "cs_crdt",
        updatedAt = "cs_updt",
        deletedAt = "cs_dldt",
        status = "cs_status"
)
public class Consult extends _BSModel {

    @BSColumn(name = "cs_idx")
    private Long idx;

    @BSColumn(name = "m_idx")
    private Long member_idx;

    @BSColumn(name = "csc_idx")
    private Long category_idx;

    @BSColumn(name = "f_idx")
    private Long answer_file_idx;

    @BSColumn(name = "cs_status", reqName = "consult_status")
    private String status;

    @BSColumn(name = "cs_name")
    private String name;

    @BSColumn(name = "cs_cell")
    private String cell;

    @BSColumn(name = "cs_desc")
    private String desc;

    @BSColumn(name = "cs_answer")
    private String answer;

    @BSColumn(name = "cs_asdt", nullable = true)
    private Timestamp answered_at;

    @BSColumn(name = "cs_crdt")
    private Timestamp created_at;

    @BSColumn(name = "cs_updt")
    private Timestamp updated_at;

    @BSColumn(name = "cs_dldt")
    private Timestamp deleted_at;

    @BSColumn(name = "csc_name")
    private String category_name;

    private FileModel file;
    private FileModel answer_file;

    public Consult(HttpServletRequest request) {
        super(request);
    }

    public Consult(ResultSet rs, int row_num) {
        super(rs, row_num);
        answer_file = new FileModel(rs, row_num);
    }

    public Consult(Integer offset, long count, ResultSet rs, int row_num) {
        super(offset, count, rs, row_num);
    }

    public String statusToString() {
        return switch (status) {
            case "waiting" -> "대기";
            case "completed" -> "완료";
            default -> "";
        };
    }

    public String statusToFrontClass() {
        return switch (status) {
            case "waiting" -> "wait";
            default -> "completed";
        };
    }

    public String statusToClass() {
        return switch (status) {
            case "waiting" -> "txt-red";
            default -> "txt-blue";
        };
    }
}
