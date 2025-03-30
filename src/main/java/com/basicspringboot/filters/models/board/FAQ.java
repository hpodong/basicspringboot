package com.basicspringboot.filters.models.board;

import com.basicspringboot.filters.annotations.BSColumn;
import com.basicspringboot.filters.annotations.BSTable;
import com.basicspringboot.filters.annotations.BSValidation;
import com.basicspringboot.filters.enums.APStatus;
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
        name = "faq",
        primaryKey = "faq_idx",
        createdAt = "faq_crdt",
        updatedAt = "faq_updt",
        deletedAt = "faq_dldt",
        status = "faq_status"
)
public class FAQ extends _BSModel {
    @BSColumn(name = "faq_idx")
    private Long idx;

    @BSColumn(name = "faqc_idx", reqName = "category")
    private Long category_idx;

    @BSColumn(name = "faq_sort")
    private Integer sort;

    @BSValidation(label = "질문", min = 1, max = 100)
    @BSColumn(name = "faq_question")
    private String question;

    @BSValidation(label = "답변", min = 1)
    @BSColumn(name = "faq_asked")
    private String asked;

    @BSColumn(name = "faq_status", reqName = "faq_status")
    private APStatus status;

    @BSColumn(name = "faqc_name", isInput = false)
    private String category;

    @BSColumn(name = "faq_crdt")
    private Timestamp created_at;

    @BSColumn(name = "faq_updt")
    private Timestamp updated_at;

    @BSColumn(name = "faq_dldt")
    private Timestamp deleted_at;

    public FAQ(ResultSet rs, int row_num) {
        super(rs, row_num);
    }
    public FAQ(Integer offset, Long count, ResultSet rs, int row_num) {
        super(offset, count, rs, row_num);
    }
    public FAQ(HttpServletRequest req) {
        super(req);
    }
}
