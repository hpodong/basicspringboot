package com.basicspringboot.filters.services.contents;

import com.basicspringboot.filters.dto.BSQuery;
import com.basicspringboot.filters.models.site.EventBanner;
import com.basicspringboot.filters.services._BSService;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Getter
public class EventBannerService extends _BSService<EventBanner> {

    private final List<EventBanner> banners = new CopyOnWriteArrayList<>();

    public void refresh() {
        banners.clear();
        final BSQuery bsq = new BSQuery(EventBanner.class);
        final String tn = bsq.getTable();

        bsq.setSelect("*");

        bsq.addJoin("JOIN file ON file.f_idx = "+tn+".f_idx");

        bsq.addWhere("eb_dldt IS NULL", "eb_status = 'activated'");

        bsq.addOrderBy("eb_sort", "eb_crdt", "eb_idx");

        bsq.setLimit(null);

        banners.addAll(findAll(bsq, EventBanner::new));
    }

    @Transactional
    public boolean insert(EventBanner data) {
        final boolean result = super.insert(EventBanner.class, setter -> {
            setter.putAll(data.toInput());
        });
        if(result) refresh();
        return result;
    }

    @Transactional
    public boolean update(EventBanner data) {
        final String where = "WHERE eb_idx = "+data.getIdx();
        return super.update(EventBanner.class, where, setter -> {
            setter.putAll(data.toInput());
            if(data.getSort() == null) setter.put("eb_sort", null);
        });
    }

    @Transactional
    public int updateSort(List<EventBanner> data) {
        int result = 0;
        for(EventBanner mv : data) if(update(mv)) result++;
        if(result > 0) refresh();
        return result;
    }
}
