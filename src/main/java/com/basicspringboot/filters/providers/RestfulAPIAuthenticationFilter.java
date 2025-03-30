package com.basicspringboot.filters.providers;

import com.basicspringboot.filters.models.ResponseModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class RestfulAPIAuthenticationFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "x-api-key";
    private static final ObjectMapper mapper = new ObjectMapper();

    private final String API_KEY;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String REQ_API_KEY = request.getHeader(API_KEY_HEADER);

        if(API_KEY == null || !API_KEY.equals(REQ_API_KEY)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            final ResponseModel responseModel = new ResponseModel();
            responseModel.setStatusCode(response.getStatus());
            responseModel.setMessage("API 키값이 비어 있거나 일치하지 않습니다.");
            responseModel.setError("NOT FOUND API KEY");

            response.getWriter().write(mapper.writeValueAsString(responseModel));
            return;
        }

        filterChain.doFilter(request, response);
    }
}
