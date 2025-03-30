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

@BSTable(
        name = "agreement",
        primaryKey = "agm_idx",
        createdAt = "agm_crdt",
        updatedAt = "agm_updt",
        deletedAt = "agm_dldt",
        status = "agm_status"
)
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Agreement extends _BSModel {

    @BSColumn(name = "agm_idx")
    private Long idx;

    @BSColumn(name = "agm_title")
    @BSValidation(label = "제목", min = 1, max = 100)
    private String title;

    @BSValidation(label = "내용", min = 1)
    @BSColumn(name = "agm_desc")
    private String desc;

    @BSColumn(name = "agm_sort")
    private Integer sort;

    @BSColumn(name = "agmc_idx")
    private Integer category_idx;

    @BSColumn(name = "agm_is_required")
    private Boolean is_required;

    @BSColumn(name = "agm_is_footer")
    private Boolean is_footer;

    @BSColumn(name = "agm_status", reqName = "agreement_status")
    private APStatus status;

    @BSColumn(name = "agm_crdt")
    private Timestamp created_at;

    @BSColumn(name = "agm_updt")
    private Timestamp updated_at;

    @BSColumn(name = "agm_dldt")
    private Timestamp deleted_at;

    private AgreementCategory category;

    public Agreement(ResultSet rs, int rn) {
        super(rs, rn);
        category = new AgreementCategory(rs, rn);
    }

    public Agreement(HttpServletRequest request) {
        super(request);
    }

    public Agreement(Integer offset, Long count, ResultSet rs, int row_num) {
        super(offset, count, rs, row_num);
        category = new AgreementCategory(rs, row_num);
    }

    public String isRequiredToString() {
        return is_required ? "필수" : "선택";
    }
}
