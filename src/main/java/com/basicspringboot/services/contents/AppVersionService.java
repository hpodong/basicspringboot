package com.basicspringboot.services.contents;

import com.basicspringboot.dto.BSQuery;
import com.basicspringboot.models.others.AppVersion;
import com.basicspringboot.services._BSService;
import org.springframework.stereotype.Service;

@Service
public class AppVersionService extends _BSService<AppVersion> {

    public boolean insert(AppVersion data) {
        return insert(AppVersion.class, setter -> setter.putAll(data.toInput()));
    }
    public boolean update(AppVersion data) {
        final String where = "WHERE av_idx = "+data.getIdx();
        return update(AppVersion.class, where, setter -> {
            setter.putAll(data.toInput());
        });
    }

    public AppVersion findLatestVersion(String os) {
        final BSQuery bsq = new BSQuery(AppVersion.class);
        bsq.setWhere("av_os = ?");
        bsq.setOrderBy("av_build_number DESC");
        bsq.setLimit(1);
        return findOne(bsq, AppVersion::new, os);
    }

    public AppVersion findRequestVersion(String os, Integer build_number) {
        final BSQuery bsq = new BSQuery(AppVersion.class);
        bsq.setWhere("av_os = ?", "av_build_number = ?");
        bsq.setLimit(1);
        return findOne(bsq, AppVersion::new, os, build_number);
    }
}
