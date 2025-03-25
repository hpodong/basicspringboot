package com.travplan.models.site;

import com.travplan.annotations.BSColumn;
import com.travplan.annotations.BSTable;
import com.travplan.models._BSModel;
import com.travplan.models.others.FileModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.ResultSet;

@ToString
@Getter
@Setter
@BSTable(
        name = "search_engine_optimization_image",
        primaryKey = "seoi_idx",
        createdAt = "seoi_crdt"
)
public class SEOImage extends _BSModel {

    @BSColumn(name = "seoi_idx")
    private Long idx;

    @BSColumn(name = "seo_idx")
    private Long seo_idx;

    @BSColumn(name = "f_idx")
    private Long file_idx;

    @BSColumn(name = "seoi_type")
    private String type;

    private FileModel file;

    public SEOImage(Long idx, String type, Long file_idx) {
        this.seo_idx = idx;
        this.type = type;
        this.file_idx = file_idx;
    }

    public SEOImage(ResultSet rs, int row_num) {
        super(rs, row_num);
        file = new FileModel(rs, row_num);
    }
}
