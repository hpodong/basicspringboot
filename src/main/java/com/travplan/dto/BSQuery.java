package com.travplan.dto;

import com.travplan.annotations.BSColumn;
import com.travplan.annotations.BSTable;
import com.travplan.interfaces.DataSetter;
import com.travplan.models._BSModel;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.*;

@Data
@Slf4j
public class BSQuery<T extends _BSModel> {

    private String table;
    private List<String> select = new ArrayList<>();
    private List<String> join = new ArrayList<>();
    private List<String> where = new ArrayList<>();
    private List<String> orderBy = new ArrayList<>();
    private List<String> groupBy = new ArrayList<>();
    private Integer offset;
    private Integer limit = 10;
    private int page = 1;

    private String status;
    private String primaryKey;
    private String createdAtCol;
    private String deletedAtCol;

    private final List<Object> args = new ArrayList<>();
    private final Map<String, String> replaces = new HashMap<>();
    @Setter
    private Map<String, String> types = new LinkedHashMap<>();

    private Class<T> clazz;

    public BSQuery(Class<T> clazz) {
        this(clazz, null);
    }
    public BSQuery(Class<T> clazz, HttpServletRequest request) {
        this.clazz = clazz;
        if(clazz.isAnnotationPresent(BSTable.class)) {
            final BSTable bst = clazz.getAnnotation(BSTable.class);
            table = bst.name();
            primaryKey = bst.primaryKey().isBlank() ? null : bst.primaryKey();
            createdAtCol = bst.createdAt().isBlank() ? null : bst.createdAt();
            deletedAtCol = bst.deletedAt().isBlank() ? null : bst.deletedAt();
            status = bst.status();
            select.add(table+".*");
        }
        if(request != null) setFromParams(request);
    }

    public void setFromParams(HttpServletRequest req, Boolean isDeleted, String dateColumn) {
        final String dateCol = req.getParameter("dt");
        final String type = req.getParameter("t");
        final String keyword = req.getParameter("q");
        final String started_at = req.getParameter("sd");
        final String end_at = req.getParameter("ed");
        final String limit = req.getParameter("s");
        final String page = req.getParameter("p");
        final String status = req.getParameter("st");
        if(dateCol != null) {
            dateColumn = dateCol.trim();
        } else {
            dateColumn = createdAtCol;
        }
        if(limit != null && !limit.isBlank()) this.limit = Integer.parseInt(limit);
        if(page != null && !page.isBlank()) this.page = Integer.parseInt(page);
        if(this.limit != null) offset = this.limit * (this.page-1);
        if(!types.isEmpty() && type != null && keyword != null && !keyword.isBlank()) {
            if(type.isBlank()) {
                final List<String> keys = types.keySet().stream().map(key->setLikeString(types.get(key), keyword)).toList();
                addWhereOr(keys.toArray(new String[0]));
            } else {
                addWhere(setLikeString(types.get(type), keyword));
            }
        }
        if(this.status != null && status != null && !this.status.isBlank() && !status.isBlank()) addWhere(this.status + " = '"+status+"'");
        if(dateColumn != null) {
            if(started_at != null && !started_at.isBlank()) addWhere("DATE("+dateColumn+") >= '" + started_at + "'");
            if(end_at != null && !end_at.isBlank()) addWhere("DATE("+dateColumn+") <= '" + end_at + "'");
        }

        //회원 권한 세팅
        if(isDeleted != null && deletedAtCol != null && !deletedAtCol.isBlank()) {
            if(!isDeleted) {
                addWhere(deletedAtCol+" IS NULL");
            } else {
                addWhere(deletedAtCol+" IS NOT NULL");
            }
        }

        //ORDER BY 세팅
        if(createdAtCol != null && !createdAtCol.isBlank()) setOrderBy(createdAtCol+" DESC");
    }

    public void setFromParams(HttpServletRequest req, Boolean isDeleted) {
        setFromParams(req, isDeleted, null);
    }

    public void setFromParams(HttpServletRequest req) {
        setFromParams(req, false, null);
    }

    public void setFromParams(HttpServletRequest req, String dateColumn) {
        setFromParams(req, false, dateColumn);
    }

    public void setSelect(String sql) {
        select.clear();
        select.add(sql);
    }

    public void setSelect(String ...sql) {
        select.clear();
        select.addAll(List.of(sql));
    }

    public String toSql() {
        return toSql(false);
    }

    public String toSql(boolean emptyLimit) {
        final StringBuilder sb = new StringBuilder();
        sb.append("SELECT ").append(factorySelectSql()).append(" FROM \n").append("`").append(table).append("`");
        if(join != null && !join.isEmpty()) sb.append("\n").append(factoryJoinSql());
        if(where != null && !where.isEmpty()) sb.append("\n").append(factoryWhereSql());
        if(groupBy != null && !groupBy.isEmpty()) sb.append("\n").append(factoryGroupBySql());
        if(orderBy != null && !orderBy.isEmpty()) sb.append("\n").append(factoryOrderBySql());
        if(limit != null && !emptyLimit) {
            sb.append("\n").append("LIMIT ").append(limit);
            if(offset != null) sb.append("\n").append("OFFSET ").append(offset);
        }
        String sql = sb.toString();
        for(String key : replaces.keySet()) {
            sql = sql.replace(key, replaces.get(key));
        }
        return sql;
    }

    public String toCountSql() {
        return toObjectSql("COUNT(*)");
    }

    public String toObjectSql(String select) {
        final StringBuilder sb = new StringBuilder();
        sb.append("SELECT ").append(select).append(" FROM ").append(table);
        if(join != null && !join.isEmpty()) sb.append("\n").append(factoryJoinSql());
        if(where != null && !where.isEmpty()) sb.append("\n").append(factoryWhereSql());
        String sql = sb.toString();
        for(String key : replaces.keySet()) {
            sql = sql.replace(key, replaces.get(key));
        }
        return sql;
    }

    private String factorySelectSql() {
        final StringBuilder sb = new StringBuilder();
        if(!select.isEmpty()) {
            sb.append(String.join(",\n", select));
        } else {
            sb.append("*");
        }

        return sb.toString();
    }
    private String factoryJoinSql() {
        final StringBuilder sb = new StringBuilder();
        if(!join.isEmpty()) {
            sb.append(String.join("\n", join));
        }

        return sb.toString();
    }

    private String factoryWhereSql() {
        final StringBuilder sb = new StringBuilder();
        if(!where.isEmpty()) {
            sb.append("WHERE ").append(String.join("\nAND ", where));
        }

        return sb.toString();
    }

    private String factoryOrderBySql() {
        final StringBuilder sb = new StringBuilder();
        if(!orderBy.isEmpty()) {
            sb.append("ORDER BY ").append(String.join(", ", orderBy));
        }
        return sb.toString();
    }

    private String factoryGroupBySql() {
        final StringBuilder sb = new StringBuilder();
        if(!groupBy.isEmpty()) {
            sb.append("GROUP BY ").append(String.join(", ", groupBy));
        }
        return sb.toString();
    }

    public void addSelect(String... sql) {
        select.addAll(List.of(sql));
    }

    public void addJoin(String... sql) {
        join.addAll(List.of(sql));
    }
    public void addWhere(String... sql) {
        where.addAll(List.of(sql));
    }
    public void addWhereOr(String... sql) {
        where.add("("+String.join(" OR ", sql)+")");
    }
    public void addOrderBy(String... sql) {
        orderBy.addAll(List.of(sql));
    }
    public void addGroupBy(String... sql) {groupBy.addAll(List.of(sql));}
    public void setWhere(String ...sql) {
        where = new ArrayList<>();
        where.addAll(List.of(sql));
    }
    public void setWhere(List<String> sql) {
        where = sql;
    }
    public void addType(String key, String value) {
        types.put(key, value);
    }

    public void setIdx(Long idx) {
        addWhere(table+"."+primaryKey+" = "+idx);
        setLimit(1);
    }

    public void setOrderBy(String ...sql) {
        orderBy.clear();
        addOrderBy(sql);
    }

    private String setLikeString(String column, String value) {
        return "REGEXP_REPLACE("+column+", '<[^>]*>', '')" + " LIKE '%"+value+"%'";
    }

    public void addQuerySelect() {
        addQuerySelect(this.clazz);
    }

    public void addQuerySelect(String ...columns) {
        if(columns != null) {
            for (final String column : columns) {
                addQuerySelect(this.clazz, column);
            }
        }
    }

    public void addArgs(Object ...args) {
        if(args != null) Collections.addAll(this.args, args);
    }

    public Object[] toJDBCTemplate() {
        return args.toArray();
    }

    public void addQuerySelect(Class<? extends _BSModel> clazz) {
        addQuerySelect(clazz, null);
    }

    public void addQuerySelect(Class<? extends _BSModel> clazz, String column) {

        final List<Field> fields = new ArrayList<>();

        if(clazz.isAnnotationPresent(BSTable.class)) {
            for(final Field field : clazz.getDeclaredFields()) {
                if(field.isAnnotationPresent(BSColumn.class)) {
                    fields.add(field);
                }
            }
        }


        for(final Field field : fields) {
            final BSColumn bsc = getBSColumn(field);
            if(bsc.isQuerySelect()) {
                final String field_name = field.getName();
                final String column_name = bsc.name();
                if(column != null && column.equals(field_name)) {
                    addSelect(column_name + " " + field.getName());
                    break;
                } else if(column == null) {
                    addSelect(column_name + " " + field.getName());
                }
            }
        }
    }

    private BSColumn getBSColumn(Field field){
        BSColumn bsc = null;
        if(field.isAnnotationPresent(BSColumn.class)) {
            bsc = field.getAnnotation(BSColumn.class);
        }
        return bsc;
    }

    public void addReplace(String key, String value) {
        replaces.put(key, value);
    }

    public void addQueryColumn(HttpServletRequest request, String key, String column) {
        final String value = request.getParameter(key);
        if(value != null && !value.isBlank()) {
            addWhere(column+" = ?");
            addArgs(value);
        }
    }
}
