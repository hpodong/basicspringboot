package com.basicspringboot.services.contents;

import com.basicspringboot.dto.BSQuery;
import com.basicspringboot.models.board.Agreement;
import com.basicspringboot.models.board.AgreementCategory;
import com.basicspringboot.services._BSService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
public class AgreementService extends _BSService<Agreement> {

    @Getter
    private final List<AgreementCategory> categories = new CopyOnWriteArrayList<>();

    @Transactional
    public boolean insert(Agreement data) {
        updateStatus(data);
        return super.insert(Agreement.class, setter -> setter.putAll(data.toInput()));
    }

    @Transactional
    public boolean update(Agreement data) {
        updateStatus(data);
        final String where = "WHERE agm_idx = "+data.getIdx();
        return super.update(Agreement.class, where, setter -> setter.putAll(data.toInput()));
    }

    private void updateStatus(Agreement data) {
        if(data.getStatus() != null && data.getStatus().equals("activated")) {
            final String sql = "UPDATE agreement SET agm_status = 'paused' WHERE agmc_idx = ? AND agm_status = 'activated'";
            master.update(sql, data.getCategory_idx());
        }
    }

    public void refreshCategories() {
        final BSQuery bsq = new BSQuery(AgreementCategory.class);
        final List<AgreementCategory> categories = super.master.query(bsq.toSql(), (rs, rn) -> new AgreementCategory(rs, rn));
        this.categories.clear();
        this.categories.addAll(categories);
    }

    public List<Agreement> findAgreements(String type) {
        final BSQuery bsq = new BSQuery(Agreement.class);
        bsq.setSelect("*");

        final String tn = bsq.getTable();

        bsq.addJoin("LEFT JOIN agreement_category ON agreement_category.agmc_idx = "+tn+".agmc_idx AND agmc_dldt IS NULL AND agmc_status = 'activated'");

        bsq.addWhere("agm_dldt IS NULL", "agm_status = 'activated'");

        if(type.equals("signup")) bsq.addWhere("agreement_category.agmc_include_signup = 1");

        bsq.addOrderBy("agm_is_required DESC", "agm_sort", "agm_crdt", "agm_idx");

        bsq.setLimit(null);
        return findAll(bsq, Agreement::new);
    }

    public List<Agreement> getInitData(Timestamp updatedAt) {
        final BSQuery<Agreement> bsq = new BSQuery<>(Agreement.class);
        if(updatedAt != null) {
            bsq.addWhere("agm_updt >= ?");
            bsq.addArgs(updatedAt);
        }
        bsq.setLimit(null);
        return findAll(bsq, Agreement::new);
    }
}
