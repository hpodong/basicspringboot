package com.basicspringboot.models;

import com.basicspringboot.annotations.BSColumn;
import com.basicspringboot.annotations.BSTable;
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
        name = "_example",
        primaryKey = "_idx",
        createdAt = "_crdt",
        updatedAt = "_updt",
        deletedAt = "_dldt"
)
public class _Example extends _BSModel {

    @BSColumn(name = "_idx")
    private Long idx;

    @BSColumn(name = "_crdt")
    private Timestamp created_at;

    @BSColumn(name = "_updt")
    private Timestamp updated_at;

    @BSColumn(name = "_dldt")
    private Timestamp deleted_at;

    public _Example(ResultSet rs, int rn) {
        super(rs, rn);
    }

    public _Example(HttpServletRequest request) {
        super(request);
    }

    public _Example(Integer offset, Long count, ResultSet rs, int row_num) {
        super(offset, count, rs, row_num);
    }
}
