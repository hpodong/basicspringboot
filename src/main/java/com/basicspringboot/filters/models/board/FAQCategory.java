package com.basicspringboot.filters.models.board;

import com.basicspringboot.filters.annotations.BSColumn;
import com.basicspringboot.filters.annotations.BSTable;
import com.basicspringboot.filters.models._BSModel;
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
        name = "faq_category",
        createdAt = "faqc_crdt",
        updatedAt = "faqc_updt",
        deletedAt = "faqc_dldt",
        status = "faqc_status"
)
public class FAQCategory extends _BSModel {

    @BSColumn(name = "faqc_idx")
    private Long idx;

    @BSColumn(name = "faqc_sort")
    private Integer sort;

    @BSColumn(name = "faqc_name")
    private String name;

    @BSColumn(name = "faqc_status")
    private String status;

    @BSColumn(name = "faqc_crdt")
    private Timestamp created_at;

    @BSColumn(name = "faqc_updt")
    private Timestamp updated_at;

    @BSColumn(name = "faqc_dldt")
    private Timestamp deleted_at;

    public FAQCategory(ResultSet rs, int row_num) {
        super(rs, row_num);
    }
    public FAQCategory(Integer offset, Long count, ResultSet rs, int row_num) {
        super(offset, count, rs, row_num);
    }
    public FAQCategory(HttpServletRequest req) {
        super(req);
    }

    public String statusToString() {
        return switch (status) {
            case "active" -> "활성화";
            case "pause" -> "비활성화";
            default -> "";
        };
    }
}
