package com.basicspringboot.services.manage;

import com.basicspringboot.dto.BSQuery;
import com.basicspringboot.exceptions.DeleteException;
import com.basicspringboot.models.admin.AdminRole;
import com.basicspringboot.services._BSService;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class AdminRoleService extends _BSService<AdminRole> {

    @Getter
    private final List<AdminRole> roles = new CopyOnWriteArrayList<>();

    public List<AdminRole> getShownRoles() {
        return roles.stream().filter(AdminRole::getCan_show).toList();
    }

    public void insertItems(long idx, String ...items) {
        for(String item : items) {
            super.insert("admin_role_item", setter -> {
                setter.put("ar_idx", idx);
                setter.put("am_idx", item);
            });
        }
    }

    public void deleteItems(Long idx) {
        final String where = "ar_idx = ?";
        try {
            super.delete("admin_role_item", where, idx);
        } catch (DeleteException ignore) {
        }
    }

    public boolean update(AdminRole data) {
        final String where = "WHERE ar_idx = "+data.getIdx();
        return super.update(AdminRole.class, where, setter -> setter.putAll(data.toInput()));
    }

    public void refresh() {
        final BSQuery bsq = new BSQuery(AdminRole.class);
        bsq.setWhere("ar_dldt IS NULL", "ar_status = 'activated'", "ar_can_show = 1");
        bsq.setOrderBy("ar_sort ASC, ar_crdt");
        bsq.setLimit(null);

        roles.clear();
        roles.addAll(super.master.query(bsq.toSql(), (rs, rn) -> new AdminRole(rs, rn)));
    }

    public List<Long> findItemsIdx(Long ar_idx) {
        final String sql  = """
                SELECT am_idx FROM admin_role_item
                WHERE ar_idx = ?
                """;
        return master.query(sql, (rs, rn) -> rs.getLong("am_idx"), ar_idx);
    }
}