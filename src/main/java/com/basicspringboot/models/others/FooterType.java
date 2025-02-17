package com.basicspringboot.models.others;

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
        name = "footer_type",
        primaryKey = "fot_idx",
        createdAt = "fot_crdt",
        updatedAt = "fot_updt",
        deletedAt = "fot_dldt"
)
public class FooterType extends _BSModel {

    @BSColumn(name = "fot_idx")
    private Long idx;

    @BSColumn(name = "fot_name")
    private String name;

    @BSColumn(name = "fot_html")
    private String html;

    @BSColumn(name = "fot_crdt")
    private Timestamp created_at;

    @BSColumn(name = "fot_updt")
    private Timestamp updated_at;

    @BSColumn(name = "fot_dldt")
    private Timestamp deleted_at;

    public FooterType(ResultSet rs, int rn) {
        super(rs, rn);
    }

    public FooterType(HttpServletRequest request) {
        super(request);
    }

    public FooterType(Integer offset, Long count, ResultSet rs, int row_num) {
        super(offset, count, rs, row_num);
    }
}
