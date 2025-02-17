package com.basicspringboot.models.site;

import com.basicspringboot.annotations.BSColumn;
import com.basicspringboot.annotations.BSTable;
import com.basicspringboot.enums.APStatus;
import com.basicspringboot.models._BSModel;
import com.basicspringboot.models.others.FileModel;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Timestamp;

@BSTable(
        name = "popup",
        primaryKey = "pu_idx",
        createdAt = "pu_crdt",
        updatedAt = "pu_updt",
        deletedAt = "pu_dldt",
        status = "pu_status"
)
@Getter
@Setter
@NoArgsConstructor
@Slf4j
@ToString
public class Popup extends _BSModel {

    @BSColumn(name = "pu_idx")
    private Long idx;

    @BSColumn(name = "f_idx")
    private Long file_idx;

    @BSColumn(name = "pu_is_center")
    private Boolean is_center;

    @BSColumn(name = "pu_is_main")
    private Boolean is_main;

    @BSColumn(name = "pu_device", nullable = true)
    private String device;

    @BSColumn(name = "pu_width")
    private int width;

    @BSColumn(name = "pu_height")
    private int height;

    @BSColumn(name = "pu_left")
    private Integer left;

    @BSColumn(name = "pu_top")
    private Integer top;

    @BSColumn(name = "pu_title")
    private String title;

    @BSColumn(name = "pu_url")
    private String url;

    @BSColumn(name = "pu_stdt")
    private Date started_at;

    @BSColumn(name = "pu_endt")
    private Date ended_at;

    @BSColumn(name = "pu_status", reqName = "popup_status")
    private APStatus status;

    @BSColumn(name = "pu_crdt")
    private Timestamp created_at;

    @BSColumn(name = "pu_updt")
    private Timestamp updated_at;

    @BSColumn(name = "pu_dldt")
    private Timestamp deleted_at;

    private FileModel file;

    public Popup(HttpServletRequest req) {
        super(req);
    }

    public Popup(ResultSet rs, int row_num) {
        super(rs, row_num);
        file = new FileModel(rs, row_num);
    }

    public Popup(Integer offset, long count, ResultSet rs, int row_num) {
        super(offset, count, rs, row_num);
        file = new FileModel(rs, row_num);
    }

    public String deviceToString() {
        if(device == null) return "총";
        return switch (device) {
            case "PC" -> "PC에서만";
            case "MO" -> "MO에서만";
            case "APP" -> "APP에서만";
            default -> "총";
        };
    }
}
