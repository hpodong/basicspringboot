package com.basicspringboot.interceptors.app;

import com.basicspringboot.exceptions.NoMatchApiKeyException;
import com.basicspringboot.exceptions.NotFoundApiKeyException;
import jakarta.interceptor.Interceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Interceptor
@Component
public class AppInterceptors implements HandlerInterceptor {

    @Value("${api.key}")
    private String API_KEY;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        final String apiKey = request.getHeader("x-api-key");
        if(apiKey == null) {
            throw new NotFoundApiKeyException();
        } else if(!API_KEY.equals(apiKey)) {
            throw new NoMatchApiKeyException();
        } else {
            return true;
        }
    }
}
