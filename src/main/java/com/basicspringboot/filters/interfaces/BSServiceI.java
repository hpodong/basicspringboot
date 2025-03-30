package com.basicspringboot.filters.interfaces;

import com.basicspringboot.filters.dto.BSQuery;
import com.basicspringboot.filters.models._BSModel;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

public interface BSServiceI<T extends _BSModel> {

    T findByIdx(BSQuery<T> bsq, Long idx, RowMapper<T> rm);
    T findOne(BSQuery<T> bsq, RowMapper<T> rm);
    List<T> findAll(BSQuery<T> bsq, RowMapper<T> rm);
    long findAllCount(BSQuery<T> bsq);
    Long insertReturnKey(T data);
    boolean insert(T data);
    boolean update(T data);
    int delete(Class<T> clazz, Long... idx);
}