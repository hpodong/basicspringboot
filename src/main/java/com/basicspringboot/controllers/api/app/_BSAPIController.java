package com.basicspringboot.controllers.api.app;

import com.basicspringboot.dto.BSQuery;
import com.basicspringboot.dto.ResponseDTO;
import com.basicspringboot.exceptions.DeleteException;
import com.basicspringboot.exceptions.InsertException;
import com.basicspringboot.exceptions.NoDataException;
import com.basicspringboot.exceptions.UpdateException;
import com.basicspringboot.interfaces.BSAPIControllerI;
import com.basicspringboot.interfaces.ResponseSetter;
import com.basicspringboot.models._BSModel;
import com.basicspringboot.services._BSService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.RowMapper;

@Slf4j
public abstract class _BSAPIController<T extends _BSModel, S extends _BSService<T>> implements BSAPIControllerI<T> {
    @Autowired
    protected HttpServletRequest request;
    protected final Class<T> baseClass;
    protected S service;

    protected _BSAPIController(Class<T> baseClass, S service) {
        this.baseClass = baseClass;
        this.service = service;
    }

    protected ResponseEntity<ResponseDTO> returnResponse(ResponseSetter setter) {
        final ResponseDTO res = new ResponseDTO();
        setter.setResponse(res);
        return ResponseEntity.status(res.getStatusCode()).body(res);
    }

    @Override
    public ResponseEntity<ResponseDTO> findByIdx(Long idx, RowMapper<T> mapper) {
        final BSQuery<T> bsq = new BSQuery<>(baseClass);
        bsq.setIdx(idx);
        final T data = service.findByIdx(bsq, idx, mapper);
        return returnResponse(setter -> {
            if(data == null) throw new NoDataException();

            setter.setStatusCode(HttpStatus.OK);
            setter.setData(data);
        });
    }

    @Override
    public ResponseEntity<ResponseDTO> insert(T data) {
        final boolean result = service.insert(data);
        if(!result) throw new InsertException();
        return returnResponse(setter -> setter.setStatusCode(HttpStatus.CREATED));
    }

    @Override
    public ResponseEntity<ResponseDTO> update(T data) {
        final boolean result = service.update(data);
        if(!result) throw new UpdateException();
        return returnResponse(setter -> setter.setStatusCode(HttpStatus.OK));
    }

    @Override
    public ResponseEntity<ResponseDTO> delete(Class<T> clazz, Long idx) {
        final boolean result = service.delete(clazz, idx) > 0;
        if(!result) throw new DeleteException();
        return returnResponse(setter -> setter.setStatusCode(HttpStatus.OK));
    }
}
