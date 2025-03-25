package com.travplan.services.visit;

import com.travplan.dto.BSQuery;
import com.travplan.models.logs.PageLog;
import com.travplan.services._BSService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
public class PageLogService extends _BSService<PageLog> {

    private final List<PageLog> logs = new CopyOnWriteArrayList<>();

    @Transactional
    public synchronized void insert() {
        if(!logs.isEmpty()) {
            final String[] cols = new String[] {
                    "ifpl_url",
                    "ifpl_ip",
                    "ifpl_os",
                    "ifpl_browser",
                    "ifpl_useragent",
            };

            final List<Object> values = new ArrayList<>();

            for (PageLog log : logs) {
                values.add(log.getUrl());
                values.add(log.getIp());
                values.add(log.getOs());
                values.add(log.getBrowser());
                values.add(log.getUseragent());
            }

            if(insertMany(PageLog.class, cols, values.toArray()) > 0) logs.clear();
        }
    }

    public List<Map<String, Object>> findCharts(List<String> where) {
        final BSQuery bsq = new BSQuery(PageLog.class);
        bsq.setSelect("COUNT(*) count, ifpl_browser name");
        bsq.setWhere(where);
        bsq.addGroupBy("ifpl_browser");
        bsq.addOrderBy("count DESC");
        bsq.setLimit(5);
        return super.master.query(bsq.toSql(), (rs, rn) -> {
            final Map<String, Object> data = new LinkedHashMap<>();
            data.put("count", rs.getLong("count"));
            data.put("name", rs.getString("name"));
            return data;
        });
    }

    public List<Map<String, Object>> findChartFrom1Week(String useragent) {
        final String sql = """
                WITH RECURSIVE date_range AS (
                    SELECT CURDATE() - INTERVAL 6 DAY AS date
                    UNION ALL
                    SELECT date + INTERVAL 1 DAY
                    FROM date_range
                    WHERE date + INTERVAL 1 DAY <= CURDATE()
                )
                SELECT
                    DATE_FORMAT(dr.date, '%m.%d') date,
                    COUNT(pl.ifpl_idx) AS count
                FROM
                    date_range dr
                        LEFT JOIN
                    page_log pl ON DATE(pl.ifpl_crdt) = dr.date AND ifpl_useragent = ?
                GROUP BY
                    dr.date
                ORDER BY
                    dr.date;
                """;
        return super.master.query(sql, (rs, rn) -> {
            final Map<String, Object> data = new LinkedHashMap<>();
            data.put("date", rs.getString("date"));
            data.put("count", rs.getLong("count"));
            return data;
        }, useragent);
    }

    public boolean addLog(PageLog data) {
        synchronized (logs) {
            return logs.add(data);
        }
    }

    public long getBeforeMonthPageLogCount() {
        final String sql = """
                SELECT COUNT(*) FROM inflow_page_log
                WHERE DATE(ifpl_crdt) BETWEEN DATE_SUB(LAST_DAY(CURDATE()), INTERVAL 2 MONTH) + INTERVAL 1 DAY AND DATE_SUB(LAST_DAY(CURDATE()), INTERVAL 1 MONTH);
                """;
        return master.queryForObject(sql, (rs, rn) -> rs.getLong(1));
    }

    public long getCurrentMonthPageLogCount() {
        final String sql = """
                SELECT COUNT(*) FROM inflow_page_log
                WHERE DATE(ifpl_crdt) BETWEEN DATE_SUB(LAST_DAY(CURDATE()), INTERVAL 1 MONTH) + INTERVAL 1 DAY AND LAST_DAY(CURDATE());
                """;
        return master.queryForObject(sql, (rs, rn) -> rs.getLong(1));
    }
}
