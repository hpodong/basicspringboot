package com.travplan.services.member;

import com.travplan.dto.BSQuery;
import com.travplan.models.member.MemberPushLogCategory;
import com.travplan.services._BSService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@Slf4j
@Getter
public class MemberPushLogCategoryService extends _BSService<MemberPushLogCategory> {

    public List<MemberPushLogCategory> getInitData(Timestamp updatedAt) {
        final BSQuery<MemberPushLogCategory> bsq = new BSQuery<>(MemberPushLogCategory.class);
        if(updatedAt != null) {
            bsq.addWhere("mpslc_updt >= ?");
            bsq.addArgs(updatedAt);
        }
        bsq.setLimit(null);
        return findAll(bsq, MemberPushLogCategory::new);
    }
}
