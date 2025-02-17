package com.basicspringboot.services.board;

import com.basicspringboot.dto.BSQuery;
import com.basicspringboot.models.board.FAQ;
import com.basicspringboot.models.board.FAQCategory;
import com.basicspringboot.services._BSService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
public class FAQService extends _BSService<FAQ> {

    @Getter
    private final List<FAQCategory> categories = new CopyOnWriteArrayList<>();

    public boolean insert(FAQ faq) {
        try {
            return super.insert(FAQ.class, setter -> setter.putAll(faq.toInput()));

        } catch (DuplicateKeyException e) {
            if(e.getMessage().contains("faq_unique")) {
                updateSort(faq);
                return super.insert(FAQ.class, setter -> setter.putAll(faq.toInput()));
            } else {
                throw new DuplicateKeyException(e.getMessage());
            }
        }
    }

    @Transactional
    public boolean update(FAQ faq) {
        final String where = "WHERE faq_idx = "+faq.getIdx();
        try {
            return super.update(FAQ.class, where, setter -> setter.putAll(faq.toInput()));
        } catch (DuplicateKeyException e) {
            if(e.getMessage().contains("faq_unique")) {
                updateSort(faq);
                return super.update(FAQ.class, where, setter -> setter.putAll(faq.toInput()));
            } else {
                throw new DuplicateKeyException(e.getMessage());
            }
        }
    }

    private void updateSort(FAQ data) {
        jt.update("CALL update_faq_sort(?, ?, ?)", data.getIdx(), data.getCategory_idx(), data.getSort());
    }

    public void refreshCategories() {
        final BSQuery bsq = new BSQuery(FAQCategory.class);
        final List<FAQCategory> categories = super.jt.query(bsq.toSql(), (rs, rn) -> new FAQCategory(rs, rn));
        this.categories.clear();
        this.categories.addAll(categories);
    }
}
