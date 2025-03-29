package com.travplan.services.contents;

import com.travplan.dto.BSQuery;
import com.travplan.enums.AppVersionOs;
import com.travplan.models.others.AppVersion;
import com.travplan.services._BSService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
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

    public AppVersion findLatestVersion(AppVersionOs os) {
        final BSQuery<AppVersion> bsq = new BSQuery<>(AppVersion.class);
        bsq.setWhere("av_os = ?");
        bsq.setOrderBy("av_build_number DESC");
        bsq.setLimit(1);
        bsq.addArgs(os.getValue());
        return findOne(bsq, AppVersion::new);
    }

    public AppVersion findRequestVersion(AppVersionOs os, Integer build_number) {
        final BSQuery<AppVersion> bsq = new BSQuery<>(AppVersion.class);
        bsq.setWhere("av_os = ?", "av_build_number = ?");
        bsq.setLimit(1);
        bsq.addArgs(os.getValue(), build_number);
        return findOne(bsq, AppVersion::new);
    }
}
