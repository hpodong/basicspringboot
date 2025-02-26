package com.basicspringboot.services.manage;

import com.basicspringboot.dto.BSQuery;
import com.basicspringboot.enums.APStatus;
import com.basicspringboot.models.admin.AdminMenu;
import com.basicspringboot.services._BSService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

@Service
@Slf4j
public class AdminMenuService extends _BSService<AdminMenu> {

    @Getter
    private List<AdminMenu> allMenus = new CopyOnWriteArrayList<>();
    @Getter
    private List<AdminMenu> menus = new CopyOnWriteArrayList<>();

    public Long insert(AdminMenu data) {
        try {
            return super.insertReturnKey(AdminMenu.class, setter -> setter.putAll(data.toInput(true)));
        } catch (DuplicateKeyException e) {
            if(e.getMessage().contains("admin_menu_unique") && setNullSort(data.getSort(), data.getParent_fk())) {
                return super.insertReturnKey(AdminMenu.class, setter -> setter.putAll(data.toInput()));
            } else {
                throw e;
            }
        }
    }

    public boolean update(AdminMenu data, boolean nullable) {
        final String where = "WHERE am_idx = "+data.getIdx();
        try {
            return super.update(AdminMenu.class, where, setter -> setter.putAll(data.toInput(nullable)));
        } catch (DuplicateKeyException e) {
            if(e.getMessage().contains("admin_menu_unique") && setNullSort(data.getSort(), data.getParent_fk())) {
                return super.update(AdminMenu.class, where, setter -> setter.putAll(data.toInput(nullable)));
            } else {
                throw e;
            }
        }
    }

    public boolean setNullSort(int sort, Long parent) {
        String where = "WHERE am_sort = "+sort;
        if(parent == null) {
            where += " AND am_fk IS NULL";
        } else {
            where += " AND am_fk = "+parent;
        }
        return super.update(AdminMenu.class, where, setter -> {
            setter.put("am_sort", null);
        });
    }

    public void refreshMenus() {
        final BSQuery bsq = new BSQuery(AdminMenu.class);
        bsq.setWhere("am_dldt IS NULL", "am_status = 'activated'");
        bsq.setOrderBy("am_fk", "am_sort", "am_title", "am_crdt DESC");
        bsq.setLimit(null);
        allMenus = findAll(bsq, AdminMenu::new);
        factoryMenusWithChildren(allMenus);
        menus = factoryParentMenus(allMenus);
    }

    public List<AdminMenu> getAllPageMenus() {
        return getAllPageMenus(null);
    }

    public List<AdminMenu> getAllPageMenus(Long adminIdx) {
        final BSQuery bsq = new BSQuery(AdminMenu.class);
        bsq.setWhere("am_dldt IS NULL", "am_status = ?", "am_type = ?");
        bsq.addArgs(APStatus.ACTIVATED.getValue(), "page");

        if(adminIdx != null) {
            bsq.addWhere("""
                    EXISTS(
                         SELECT 1 FROM admin_role_item
                         WHERE admin_role_item.am_idx = admin_menu.am_idx
                           AND EXISTS(
                             SELECT 1 FROM admin
                             WHERE admin.ar_idx = admin_role_item.ar_idx AND a_idx = ?
                         )
                     )
                    """);
            bsq.addArgs(adminIdx);
        }

        bsq.setLimit(null);

        return findAll(bsq, AdminMenu::new);
    }

    public AdminMenu getMenuFromUrl(String url) {
        AdminMenu menu = null;
        for(AdminMenu am : allMenus) {
            if(am.getLink() != null && !am.getLink().isBlank()) {
                final Pattern pattern = Pattern.compile(am.getLink()+"(/.*)?");
                if(pattern.matcher(url).matches()) {
                    menu = am;
                    break;
                }
            }
        }

        return menu;
    }

    private List<Long> getAdminRoleItems(Long ar_idx) {
        final String sql = "SELECT am_idx FROM admin_role_item\n" +
                "WHERE ar_idx = "+ar_idx;
        return super.jt.query(sql, (rs, rn) -> rs.getLong("am_idx"));
    }

    public void factoryMenusWithChildren(List<AdminMenu> menus) {
        menus.forEach(parent -> parent.addChildren(menus.stream().filter(child->child.isParent(parent)).toList()));
    }

    public List<AdminMenu> factoryParentMenus(List<AdminMenu> menus) {
        return menus.stream().filter(am->am.getParent_fk() == null).toList();
    }

    public boolean getAdminRoleCheck(Long admin_idx, String url) {
        final String sql = """
                SELECT COUNT(*) count FROM admin_role_item
                JOIN admin_role ON admin_role_item.ar_idx = admin_role.ar_idx AND ar_dldt IS NULL AND ar_status = 'activated'
                JOIN admin_menu ON admin_role_item.am_idx = admin_menu.am_idx AND ? REGEXP am_link AND am_dldt IS NULL AND am_status = 'activated' AND am_type = 'page'
                JOIN admin ON admin_role.ar_idx = admin.ar_idx AND a_idx = ? AND a_dldt IS NULL AND ar_status = 'activated'
                """;
        return Boolean.TRUE.equals(jt.queryForObject(sql, (rs, rn) -> rs.getLong("count") > 0, url+"(/.*)?", admin_idx));
    }

    public List<AdminMenu> findRolesFromIdx(Long ar_idx) {
        final BSQuery bsq = new BSQuery(AdminMenu.class);
        bsq.setSelect("admin_menu.*");
        bsq.addJoin("join admin_role_item on admin_menu.am_idx = admin_role_item.am_idx");
        bsq.addWhere("am_dldt is null", "am_status = 'activated'", "ar_idx = "+ar_idx);
        bsq.setLimit(null);
        bsq.setOrderBy("am_level", "am_sort", "am_crdt DESC");
        return findAll(bsq, AdminMenu::new);
    }

    public List<AdminMenu> findMyAdminMenus(Long a_idx) {
        final BSQuery bsq = new BSQuery(AdminMenu.class);
        bsq.setSelect("admin_menu.*");
        bsq.addJoin("join admin_role_item on admin_menu.am_idx = admin_role_item.am_idx");
        bsq.addJoin("join admin on admin_role_item.ar_idx = admin.ar_idx and a_dldt is null and a_status = 'active' AND admin.a_idx = ?");
        bsq.addWhere("admin_menu.am_dldt IS NULL", "admin_menu.am_status = 'activated'");
        bsq.setLimit(null);
        bsq.setOrderBy("am_level", "am_sort", "am_crdt DESC");
        final List<AdminMenu> menus =  findAll(bsq, AdminMenu::new, a_idx);
        factoryMenusWithChildren(menus);
        return factoryParentMenus(menus);
    }
}