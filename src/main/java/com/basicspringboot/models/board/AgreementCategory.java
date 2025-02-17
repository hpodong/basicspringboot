package com.basicspringboot.models.board;

import com.basicspringboot.annotations.BSColumn;
import com.basicspringboot.annotations.BSTable;
import com.basicspringboot.enums.APStatus;
import com.basicspringboot.models._BSModel;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.ResultSet;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@BSTable(
        name = "agreement_category",
        createdAt = "agmc_crdt",
        updatedAt = "agmc_updt",
        deletedAt = "agmc_dldt",
        status = "agmc_status"
)
public class AgreementCategory extends _BSModel {

    @BSColumn(name = "agmc_idx")
    private Long idx;

    @BSColumn(name = "agmc_sort")
    private Integer sort;

    @BSColumn(name = "agmc_name")
    private String name;

    @BSColumn(name = "agmc_status")
    private APStatus status;

    @BSColumn(name = "agmc_include_signup")
    private Boolean include_signup;

    @BSColumn(name = "agmc_include_footer")
    private Boolean include_footer;

    @BSColumn(name = "agmc_crdt")
    private Timestamp created_at;

    @BSColumn(name = "agmc_updt")
    private Timestamp updated_at;

    @BSColumn(name = "agmc_dldt")
    private Timestamp deleted_at;

    public AgreementCategory(ResultSet rs, int row_num) {
        super(rs, row_num);
    }
    public AgreementCategory(Integer offset, Long count, ResultSet rs, int row_num) {
        super(offset, count, rs, row_num);
    }
    public AgreementCategory(HttpServletRequest req) {
        super(req);
    }
}
