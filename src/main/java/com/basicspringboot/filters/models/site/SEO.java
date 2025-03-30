package com.basicspringboot.filters.models.site;

import com.basicspringboot.filters.annotations.BSColumn;
import com.basicspringboot.filters.annotations.BSTable;
import com.basicspringboot.filters.enums.APStatus;
import com.basicspringboot.filters.models._BSModel;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.ResultSet;
import java.sql.Timestamp;

@ToString
@Getter
@Setter
@BSTable(
        name = "search_engine_optimization",
        primaryKey = "seo_idx",
        createdAt = "seo_crdt",
        updatedAt = "seo_updt",
        deletedAt = "seo_dldt",
        status = "seo_status"
)
public class SEO extends _BSModel {

    private static final String[] mobileKeywords = {
            "Mobile", "Android", "iPhone", "iPad", "iPod", "BlackBerry",
            "Windows Phone", "Opera Mini", "IEMobile", "Mobi", "webOS"
    };

    @BSColumn(name = "seo_idx")
    private Long idx;

    @BSColumn(name = "seo_url")
    private String url;

    @BSColumn(name = "seo_title")
    private String title;

    @BSColumn(name = "seo_desc")
    private String desc;

    @BSColumn(name = "seo_status", reqName = "seo_status")
    private APStatus status;

    @BSColumn(name = "seo_keyword")
    private String keyword;

    @BSColumn(name = "seo_crdt")
    private Timestamp created_at;

    @BSColumn(name = "seo_updt")
    private Timestamp updated_at;

    @BSColumn(name = "seo_dldt")
    private Timestamp deleted_at;

    @BSColumn(name = """
            (
                SELECT f_url FROM search_engine_optimization_image
                JOIN file ON file.f_idx = search_engine_optimization_image.f_idx
                WHERE search_engine_optimization_image.seo_idx = search_engine_optimization.seo_idx AND seoi_type = 'PC'
            )
            """, isQuerySelect = true)
    private String pc_image;
    @BSColumn(name = """
            (
                SELECT f_url FROM search_engine_optimization_image
                JOIN file ON file.f_idx = search_engine_optimization_image.f_idx
                WHERE search_engine_optimization_image.seo_idx = search_engine_optimization.seo_idx AND seoi_type = 'MO'
            )
            """, isQuerySelect = true)
    private String m_image;

    private String image_url;

    public SEO(HttpServletRequest request) {
        super(request);
    }

    public SEO(ResultSet rs, int row_num) {
        super(rs, row_num);
    }

    public SEO(Integer offset, long count, ResultSet rs, int row_num) {
        super(offset, count, rs, row_num);
    }

    public String toPCThumbnail(int width, int height) {
        if(pc_image == null) return null;
        final String extension = getExtension(pc_image);
        final StringBuilder sb = new StringBuilder("_").append(width).append("x").append(height).append(extension);
        return pc_image.replace(extension, sb);
    }

    public String toMOThumbnail(int width, int height) {
        if(m_image == null) return null;
        final String extension = getExtension(m_image);
        final StringBuilder sb = new StringBuilder("_").append(width).append("x").append(height).append(extension);
        return m_image.replace(extension, sb);
    }

    private String getExtension(String url) {
        return url.substring(url.lastIndexOf("."));
    }

    public void setImageFromRequest(HttpServletRequest request) {
        final String userAgent = request.getHeader("User-Agent");
        image_url = pc_image;

        if(userAgent != null) for(String keyword: mobileKeywords) if(userAgent.contains(keyword)) {
            image_url = m_image;
            return;
        }
    }
}
