package com.basicspringboot.services.client;

import com.basicspringboot.dto.BSQuery;
import com.basicspringboot.interfaces.InsertSetter;
import com.basicspringboot.models.others.ClientPage;
import com.basicspringboot.models.others.ClientPageInflowLog;
import com.basicspringboot.models.others.FooterType;
import com.basicspringboot.models.others.HeaderType;
import com.basicspringboot.services._BSService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
public class ClientPageService extends _BSService<ClientPage> {

    private final List<ClientPageInflowLog> client_page_inflow_logs = new CopyOnWriteArrayList<>();

    @Autowired
    private HeaderTypeService headerTypeService;
    @Autowired
    private FooterTypeService footerTypeService;

    public Long insert(ClientPage data) {
        return super.insertReturnKey(ClientPage.class, setter -> setter.putAll(data.toInput()));
    }

    public boolean update(ClientPage data) {
        final String where = "WHERE cp_idx = "+data.getIdx();
        return super.update(ClientPage.class, where, setter -> setter.putAll(data.toInput(true)));
    }

    public List<HeaderType> getHeaderTypes() {
        final BSQuery bsq = new BSQuery(HeaderType.class);
        bsq.addWhere("ht_dldt IS NULL");
        bsq.setOrderBy("ht_idx DESC");
        bsq.setLimit(null);
        return headerTypeService.findAll(bsq, HeaderType::new);
    }

    public List<FooterType> getFooterTypes() {
        final BSQuery bsq = new BSQuery(FooterType.class);
        bsq.addWhere("fot_dldt IS NULL");
        bsq.setOrderBy("fot_idx DESC");
        bsq.setLimit(null);
        return footerTypeService.findAll(bsq, FooterType::new);
    }

    public ClientPage getDataFromURI(String uri) {
        final BSQuery bsq = new BSQuery(ClientPage.class);
        final String tn = bsq.getTable();

        bsq.setSelect("*");

        bsq.addJoin("LEFT JOIN header_type ON header_type.ht_idx = "+tn+".ht_idx AND ht_dldt IS NULL");
        bsq.addJoin("LEFT JOIN footer_type ON footer_type.fot_idx = "+tn+".fot_idx AND fot_dldt IS NULL");
        bsq.setWhere("cp_dldt IS NULL", "cp_status = 'activated'", "(cp_link = ? OR ? REGEXP CONCAT('^',cp_link,'/*'))");
        bsq.setOrderBy("LENGTH(cp_link) DESC", """
                CASE
                    WHEN cp_link = ? THEN 1
                    WHEN ? REGEXP CONCAT('^',cp_link,'/*') THEN 2
                    ELSE 3 END
                """);
        bsq.setLimit(1);
        return findOne(bsq, ClientPage::new, uri, uri, uri, uri);
    }

    public List<Map<String, Object>> findCharts(List<String> where) {
        final BSQuery bsq = new BSQuery(ClientPageInflowLog.class);
        bsq.setSelect("COUNT(*) count, cp_title");
        bsq.addJoin("JOIN client_page ON client_page.cp_idx = client_page_inflow_log.cp_idx");
        bsq.setWhere(where);
        bsq.addOrderBy("count DESC");
        bsq.addGroupBy("client_page_inflow_log.cp_idx");
        bsq.setLimit(null);
        return super.jt.query(bsq.toSql(), (rs, rn) -> {
            final Map<String, Object> data = new LinkedHashMap<>();
            data.put("count", rs.getLong("count"));
            data.put("name", rs.getString("cp_title"));
            return data;
        });
    }

    public void addClientPageInflowLogs(Long idx) {
        client_page_inflow_logs.add(new ClientPageInflowLog(idx));
    }

    public void insertClientPageInflowLogs() {
        final List<ClientPageInflowLog> logs = new ArrayList<>();
        logs.addAll(client_page_inflow_logs);
        insertMany(ClientPageInflowLog.class, logs.size(), new InsertSetter() {
            @Override
            public void columnSetter(List<String> columns) {
                columns.add("cp_idx");
                columns.add("cpifl_crdt");
            }

            @Override
            public void valueSetter(int index, List<Object> values) {
                final ClientPageInflowLog data = logs.get(index);
                values.add(data.getClient_page_idx());
                values.add(data.getCreated_at());
                client_page_inflow_logs.removeFirst();
            }
        });
    }
}