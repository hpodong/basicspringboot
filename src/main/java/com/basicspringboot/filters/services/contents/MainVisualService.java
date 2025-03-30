package com.basicspringboot.filters.services.contents;

import com.basicspringboot.filters.dto.BSQuery;
import com.basicspringboot.filters.models.site.MainVisual;
import com.basicspringboot.filters.services._BSService;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Getter
public class MainVisualService extends _BSService<MainVisual> {

    private final List<MainVisual> visuals = new CopyOnWriteArrayList<>();

    public void refresh() {
        visuals.clear();
        final BSQuery bsq = new BSQuery(MainVisual.class);
        final String tn = bsq.getTable();

        bsq.setSelect("*");

        bsq.addJoin("JOIN file ON file.f_idx = "+tn+".f_idx");

        bsq.addWhere("mv_dldt IS NULL", "mv_status = 'activated'");

        bsq.addOrderBy("mv_sort", "mv_crdt", "mv_idx");

        bsq.setLimit(null);

        visuals.addAll(findAll(bsq, MainVisual::new));
    }

    @Transactional
    public boolean insert(MainVisual data) {
        final boolean result = super.insert(MainVisual.class, setter -> {
            setter.putAll(data.toInput());
        });
        if(result) refresh();
        return result;
    }

    @Transactional
    public boolean update(MainVisual data) {
        final String where = "WHERE mv_idx = "+data.getIdx();
        return super.update(MainVisual.class, where, setter -> {
            setter.putAll(data.toInput());
            if(data.getSort() == null) setter.put("mv_sort", null);
        });
    }

    @Transactional
    public int updateSort(List<MainVisual> data) {
        int result = 0;
        for(MainVisual mv : data) if(update(mv)) result++;
        if(result > 0) refresh();
        return result;
    }
}
