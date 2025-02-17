package com.basicspringboot.models;

import com.basicspringboot.annotations.BSColumn;
import com.basicspringboot.annotations.BSValidation;
import com.basicspringboot.annotations.BSTable;
import com.basicspringboot.exceptions.BSValidationException;
import com.basicspringboot.interfaces.BSEnum;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.*;

@Slf4j
@Getter
@RequiredArgsConstructor
public abstract class _BSModel{

    private int row_num;

    protected _BSModel(ResultSet rs, int row_num) {
        this(null, null, rs, row_num);
    }

    protected _BSModel(Integer offset, Long count, ResultSet rs, int row_num) {
        if(count != null) this.row_num = Math.toIntExact(count - row_num);
        if(offset != null) this.row_num -= offset;
        for(final Field field : getFields()) {
            try {
                final BSColumn bsc = getBSColumn(field);
                if(bsc != null) {
                    if(bsc.isQuerySelect()) {
                        setColumn(field.getName(), field, rs);
                    } else {
                        setColumn(bsc.name(), field, rs);
                    }
                }
            } catch (IllegalAccessException | SQLException ignore) {
            }
        }
    }

    protected _BSModel(HttpServletRequest request) {
        for(final Field field : this.getClass().getDeclaredFields()) {
            final Class<?> type = field.getType();

            String key = field.getName();

            if (field.isAnnotationPresent(BSColumn.class)) {
                final BSColumn sc = field.getAnnotation(BSColumn.class);
                if(sc != null) {
                    if(!sc.reqName().isBlank()) key = sc.reqName();

                    final String value = request.getParameter(key);
                    if(field.isAnnotationPresent(BSValidation.class) && value != null) {
                        String msg = getBSLengthException(field, value, sc);
                        if(msg != null) throw new BSValidationException(msg);
                    }

                    if(value != null) {
                        try {
                            field.setAccessible(true);
                            if (type.equals(Long.class) || type.equals(long.class)) field.set(this, Long.valueOf(value.replace(",", "")));
                            else if (type.equals(double.class) || type.equals(Double.class)) field.set(this, Double.parseDouble(value));
                            else if (type.equals(float.class) || type.equals(Float.class)) field.set(this, Float.parseFloat(value));
                            else if (type.equals(BigDecimal.class)) field.set(this, new BigDecimal(value));
                            else if (type.equals(int.class) || type.equals(Integer.class)) field.set(this, Integer.parseInt(value.replace(",", "")));
                            else if (type.equals(boolean.class) || type.equals(Boolean.class)) field.set(this, Boolean.parseBoolean(value) || value.equals("1"));
                            else if (type.equals(Timestamp.class)) field.set(this, Timestamp.valueOf(value));
                            else if (type.equals(Date.class)) field.set(this, setDate(value));
                            else if (BSEnum.class.isAssignableFrom(type)) {
                                final BSEnum<?> enumValue = ((BSEnum<?>) type.getEnumConstants()[0]).enumFromString(value);
                                field.set(this, enumValue);
                            } else if(sc.nullable() && !value.isEmpty()) {
                                field.set(this, value);
                            } else {
                                field.set(this, value);
                            }
                        } catch (Exception e) {
                            if(field.isAnnotationPresent(BSValidation.class)) {
                                final BSValidation bsv = field.getAnnotation(BSValidation.class);
                                final String label = bsv.label();
                                throw new BSValidationException(label+"의 형식을 확인해주세요.");
                            }
                        }
                    }
                }
            }
        }
    }

    private void setColumn(String field_name, Field field, ResultSet rs) throws SQLException, IllegalAccessException {
        if (field_name != null && rs.getObject(field_name) != null) {
            final Class<?> type = field.getType();

            field.setAccessible(true);

            if (type.equals(String.class)) field.set(this, rs.getString(field_name));
            else if (type.equals(String[].class)) field.set(this, rs.getString(field_name).split(","));
            else if (type.equals(Long.class) || type.equals(long.class)) field.set(this, rs.getLong(field_name));
            else if (type.equals(int.class) || type.equals(Integer.class)) field.set(this, rs.getInt(field_name));
            else if (type.equals(boolean.class) || type.equals(Boolean.class)) field.set(this, rs.getBoolean(field_name));
            else if (type.equals(BigDecimal.class)) field.set(this, rs.getBigDecimal(field_name));
            else if (type.equals(double.class) || type.equals(Double.class)) field.set(this, rs.getDouble(field_name));
            else if (type.equals(Timestamp.class)) field.set(this, rs.getTimestamp(field_name));
            else if (type.equals(Date.class)) field.set(this, rs.getDate(field_name));
            else if (BSEnum.class.isAssignableFrom(type)) {
                final String value = rs.getString(field_name);
                final BSEnum<?> enumValue = ((BSEnum<?>) type.getEnumConstants()[0]).enumFromString(value);
                field.set(this, enumValue);
            }
            else field.set(this, rs.getObject(field_name));
        }
    }

    @Nullable
    private static String getBSLengthException(Field field, String value, BSColumn sc) {
        final BSValidation bsl = field.getAnnotation(BSValidation.class);
        final int minLength = bsl.min();
        final int maxLength = bsl.max();
        final String key = bsl.label();
        String msg = null;
        final String suffix = hasSupport(key) ? "은" : "는";
        if(bsl.required() || !value.isBlank()) {
            if(value.length() < minLength) {
                msg = key+suffix+" "+minLength+"자 이상 작성해주세요.";
            } else if(value.length() > maxLength) {
                msg = key+suffix+" "+maxLength+"자 이하로 작성해주세요.";
            }
        }
        return msg;
    }

    private List<Field> getFields() {
        final Class<?> clazz = this.getClass();
        final List<Field> fields = new ArrayList<>();
        if(clazz.isAnnotationPresent(BSTable.class)) {
            for(final Field field : clazz.getDeclaredFields()) {
                if(field.isAnnotationPresent(BSColumn.class)) {
                    fields.add(field);
                }
            }
        }
        return fields;
    }

    private BSColumn getBSColumn(Field field){
        BSColumn bsc = null;
        if(field.isAnnotationPresent(BSColumn.class)) {
            bsc = field.getAnnotation(BSColumn.class);
        }
        return bsc;
    }

    public Map<String, Object> toInput() {
        return toInput(false);
    }

    public Map<String, Object> toInput(boolean nullable) {
        final Map<String, Object> data = new HashMap<>();
        final BSTable bst = getBSTable(this.getClass());
        final String primaryKey = bst.primaryKey();
        final String createdAt = bst.createdAt();
        final String updatedAt = bst.updatedAt();
        final String deletedAt = bst.deletedAt();
        for(Field f : this.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            if(f.isAnnotationPresent(BSColumn.class)) {
                final BSColumn bsc = f.getAnnotation(BSColumn.class);
                final String col = bsc.name();
                if(!primaryKey.equals(bsc.name()) && !createdAt.equals(col) && !updatedAt.equals(col) && !deletedAt.equals(col) && bsc.isInput() && !bsc.isQuerySelect()) {
                    try {
                        final Object value = f.get(this);
                        final Class<?> type = f.getType();
                        if(value != null || (bsc.nullable() && nullable)) {
                            if(String.valueOf(value).isBlank() && bsc.nullable() && nullable) {
                                data.put(bsc.name(), null);
                            } else {
                                if(BSEnum.class.isAssignableFrom(type)) {
                                    BSEnum<?> enumValue = (BSEnum<?>) value;
                                    data.put(bsc.name(), enumValue.getValue());
                                } else {
                                    data.put(bsc.name(), value);
                                }
                            }
                        }
                    } catch (IllegalAccessException ignore) {
                    }
                }
            }
        }
        return data;
    }

    public Map<String, Object> toListData() {
        final Map<String, Object> data = new LinkedHashMap<>();
        data.put("row_num", row_num);
        for(Field f : this.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            if(f.isAnnotationPresent(BSColumn.class)) {
                final BSColumn bsc = f.getAnnotation(BSColumn.class);
                if(bsc.isShow() && bsc.isShowList()) {
                    String key = f.getName();
                    if(!bsc.resName().isBlank()) key = bsc.resName();
                    try {
                        data.put(key, f.get(this));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return data;
    }

    public BSTable getBSTable(Class<?> clazz) {
        BSTable bst = null;
        if(clazz.isAnnotationPresent(BSTable.class)) {
            bst = clazz.getAnnotation(BSTable.class);
        }
        return bst;
    }

    private Date setDate(String value) {
        if(value == null) return null;
        return Date.valueOf(value);
    }

    public String boolToYN(Boolean value) {
        if(value) return "Y";
        else return "N";
    }

    private static boolean hasSupport(String name) {
        if (name == null || name.isEmpty()) {
            return false; // 빈 문자열 처리
        }

        char lastChar = name.charAt(name.length() - 1);  // 마지막 문자 가져오기
        int consonantCode = ((int) lastChar - 44032) % 28;  // 받침 여부 확인

        return consonantCode != 0;  // 받침이 있으면 true, 없으면 false
    }

    public String numberToString(Number price) {
        final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.KOREA);
        return numberFormat.format(price);
    }
}
