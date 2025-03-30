package com.basicspringboot.interfaces;

import com.basicspringboot.dto.ResponseDTO;
import com.basicspringboot.models._BSModel;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.RestController;


@RestController
public interface BSAPIControllerI<T extends _BSModel> {

    ResponseEntity<ResponseDTO> findByIdx(Long idx, RowMapper<T> mapper);

    ResponseEntity<ResponseDTO> insert(T data);

    ResponseEntity<ResponseDTO> update(T data);

    ResponseEntity<ResponseDTO> delete(Class<T> clazz, Long idx);
}
