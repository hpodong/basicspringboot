package com.travplan.services.manage;

import com.travplan.models.admin.AdminPushLog;
import com.travplan.services._BSService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@Getter
public class AdminPushLogService extends _BSService<AdminPushLog> {

    public boolean insert(AdminPushLog data) {
        return super.insert(AdminPushLog.class, setter -> setter.putAll(data.toInput()));
    }

    public boolean update(AdminPushLog data) {
        return update(data, false);
    }

    public boolean update(AdminPushLog data, boolean nullable) {
        final String where = "WHERE apl_idx = "+data.getIdx();
        return super.update(AdminPushLog.class, where, setter -> setter.putAll(data.toInput(nullable)));
    }

    public Map<String, Long> getPushLogsCount() {
        final String sql = """
        SELECT
            COUNT(CASE WHEN apl_type = 'order' THEN 1 END) AS order_count,
            COUNT(CASE WHEN apl_type = 'return' THEN 1 END) AS return_count,
            COUNT(CASE WHEN apl_type = 'consult' THEN 1 END) AS consult_count
        FROM admin_push_log
        WHERE apl_dldt IS NULL AND DATE(apl_crdt) BETWEEN DATE_SUB(CURDATE(), INTERVAL 30 DAY) AND CURDATE() AND apl_is_read = 0
        """;
        return master.queryForObject(sql, (rs, rowNum) -> {
            Map<String, Long> map = new HashMap<>();
            map.put("order", rs.getLong("order_count"));
            map.put("return", rs.getLong("return_count"));
            map.put("consult", rs.getLong("consult_count"));
            return map;
        });
    }

    public boolean hasPushLogs() {
        final String sql = """
                SELECT EXISTS(
                    SELECT 1 FROM admin_push_log
                    WHERE apl_dldt IS NULL AND DATE(apl_crdt) BETWEEN DATE_SUB(CURDATE(), INTERVAL 30 DAY) AND CURDATE() AND apl_is_read = 0
                )
                """;
        return master.queryForObject(sql, Boolean.class);
    }
}
