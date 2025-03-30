package com.basicspringboot.services.visit;

import com.basicspringboot.dto.BSQuery;
import com.basicspringboot.enums.APStatus;
import com.basicspringboot.models.site.SEO;
import com.basicspringboot.models.site.SEOImage;
import com.basicspringboot.services._BSService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@Getter
@RequiredArgsConstructor
public class SEOService extends _BSService<SEO> {

    @Value("${server.url}")
    private String SERVER_URL;

    @Transactional
    public boolean insert(SEO data) {
        return super.insert(SEO.class, setter -> setter.putAll(data.toInput()));
    }

    public SEOImage findImageFromType(Long idx, String type) {
        final String sql = """
                SELECT * FROM search_engine_optimization_image
                JOIN file ON file.f_idx = search_engine_optimization_image.f_idx
                WHERE seo_idx = ? AND seoi_type = ?
                """;
        try {
            return master.queryForObject(sql, SEOImage::new, idx, type);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Transactional
    public Long insertReturnKey(SEO data) {
        return super.insertReturnKey(SEO.class, setter -> setter.putAll(data.toInput()));
    }

    @Transactional
    public boolean update(SEO data) {
        final String where = "WHERE seo_idx = "+data.getIdx();
        return super.update(SEO.class, where, setter -> {
            setter.put("seo_updt", Timestamp.valueOf(LocalDateTime.now()));
            setter.putAll(data.toInput());
        });
    }

    @Transactional
    public int deleteFromIdx(String[] idx) {
        return deleteFromIdx(SEO.class, idx);
    }

    public boolean insertImage(SEOImage data) {
        return super.insert("search_engine_optimization_image", setter -> {
            setter.putAll(data.toInput());
        });
    }

    public boolean updateImage(SEOImage data) {
        final String where = "WHERE seo_idx = "+data.getSeo_idx()+" AND seoi_type = '"+data.getType()+"'";
        return super.update(SEOImage.class, where, setter -> setter.putAll(data.toInput()));
    }

    public SEO getDataFromURI(String uri) {
        final BSQuery bsq = new BSQuery(SEO.class);

        bsq.setSelect("*");
        bsq.addQuerySelect();
        bsq.setWhere("seo_dldt IS NULL", "seo_status = 'activated'", "(seo_url = ? OR ? REGEXP CONCAT('^',seo_url,'/.*'))");
        bsq.setOrderBy("LENGTH(seo_url) DESC", """
                CASE
                    WHEN seo_url = ? THEN 1
                    WHEN seo_url LIKE CONCAT(?, '%') THEN 2
                    ELSE 3 END
                """);
        bsq.setLimit(1);
        bsq.addArgs(uri, uri, uri, uri);
        return findOne(bsq, SEO::new);
    }

    public List<SEO> getAllList() {
        final BSQuery bsq = new BSQuery(SEO.class, request);
        bsq.addWhere("seo_status = ?");
        bsq.addArgs(APStatus.ACTIVATED.getValue());
        bsq.setLimit(null);
        return findAll(bsq, SEO::new);
    }

    private void updateRobots(List<SEO> seos) {
        final String uploadPath = System.getProperty("user.dir") + "/uploads/robots.txt";
        final Path path = Paths.get(uploadPath);
        final File file = path.toFile();
        file.delete();

        final StringBuilder robotsTxt = new StringBuilder();
        robotsTxt.append("User-agent: *\n\n");
        robotsTxt.append("Disallow: /*\n");
        robotsTxt.append("Disallow: /admin/\n");
        robotsTxt.append("Disallow: /uploads/\n\n");

        for(SEO row : seos) {
            final String url = row.getUrl();
            robotsTxt.append("Allow: ").append(url);
            if(url.equals("/")){
                robotsTxt.append("\n");
            } else if(url.endsWith("/")) {
                robotsTxt.append("*\n");
            } else {
                robotsTxt.append("\n");
            }
        }

        robotsTxt.append("\nsitemap: ").append(SERVER_URL).append("/sitemap.xml");

        try {
            Files.write(path, robotsTxt.toString().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
