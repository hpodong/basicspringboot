package com.basicspringboot.services.application;

import com.basicspringboot.dto.BSQuery;
import com.basicspringboot.models.consults.Consult;
import com.basicspringboot.models.consults.ConsultCategory;
import com.basicspringboot.models.others.FileModel;
import com.basicspringboot.services._BSService;
import com.basicspringboot.services.manage.AdminPushLogService;
import com.basicspringboot.services.others.FileService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
@Getter
@RequiredArgsConstructor
public class ConsultService extends _BSService<Consult> {

    private final List<ConsultCategory> categories = new CopyOnWriteArrayList<>();

    private final FileService fileService;
    private final AdminPushLogService adminPushLogService;

    public boolean update(Consult data) {
        final String where = "WHERE cs_idx = "+data.getIdx();
        return super.update(Consult.class, where, setter -> setter.putAll(data.toInput(true)));
    }

    private void insertTrigger(Consult data) {
        final String sql = """
                INSERT INTO admin_push_log(apl_url, apl_title, apl_description, apl_type)
                SELECT
                    CONCAT('/admin/consult/view?idx=', ?),
                    CONCAT(csc_name, '(이)가 도착했습니다.'),
                    CONCAT(m_name, ' 고객님의 문의가 도착했습니다.'),
                    'consult'
                    FROM consult
                JOIN member ON member.m_idx = consult.m_idx
                JOIN consult_category ON consult_category.csc_idx = consult.csc_idx
                WHERE cs_idx = ?
                """;
        master.update(sql, data.getIdx(), data.getIdx());
    }

    public void refreshCategories() {
        final BSQuery bsq = new BSQuery(ConsultCategory.class);
        final List<ConsultCategory> categories = super.master.query(bsq.toSql(), (rs, rn) -> new ConsultCategory(rs, rn));
        this.categories.clear();
        this.categories.addAll(categories);
    }

    @Transactional
    public void insertFiles(List<FileModel> files, Long data_idx) {
        insertFiles(files, data_idx, null);
    }

    @Transactional
    public void insertFiles(List<FileModel> files, Long data_idx, Integer initSort) {
        final int length = files.size();
        final List<Object> values = new ArrayList<>();
        final String[] cols = new String[] {
                "cs_idx",
                "f_idx",
                "csf_sort",
        };

        for (int index = 0; index < length; index++) {
            int sort = index+1;
            if(initSort != null) sort += initSort;
            final FileModel file = files.get(index);
            final Long file_idx = fileService.insertReturnKey(file);
            if(file_idx != null) {
                values.add(data_idx);
                values.add(file_idx);
                values.add(sort);
                file.create();
            }
        }

        fileService.insertMany("consult_file", cols, values.toArray());
    }

    public List<FileModel> getFiles(Long idx) {
        final BSQuery bsq = new BSQuery(FileModel.class);
        bsq.addJoin("JOIN consult_file ON consult_file.f_idx = file.f_idx AND cs_idx = ?");
        bsq.addOrderBy("csf_sort", "f_crdt");
        bsq.setLimit(null);
        bsq.addArgs(idx);
        return fileService.findAll(bsq, FileModel::new);
    }

    public List<Consult> getDashboardData() {
        final BSQuery bsq = new BSQuery(Consult.class, request);
        bsq.addSelect("consult_category.csc_name");
        bsq.addJoin("LEFT JOIN consult_category ON consult_category.csc_idx = consult.csc_idx");
        bsq.setLimit(5);
        return findAll(bsq, Consult::new);
    }
}
