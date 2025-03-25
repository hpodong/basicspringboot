package com.travplan.models.consults;

import com.travplan.annotations.BSColumn;
import com.travplan.annotations.BSTable;
import com.travplan.models._BSModel;
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
        name = "consult_category",
        primaryKey = "csc_idx",
        createdAt = "csc_crdt",
        updatedAt = "csc_updt",
        deletedAt = "csc_dldt"
)
public class ConsultCategory extends _BSModel {

    @BSColumn(name = "csc_idx")
    private Long idx;

    @BSColumn(name = "csc_sort")
    private Integer sort;

    @BSColumn(name = "csc_name")
    private String name;

    @BSColumn(name = "csc_status")
    private String status;

    @BSColumn(name = "csc_crdt")
    private Timestamp created_at;

    @BSColumn(name = "csc_updt")
    private Timestamp updated_at;

    @BSColumn(name = "csc_dldt")
    private Timestamp deleted_at;

    public ConsultCategory(HttpServletRequest request) {
        super(request);
    }

    public ConsultCategory(ResultSet rs, int row_num) {
        super(rs, row_num);
    }

    public ConsultCategory(Integer offset, long count, ResultSet rs, int row_num) {
        super(rs, row_num);
    }
}
