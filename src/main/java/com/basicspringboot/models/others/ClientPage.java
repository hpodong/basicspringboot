package com.basicspringboot.models.others;

import com.basicspringboot.annotations.BSColumn;
import com.basicspringboot.annotations.BSTable;
import com.basicspringboot.annotations.BSValidation;
import com.basicspringboot.enums.APStatus;
import com.basicspringboot.models._BSModel;
import jakarta.servlet.http.HttpServletRequest;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Objects;

@Getter
@Setter
@ToString
@Slf4j
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@BSTable(name = "client_page", primaryKey = "cp_idx", createdAt = "cp_crdt", updatedAt = "cp_updt", deletedAt = "cp_dldt", status = "cp_status")
public class ClientPage extends _BSModel {

    @BSColumn(name = "cp_idx")
    private Long idx;

    @BSColumn(name = "ht_idx", nullable = true)
    private Long header_fk;

    @BSColumn(name = "fot_idx", nullable = true)
    private Long footer_fk;

    @BSValidation(label = "메뉴 이름", min = 1, max = 100)
    @BSColumn(name = "cp_title")
    private String title;

    @BSValidation(label = "서브 메뉴 이름", max = 100)
    @BSColumn(name = "cp_subtitle")
    private String subtitle;

    @BSValidation(label = "링크", max = 100)
    @BSColumn(name = "cp_link")
    private String link;

    @BSColumn(name = "cp_status", reqName = "menu_status")
    private APStatus status;

    @BSColumn(name = "cp_crdt")
    private Timestamp created_at;

    @BSColumn(name = "cp_updt")
    private Timestamp updated_at;

    @BSColumn(name = "cp_dldt")
    private Timestamp deleted_at;

    private HeaderType header_type;
    private FooterType footer_type;

    public ClientPage(ResultSet rs, int row_num) {
        super(rs, row_num);
        header_type = new HeaderType(rs, row_num);
        footer_type = new FooterType(rs, row_num);
    }

    public ClientPage(HttpServletRequest req) {
        super(req);
    }

    public ClientPage(Integer offset, long count, ResultSet rs, int rowNum) {
        super(offset, count, rs, rowNum);
        header_type = new HeaderType(rs, rowNum);
        footer_type = new FooterType(rs, rowNum);
    }

    public String headerToHtml() {
        if(header_type == null || header_type.getHtml() == null) return null;
        else {
            String value = header_type.getHtml().replace("${pageTitle}", title);
            value = value.replace("${pageSubTitle}", Objects.requireNonNullElse(subtitle, ""));
            return value;
        }
    }
}
