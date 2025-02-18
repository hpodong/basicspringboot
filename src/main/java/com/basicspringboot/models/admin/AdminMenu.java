package com.basicspringboot.models.admin;

import com.basicspringboot.annotations.BSColumn;
import com.basicspringboot.annotations.BSTable;
import com.basicspringboot.annotations.BSValidation;
import com.basicspringboot.enums.APStatus;
import com.basicspringboot.models._BSModel;
import jakarta.servlet.http.HttpServletRequest;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Slf4j
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@BSTable(name = "admin_menu", primaryKey = "am_idx", createdAt = "am_crdt", updatedAt = "am_updt", deletedAt = "am_dldt", status = "am_status")
public class AdminMenu extends _BSModel {

    @BSColumn(name = "am_idx")
    private Long idx;

    @BSColumn(name = "am_fk")
    private Long parent_fk;

    @BSColumn(name = "am_type")
    private String type;

    @BSColumn(name = "am_level")
    private Integer level;

    @BSColumn(name = "am_sort")
    private Integer sort;

    @BSValidation(label = "메뉴 이름", min = 1, max = 100)
    @BSColumn(name = "am_title")
    private String title;

    @BSColumn(name = "am_desc")
    private String desc;

    @BSColumn(name = "am_link")
    private String link;

    @BSColumn(name = "am_icon_name", nullable = true)
    private String icon_name;

    @BSColumn(name = "am_is_basic")
    private Boolean is_basic;

    @BSColumn(name = "am_status", reqName = "menu_status")
    private APStatus status;

    @BSColumn(name = "am_crdt")
    private Timestamp created_at;

    @BSColumn(name = "am_updt")
    private Timestamp updated_at;

    @BSColumn(name = "am_dldt")
    private Timestamp deleted_at;

    @BSColumn(name = "parent_name")
    private String parent_name;

    private List<AdminMenu> children = new ArrayList<>();

    public AdminMenu(ResultSet rs, int row_num) {
        super(rs, row_num);
    }

    public AdminMenu(HttpServletRequest req) {
        super(req);
    }

    public AdminMenu(Integer offset, long count, ResultSet rs, int rowNum) {
        super(offset, count, rs, rowNum);
    }

    public void addChildren(AdminMenu menu) {
        children.add(menu);
    }

    public boolean isParent(AdminMenu parent) {
        return parent_fk != null && parent_fk.equals(parent.getIdx());
    }

    public void addChildren(List<AdminMenu> menus) {
        children.addAll(menus);
    }

    public String childrenToString() {
        return String.join(", ", children.stream().map(AdminMenu::getTitle).toList());
    }

    public String getSidebarHtml() {
        return switch (type) {
            case "arrow" -> arrowHtml();
            case "page" -> pageHtml();
            default -> "";
        };
    }

    public String typeToString() {
        return switch (type) {
            case "arrow" -> "메뉴그룹";
            case "page" -> "페이지";
            default -> "";
        };
    }

    private String arrowHtml() {
        final StringBuilder sb = new StringBuilder("<li>");
        sb.append("<a href='javascript:;' class='on_page'")
                .append(" data-idx='").append(idx).append("'")
                .append(">");
        if(icon_name != null) sb.append("<img src='/admin/images/").append(icon_name).append("'>");
        sb.append(title).append("</a>");
        sb.append(childrenHtml());
        sb.append("</li>");

        return sb.toString();
    }

    private String childrenHtml() {
        final StringBuilder html = new StringBuilder();
        if(!children.isEmpty()) {
            html.append("<ul>");
            for(AdminMenu am : children) html.append(am.getSidebarHtml());
            html.append("</ul>");
        }
        return html.toString();
    }

    private String pageHtml() {

        final StringBuilder sb = new StringBuilder();
        sb.append("<li><a href='")
                .append(link)
                .append("' title='").append(title)
                .append("' data-idx='").append(idx).append("'");
        if(parent_fk != null) sb.append(" data-parent='").append(parent_fk).append("'");
        sb.append(">");
        if(icon_name != null) sb.append("<img src='/admin/images/").append(icon_name).append("'>");
        sb.append(title).append("</a></li>");

        return sb.toString();
    }

    public String adminMenuToCheckbox() {
        return adminMenuToCheckbox(this, null, false);
    }

    public String adminMenuToCheckbox(boolean readonly) {
        return adminMenuToCheckbox(this, null, readonly);
    }

    public String adminMenuToCheckbox(List<Long> idx) {
        return adminMenuToCheckbox(this, idx, false);
    }

    private String adminMenuToCheckbox(AdminMenu am, List<Long> idx, boolean readonly) {
        final StringBuilder sb = new StringBuilder();
        sb.append("<li>");
        sb.append("<input type='checkbox' name='item' id='item").append(am.getIdx()).append("'");
        sb.append(" value='").append(am.getIdx()).append("'");
        if(am.getParent_fk() != null) sb.append(" data-parent='").append(am.getParent_fk()).append("'");
        if(am.getIs_basic() || readonly) sb.append(" onclick='return false;'");
        if(am.getIs_basic() || readonly || (idx != null && idx.contains(am.getIdx()))) sb.append(" checked");
        sb.append(">");
        sb.append("<label for='item").append(am.getIdx()).append("'>").append(am.getTitle()).append("</label>");
        sb.append("<input type='checkbox' name='access_write' id='access_write").append(am.getIdx()).append("'>");
        sb.append("<label for='access_write").append(am.getIdx()).append("'>읽기 권한").append("</label>");
        sb.append("</li>");
        if(!am.getChildren().isEmpty()) {
            sb.append("<ul>");
            for(final AdminMenu child : am.getChildren()) {
                sb.append(adminMenuToCheckbox(child, idx, readonly));
            }
            sb.append("</ul>");
        }
        return sb.toString();
    }
}
