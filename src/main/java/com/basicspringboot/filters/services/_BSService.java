package com.basicspringboot.filters.services;

import com.basicspringboot.filters.annotations.BSTable;
import com.basicspringboot.filters.dto.BSQuery;
import com.basicspringboot.filters.exceptions.DeleteException;
import com.basicspringboot.filters.exceptions.NotFoundDeletedAtColumnException;
import com.basicspringboot.filters.exceptions.NotFoundIdxException;
import com.basicspringboot.filters.exceptions.NotFoundWhereException;
import com.basicspringboot.filters.interfaces.*;
import com.basicspringboot.filters.models.AdminListview;
import com.basicspringboot.filters.models._BSModel;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public abstract class _BSService<T extends _BSModel> implements BSServiceI<T> {

    protected static final int BASIC_WIDTH = 256;

    @Autowired
    @Qualifier("masterJdbcTemplate")
    protected JdbcTemplate master;

    @Autowired
    @Qualifier("slaveJdbcTemplate")
    protected JdbcTemplate slave;

    @Autowired
    protected HttpServletRequest request;
    @Autowired
    protected HttpServletResponse response;
    @Autowired
    protected HttpSession session;

    @Override
    public T findByIdx(BSQuery<T> bsq, Long idx, RowMapper<T> rm) {
        bsq.setIdx(idx);
        return slave.queryForObject(bsq.toSql(), rm);
    }

    @Override
    public List<T> findAll(BSQuery<T> bsq, RowMapper<T> rm) {
        return slave.query(bsq.toSql(), bsq.toJDBCTemplate(), rm);
    }

    @Override
    public long findAllCount(BSQuery<T> bsq) {
        return slave.queryForObject(bsq.toCountSql(), bsq.toJDBCTemplate(), (rs, rn) -> rs.getLong(1));
    }

    @Transactional
    public AdminListview<T> findAllListView(BSQuery<T> bsq, Long count, AdminListViewSetter<T> setter) {
        final List<T> list = this.findAll(bsq, (rs, rn) -> setter.setter(bsq.getOffset(), count, rs, rn));
        return new AdminListview<>(list, count, bsq.getLimit(), bsq.getPage());
    }

    @Transactional
    public AdminListview<T> findAllListView(BSQuery<T> bsq, AdminListViewSetter<T> setter) {
        final long count = findAllCount(bsq);
        final List<T> list = this.findAll(bsq, (rs, rn) -> setter.setter(bsq.getOffset(), count, rs, rn));

        return new AdminListview<>(list, count, bsq.getLimit(), bsq.getPage());
    }

    public long findAllVisitedCount(BSQuery<T> bsq) {
        return slave.queryForObject(bsq.toObjectSql("COUNT(*)"), Long.class);
    }

    public long findAllRowCount(BSQuery<T> bsq) {
        final StringBuilder sb = new StringBuilder();
        sb.append("SELECT COUNT(*) FROM (");
        sb.append(bsq.toSql(true));
        sb.append(") as row_count");
        return slave.queryForObject(sb.toString(), bsq.toJDBCTemplate(), Long.class);
    }

    @Override
    public T findOne(BSQuery<T> bsq, RowMapper<T> rm) {
        bsq.setLimit(1);
        try {
            return slave.queryForObject(bsq.toSql(), bsq.toJDBCTemplate(), rm);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public boolean insert(T data) {
        return insert(getTableName(data.getClass()), setter -> setter.putAll(data.toInput()));
    }

    @Override
    public boolean update(T data) {
        final Class<?> clazz = data.getClass();
        final String where = "WHERE "+getIdx(clazz)+"="+data.getIdx();
        return update(clazz, where, setter -> setter.putAll(data.toInput()));
    }

    @Override
    public int delete(Class<T> clazz, Long... idx) {
        return deleteFromIdx(clazz, idx);
    }

    public boolean insert(Class<? extends _BSModel> clazz, DataSetter setter) {
        final String table = getTableName(clazz);
        return insert(table, setter);
    }

    public boolean insert(String table, DataSetter setter) {
        final Map<String, Object> data = new HashMap<>();

        setter.setData(data);

        final StringBuilder sb = new StringBuilder("INSERT INTO ")
                .append(table)
                .append("(")
                .append(String.join(",", data.keySet()))
                .append(")")
                .append("\nVALUES(")
                .append(data.values().stream().map((value) -> "?").collect(Collectors.joining(",")))
                .append(")");

        final int rows = this.master.update(sb.toString(), data.values().toArray());
        return rows > 0;
    }

    protected boolean update(Class<?> clazz, String where, DataSetter setter, Object... args) {
        if(where == null || where.isBlank()) throw new NotFoundWhereException();
        final Map<String, Object> data = new HashMap<>();
        String table = clazz.getSimpleName().toLowerCase();
        if (clazz.isAnnotationPresent(BSTable.class)) table = clazz.getAnnotation(BSTable.class).name();

        setter.setData(data);

        if(data.isEmpty()) return false;

        final StringBuilder sb = new StringBuilder("UPDATE ")
                .append(table)
                .append(" SET ");

        final Set<Map.Entry<String, Object>> entrySet = data.entrySet();
        final Iterator<Map.Entry<String, Object>> iterator = entrySet.iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            String key = entry.getKey();
            sb.append(key).append("= ?");
            if (iterator.hasNext()) {
                sb.append(", ");
            } else {
                sb.append("\n");
            }
        }
        sb.append(where);

        final int rows = this.master.update(sb.toString(), data.values().toArray());
        return rows > 0;
    }

    public Long insertReturnKey(String table, String generatedKeyName, DataSetter setter) {
        final Map<String, Object> data = new HashMap<>();

        setter.setData(data);

        final SimpleJdbcInsert insert = new SimpleJdbcInsert(master)
                .withTableName("`"+table+"`")
                .usingColumns(data.keySet().toArray(new String[0]))
                .usingGeneratedKeyColumns(generatedKeyName);

        final SqlParameterSource parameter = new MapSqlParameterSource()
                .addValues(data);
        return insert.executeAndReturnKey(parameter).longValue();
    }


    public Long insertReturnKey(Class<? extends _BSModel> clazz, DataSetter setter) {
        final String table = getTableName(clazz);
        final String generatedKeyName = getIdx(clazz);
        return insertReturnKey(table, generatedKeyName, setter);
    }

    @Override
    public Long insertReturnKey(T data) {
        return insertReturnKey(data.getClass(), ds -> ds.putAll(data.toInput()));
    }

    private String getTableName(Class<?> clazz) {
        String table = clazz.getSimpleName().toLowerCase();
        if(clazz.isAnnotationPresent(BSTable.class)) table = clazz.getAnnotation(BSTable.class).name();
        return table;
    }

    public int insertMany(String sql, BatchPreparedStatementSetter setter){
        master.batchUpdate(sql, setter);
        return setter.getBatchSize();
    }

    public int insertMany(Class<T> clazz, String[] cols, Object ...objs) {
        final String table = getTableName(clazz);
        return insertMany(table, cols, objs);
    }

    public int insertMany(Class<? extends _BSModel> clazz, int size, InsertSetter setter) {
        if(size == 0) return 0;
        final String table = getTableName(clazz);
        return insertMany(table, size, setter);
    }

    public int insertMany(String table, int size, InsertSetter setter) {
        if(size == 0) return 0;
        final List<String> columns = new ArrayList<>();
        final List<Object> values = new ArrayList<>();
        setter.columnSetter(columns);

        for(int index = 0; index < size; index++) {
            setter.valueSetter(index, values);
        }

        return insertMany(table, columns.toArray(new String[0]), values.toArray());
    }

    public int insertMany(String table, String[] cols, Object ...objs) {
        final int length = objs.length/cols.length;
        final String values = String.join(",", Arrays.stream(cols).map(col -> "?").toArray(String[]::new));

        final StringBuilder sb = new StringBuilder("INSERT INTO ").append(table).append("(").append(String.join(",", cols)).append(") VALUES");
        for(int index = 0; index < length; index++) {
            if(index > 0) sb.append(",");
            sb.append("(").append(values).append(")");
        }
        sb.append(";");
        final String sql = sb.toString();

        return master.update(sql, objs);
    }

    protected List<Long> insertManyReturnKeys(Class<? extends _BSModel> clazz, int length, InsertSetter setter) {
        if(length == 0) return null;
        final int result = insertMany(clazz, length, setter);

        if(result > 0) {
            final BSQuery bsq = new BSQuery(clazz);
            final String primaryKey = getIdx(clazz);
            bsq.setSelect(primaryKey);
            bsq.setOrderBy(primaryKey + " DESC");
            bsq.setLimit(result);

            return master.query(bsq.toSql(), (rs, rn) -> rs.getLong(primaryKey))
                    .stream()
                    .sorted(Comparator.comparingLong(Long::longValue)) // 작은 값부터 정렬
                    .toList();
        } else {
            return null;
        }
    }

    private List<Map<String, Object>> getPickedData(List<T> list) {
        return list.stream().map(_BSModel::toListData).toList();
    }

    public int deleteFromIdx(Class<? extends _BSModel> clazz, boolean isForce, Object ...idx) {
        final StringBuilder sb = new StringBuilder();
        final String table = getTableName(clazz);
        final String idxCol = getIdx(clazz);
        final String deletedAt = getTableDeletedAtColumn(clazz);

        if(idx.length == 0) throw new NotFoundIdxException();

        if(isForce) {
            sb.append("DELETE FROM ").append(table).append("\n")
                    .append("WHERE ").append(idxCol).append(" IN (")
                    .append(String.join(", ", Arrays.stream(idx).map(id -> "?").toArray(String[]::new)))
                    .append(")");
        } else {
            if(deletedAt.isBlank()) throw new NotFoundDeletedAtColumnException();
            sb.append("UPDATE ").append(getTable(clazz).name()).append(" SET\n")
                    .append(deletedAt).append(" = NOW()\n")
                    .append("WHERE ").append(idxCol).append(" IN (")
                    .append(String.join(", ", Arrays.stream(idx).map(id -> "?").toArray(String[]::new)))
                    .append(")");
        }


        final int result = master.update(sb.toString(), idx);
        if(result == 0) throw new DeleteException();
        return result;
    }
    public int deleteFromIdx(Class<? extends _BSModel> clazz, Long ...idx) {
        return deleteFromIdx(clazz, false, idx);
    }
    public int deleteFromIdx(Class<? extends _BSModel> clazz, String ...idx) {
        return deleteFromIdx(clazz, false, Arrays.stream(idx).map(Long::valueOf).toArray(Long[]::new));
    }
    protected int delete(String table, String where, Object... objs) {
        if(where == null || where.isBlank()) throw new NotFoundWhereException();
        final StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM ").append(table).append(" WHERE ").append(where);

        final int result = master.update(sb.toString(), objs);
        if(result == 0) throw new DeleteException();
        return result;
    }

    protected String getTableDeletedAtColumn(Class<? extends _BSModel> clazz) {
        String column = null;
        if(clazz.isAnnotationPresent(BSTable.class)) {
            final BSTable bst = clazz.getAnnotation(BSTable.class);
            column = bst.deletedAt();
        }
        return column;
    }
    protected String getIdx(Class<?> clazz) {
        String column = null;
        if(clazz.isAnnotationPresent(BSTable.class)) {
            final BSTable bst = clazz.getAnnotation(BSTable.class);
            column = bst.primaryKey();
        }
        return column;
    }
    protected BSTable getTable(Class<?> clazz) {
        BSTable table = null;
        if(clazz.isAnnotationPresent(BSTable.class)) table = clazz.getAnnotation(BSTable.class);
        return table;
    }
    protected String replaceFileThumbnailUrl(int width, int height) {
        return replaceFileThumbnailUrl(null, width, height);
    }

    protected String replaceFileThumbnailUrl(String alias, int width, int height) {
        if(alias != null) {
            return "REPLACE("+alias+".f_url, "+alias+".f_enc_name, CONCAT("+alias+".f_enc_name, '_"+width+"x"+height+"')) f_url";
        } else {
            return "REPLACE(f_url, f_enc_name, CONCAT(f_enc_name, '_"+width+"x"+height+"')) f_url";
        }
    }

    protected void downloadExcel(List<T> list, SheetSetter setter) throws IOException {
        final Workbook workbook = new XSSFWorkbook();
        final Sheet sheet = workbook.createSheet();

        final List<String> headers = new ArrayList<>();
        setter.setHeaders(headers);

        final int header_size = headers.size();
        final int list_size = list.size();

        final Row headerRow = sheet.createRow(0);
        for(int index = 0; index < header_size; index++) {
            final Cell cell = headerRow.createCell(index);
            final CellStyle style = workbook.createCellStyle();
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            style.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            style.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            cell.setCellValue(headers.get(index));
            cell.setCellStyle(style);
        }
        for(int index = 0; index < list_size; index++) {
            final Row row = sheet.createRow(index + 1);
            setter.setRows(index, row, workbook);
        }

        setter.setSheet(sheet);
        setter.onSuccess(workbook, URLEncoder.encode(setter.setFileName(), StandardCharsets.UTF_8).replace("+", "%20"));
    }

    protected CellStyle getDefaultStyle(Workbook workbook) {
        final CellStyle style = workbook.createCellStyle();

        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        return style;
    }

    protected CellStyle getCenterStyle(Workbook workbook) {
        final CellStyle style = getDefaultStyle(workbook);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    protected CellStyle getLeftStyle(Workbook workbook) {
        final CellStyle style = getDefaultStyle(workbook);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.TOP);
        return style;
    }

    protected CellStyle getRightStyle(Workbook workbook) {
        final CellStyle style = getDefaultStyle(workbook);
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.TOP);
        return style;
    }
}

