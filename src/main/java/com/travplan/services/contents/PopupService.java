package com.travplan.services.contents;

import com.travplan.dto.BSQuery;
import com.travplan.models.site.Popup;
import com.travplan.services._BSService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
public class PopupService extends _BSService<Popup> {

    private final List<Popup> popups = new CopyOnWriteArrayList<>();

    public List<Popup> getMains() {
        final Date now = Date.from(Instant.now());
        return popups.stream().filter(popup -> popup.getIs_main() && popup.getStarted_at().before(now) && popup.getEnded_at().after(now)).toList();
    }

    public List<Popup> getExcludeMains() {
        final Date now = Date.from(Instant.now());
        return popups.stream().filter(popup -> !popup.getIs_main() && popup.getStarted_at().before(now) && popup.getEnded_at().after(now)).toList();
    }

    private List<Popup> getAllPopups() {
        final BSQuery bsq = new BSQuery(Popup.class);
        bsq.setSelect("*");
        bsq.addJoin("LEFT JOIN file ON file.f_idx = popup.f_idx");
        bsq.addWhere("pu_dldt IS NULL", "pu_status = 'activated'");
        bsq.addWhere("CURDATE() BETWEEN pu_stdt AND pu_endt");
        bsq.setLimit(null);
        return findAll(bsq, Popup::new);
    }

    public void refresh() {
        popups.clear();
        popups.addAll(getAllPopups());
    }

    public boolean insert(Popup data) {
        final boolean result = super.insert(Popup.class, setter -> {
            setter.putAll(data.toInput());
        });
        if(result) refresh();
        return result;
    }

    public boolean insert(Popup data, boolean nullable) {
        final boolean result = super.insert(Popup.class, setter -> {
            setter.putAll(data.toInput(nullable));
        });
        if(result) refresh();
        return result;
    }

    public boolean update(Popup data, boolean nullable) {
        final String where = "WHERE pu_idx = "+data.getIdx();
        final boolean result = super.update(Popup.class, where, setter -> {
            setter.putAll(data.toInput(nullable));
        });
        if(result) refresh();
        return result;
    }

    public int delete(String[] idx) {
        final int result = deleteFromIdx(Popup.class, idx);
        if(result > 0) refresh();
        return result;
    }
}
