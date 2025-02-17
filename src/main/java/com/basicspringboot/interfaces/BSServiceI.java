package com.basicspringboot.interfaces;

import com.basicspringboot.dto.BSQuery;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

public interface BSServiceI<T> {
    List<T> findAll(BSQuery bsq, RowMapper rm);

    T findOne(BSQuery bsq, RowMapper rm);
}