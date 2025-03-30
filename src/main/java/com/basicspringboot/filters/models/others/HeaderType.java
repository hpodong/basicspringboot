package com.basicspringboot.filters.models.others;

import com.basicspringboot.filters.annotations.BSColumn;
import com.basicspringboot.filters.annotations.BSTable;
import com.basicspringboot.filters.models._BSModel;
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
        name = "header_type",
        primaryKey = "ht_idx",
        createdAt = "ht_crdt",
        updatedAt = "ht_updt",
        deletedAt = "ht_dldt"
)
public class HeaderType extends _BSModel {

    @BSColumn(name = "ht_idx")
    private Long idx;

    @BSColumn(name = "ht_name")
    private String name;

    @BSColumn(name = "ht_html")
    private String html;

    @BSColumn(name = "ht_crdt")
    private Timestamp created_at;

    @BSColumn(name = "ht_updt")
    private Timestamp updated_at;

    @BSColumn(name = "ht_dldt")
    private Timestamp deleted_at;

    public HeaderType(ResultSet rs, int rn) {
        super(rs, rn);
    }

    public HeaderType(HttpServletRequest request) {
        super(request);
    }

    public HeaderType(Integer offset, Long count, ResultSet rs, int row_num) {
        super(offset, count, rs, row_num);
    }

    public boolean isExists() {
        return idx != null;
    }
}
