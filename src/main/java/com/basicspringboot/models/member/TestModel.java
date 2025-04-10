package com.basicspringboot.models.member;

import com.basicspringboot.annotations.BSColumn;
import com.basicspringboot.annotations.BSTable;
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
        name = "test_model",
        primaryKey = "t_idx",
        createdAt = "t_crdt",
        updatedAt = "t_updt",
        deletedAt = "t_dldt",
        status = "t_status"
)
public class TestModel extends _BSModel {

    @BSColumn(name = "t_idx")
    private Long idx;

    @BSColumn(name = "t_title")
    private String title;

    @BSColumn(name = "t_status", reqName = "t_status")
    private APStatus status;

    @BSColumn(name = "t_crdt")
    private Timestamp created_at;

    @BSColumn(name = "t_updt")
    private Timestamp updated_at;

    @BSColumn(name = "t_dldt")
    private Timestamp deleted_at;

    public TestModel(ResultSet rs, int rn) {
        super(rs, rn);
    }

    public TestModel(HttpServletRequest request) {
        super(request);
    }

    public TestModel(Integer offset, Long count, ResultSet rs, int row_num) {
        super(offset, count, rs, row_num);
    }
}
