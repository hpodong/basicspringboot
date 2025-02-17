package com.basicspringboot.interceptors.util;

import jakarta.interceptor.Interceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Interceptor
@Slf4j
@RequiredArgsConstructor
public class AlertInterceptors implements HandlerInterceptor {

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        final HttpSession session = request.getSession();
        final String method = request.getMethod();
        if(!method.equals("POST")) {
            session.removeAttribute("msg");
            session.removeAttribute("err");
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
            response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
            response.setHeader("Expires", "0"); // Proxies.
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    @Transactional
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        return HandlerInterceptor.super.preHandle(req, res, handler);
    }
}
