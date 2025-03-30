package com.basicspringboot.filters.controllers.api.app;

import com.basicspringboot.filters.dto.ResponseDTO;
import com.basicspringboot.filters.exceptions.*;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

@Slf4j
@RestControllerAdvice(basePackages = "com.travplan.controllers.api")
public class AppErrorController {

    final ResponseDTO res = new ResponseDTO();

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ResponseDTO> handler(NullPointerException e) {
        log.error("[NullPointerException] cause: {}, message: {}", e.getCause(), e.getLocalizedMessage());

        res.setError(e.getLocalizedMessage());
        res.setStatusCode(HttpStatus.BAD_REQUEST);
        res.setMessage("필수값을 입력해주세요.");
        return ResponseEntity.status(res.getStatusCode()).body(res);
    }

    @ExceptionHandler(NoMatchApiKeyException.class)
    public ResponseEntity<ResponseDTO> handler(NoMatchApiKeyException e) {
        log.error("[NoMatchApiKeyException] cause: {}, message: {}", e.getCause(), e.getLocalizedMessage());

        res.setMessage(e.getMessage());
        res.setStatusCode(HttpStatus.BAD_REQUEST);
        res.setError(e.getLocalizedMessage());
        return ResponseEntity.status(res.getStatusCode()).body(res);
    }

    @ExceptionHandler(NotFoundApiKeyException.class)
    public ResponseEntity<ResponseDTO> handler(NotFoundApiKeyException e) {
        log.error("[NotFoundApiKeyException] cause: {}, message: {}", e.getCause(), e.getLocalizedMessage());

        res.setMessage(e.getMessage());
        res.setStatusCode(HttpStatus.BAD_REQUEST);
        res.setError(e.getLocalizedMessage());
        return ResponseEntity.status(res.getStatusCode()).body(res);
    }

    @ExceptionHandler(NotFoundTokenException.class)
    public ResponseEntity<ResponseDTO> handler(NotFoundTokenException e) {
        log.error("[NotFoundTokenException] cause: {}, message: {}", e.getCause(), e.getLocalizedMessage());

        res.setMessage(e.getMessage());
        res.setStatusCode(HttpStatus.BAD_REQUEST);
        res.setError(e.getLocalizedMessage());
        return ResponseEntity.status(res.getStatusCode()).body(res);
    }

    @ExceptionHandler(TokenNotEqualTypeException.class)
    public ResponseEntity<ResponseDTO> handler(TokenNotEqualTypeException e) {
        log.error("[TokenNotEqualTypeException] cause: {}, message: {}", e.getCause(), e.getLocalizedMessage());

        res.setMessage(e.getMessage());
        res.setStatusCode(HttpStatus.BAD_REQUEST);
        res.setError(e.getLocalizedMessage());
        return ResponseEntity.status(res.getStatusCode()).body(res);
    }

    @ExceptionHandler(IllegalAccessException.class)
    public ResponseEntity<ResponseDTO> handler(IllegalAccessException e) {
        log.error("[IllegalAccessException] cause: {}, message: {}", e.getCause(), e.getLocalizedMessage());

        res.setMessage(e.getMessage());
        res.setStatusCode(HttpStatus.BAD_REQUEST);
        res.setError(e.getLocalizedMessage());
        return ResponseEntity.status(res.getStatusCode()).body(res);
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<ResponseDTO> handler(EmptyResultDataAccessException e) {
        log.error("[EmptyResultDataAccessException] cause: {}, message: {}", e.getCause(), e.getLocalizedMessage());

        res.setStatusCode(HttpStatus.NO_CONTENT);
        return ResponseEntity.status(res.getStatusCode()).body(res);
    }

    @ExceptionHandler(NoDataException.class)
    public ResponseEntity<ResponseDTO> handler(NoDataException e) {
        log.error("[NoDataException] cause: {}, message: {}", e.getCause(), e.getLocalizedMessage());

        res.setStatusCode(HttpStatus.NO_CONTENT);
        return ResponseEntity.status(res.getStatusCode()).body(res);
    }

    @ExceptionHandler(UpdateException.class)
    public ResponseEntity<ResponseDTO> handler (UpdateException e){
        log.error("[UpdateException] cause: {}, message: {}", e.getCause(), e.getLocalizedMessage());

        res.setStatusCode(HttpStatus.BAD_REQUEST);
        res.setMessage(e.getMessage());
        res.setError(e.getLocalizedMessage());
        return ResponseEntity.status(res.getStatusCode()).body(res);
    }

    @ExceptionHandler(InsertException.class)
    public ResponseEntity<ResponseDTO> handler (InsertException e){
        log.error("[InsertException] cause: {}, message: {}", e.getCause(), e.getLocalizedMessage());

        res.setStatusCode(HttpStatus.BAD_REQUEST);
        res.setMessage(e.getMessage());
        res.setError(e.getLocalizedMessage());
        return ResponseEntity.status(res.getStatusCode()).body(res);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ResponseDTO> handler (ExpiredJwtException e){
        log.error("[ExpiredJwtException] cause: {}, message: {}", e.getCause(), e.getLocalizedMessage());

        res.setStatusCode(HttpStatus.FORBIDDEN);
        res.setMessage(e.getMessage());
        res.setError(e.getLocalizedMessage());
        return ResponseEntity.status(res.getStatusCode()).body(res);
    }

    @ExceptionHandler(DeleteException.class)
    public ResponseEntity<ResponseDTO> handler(DeleteException e) {
        log.error("[DeleteException] cause: {}, message: {}", e.getCause(), e.getLocalizedMessage());

        res.setStatusCode(HttpStatus.BAD_REQUEST);
        res.setMessage(e.getMessage());
        res.setError(e.getLocalizedMessage());
        return ResponseEntity.status(res.getStatusCode()).body(res);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ResponseDTO> handler(MissingServletRequestParameterException e) {
        log.error("[MissingServletRequestParameterException] cause: {}, message: {}", e.getCause(), e.getLocalizedMessage());

        res.setStatusCode(HttpStatus.BAD_REQUEST);
        res.setMessage("필수 파라미터가 비어있습니다.");
        res.setError(e.getMessage());
        return ResponseEntity.status(res.getStatusCode()).body(res);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseDTO> handler(HttpMessageNotReadableException e) {
        log.error("[HttpMessageNotReadableException] cause: {}, message: {}", e.getCause(), e.getLocalizedMessage());

        res.setStatusCode(HttpStatus.BAD_REQUEST);
        res.setMessage("요청값이 비어있습니다.");
        res.setError(e.getMessage());
        return ResponseEntity.status(res.getStatusCode()).body(res);
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ResponseDTO> handler(SQLException e) {
        log.error("[SQLException] cause: {}, message: {}", e.getCause(), e.getLocalizedMessage());

        res.setStatusCode(HttpStatus.BAD_REQUEST);
        res.setMessage("필수값이 비어있습니다.");
        res.setError(e.getMessage());
        return ResponseEntity.status(res.getStatusCode()).body(res);
    }

    @ExceptionHandler(BadSqlGrammarException.class)
    public ResponseEntity<ResponseDTO> handler(BadSqlGrammarException e) {
        log.error("[BadSqlGrammarException] cause: {}, message: {}, sql: {}", e.getCause(), e.getLocalizedMessage(), e.getSql());

        res.setStatusCode(HttpStatus.BAD_REQUEST);
        res.setMessage("SQL 오류입니다.");
        res.setError(e.getMessage());
        return ResponseEntity.status(res.getStatusCode()).body(res);
    }


    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ResponseDTO> handler(DuplicateKeyException e) {
        log.error("[DuplicateKeyException] cause: {}, message: {}", e.getCause(), e.getLocalizedMessage());

        res.setStatusCode(HttpStatus.BAD_REQUEST);
        res.setMessage("중복된 데이터가 있습니다.");
        res.setError(e.getMessage());
        return ResponseEntity.status(res.getStatusCode()).body(res);
    }

    @ExceptionHandler(UnsupportedEncodingException.class)
    public ResponseEntity<ResponseDTO> handler(UnsupportedEncodingException e) {
        log.error("[UnsupportedEncodingException] cause: {}, message: {}", e.getCause(), e.getLocalizedMessage());

        res.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        res.setMessage("디코드 오류입니다.");
        res.setError(e.getMessage());
        return ResponseEntity.status(res.getStatusCode()).body(res);
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ResponseDTO> handler(NumberFormatException e) {
        log.error("[NumberFormatException] cause: {}, message: {}", e.getCause(), e.getLocalizedMessage());

        res.setStatusCode(HttpStatus.BAD_REQUEST);
        res.setMessage("숫자 변환 오류입니다.");
        res.setError(e.getMessage());
        return ResponseEntity.status(res.getStatusCode()).body(res);
    }

    @ExceptionHandler(BSValidationException.class)
    public ResponseEntity<ResponseDTO> handler(BSValidationException e) {
        log.error("[BSLengthException] cause: {}, message: {}", e.getCause(), e.getLocalizedMessage());

        res.setStatusCode(HttpStatus.BAD_REQUEST);
        res.setMessage(e.getMessage());
        res.setError(e.getLocalizedMessage());
        return ResponseEntity.status(res.getStatusCode()).body(res);
    }

    @ExceptionHandler(APIException.class)
    public ResponseEntity<ResponseDTO> handler(APIException e) {
        log.error("[APIException] cause: {}, message: {}", e.getCause(), e.getLocalizedMessage());

        res.setStatusCode(HttpStatus.BAD_REQUEST);
        res.setMessage(e.getMessage());
        res.setError(e.getError());
        return ResponseEntity.status(res.getStatusCode()).body(res);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseDTO> handler (IllegalArgumentException e){
        log.error("[IllegalArgumentException] cause: {}, message: {}", e.getCause(), e.getLocalizedMessage());

        res.setStatusCode(HttpStatus.UNAUTHORIZED);
        res.setMessage(e.getMessage());
        res.setError(e.getLocalizedMessage());
        return ResponseEntity.status(res.getStatusCode()).body(res);
    }

    /*@ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO> handler(Exception e) {
        log.error("[Exception] cause: {}, message: {}", e.getCause(), e.getLocalizedMessage());

        res.setMessage(e.getMessage());
        res.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        res.setError(e.getLocalizedMessage());
        return ResponseEntity.status(res.getStatusCode()).body(res);
    }*/
}
