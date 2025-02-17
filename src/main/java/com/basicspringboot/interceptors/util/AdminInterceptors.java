package com.basicspringboot.interceptors.util;

import com.basicspringboot.models.admin.Admin;
import com.basicspringboot.services.manage.AdminMenuService;
import com.basicspringboot.services.manage.AdminPushLogService;
import jakarta.interceptor.Interceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Interceptor
@Slf4j
@RequiredArgsConstructor
public class AdminInterceptors implements HandlerInterceptor {

    private static final String ADMIN_LOGGED_SESSION_KEY = "admin";
    private static final String LOGIN_PAGE_BEFORE_PAGE_SESSION_KEY = "url";

    private final AdminMenuService adminMenuService;
    private final AdminPushLogService adminPushLogService;

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        final HttpSession session = request.getSession();
        session.removeAttribute(LOGIN_PAGE_BEFORE_PAGE_SESSION_KEY);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws IOException {
        final HttpSession session = request.getSession();
        String uri = request.getRequestURI();
        final Admin a = getLoggedAdmin(session);

        final String method = request.getMethod();
        if(!method.equals("POST")) {
            session.removeAttribute("msg");
            session.removeAttribute("err");
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
            response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
            response.setHeader("Expires", "0"); // Proxies.
        }

        session.setAttribute(ADMIN_LOGGED_SESSION_KEY, a);

        request.setAttribute("cm", adminMenuService.getMenuFromUrl(uri));
        request.setAttribute("admin_menus", adminMenuService.findMyAdminMenus(a.getIdx()));
        request.setAttribute("has_push_log", adminPushLogService.hasPushLogs());
    }

    @Override
    @Transactional
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        final HttpSession session = request.getSession();
        final String uri = request.getRequestURI();

        final Admin a = getLoggedAdmin(session);

        final boolean result = a != null;

        if(!result) {
            if(isAjaxRequest(request)) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
            } else {
                if(request.getQueryString() != null && !request.getQueryString().isBlank()) {
                    session.setAttribute(LOGIN_PAGE_BEFORE_PAGE_SESSION_KEY, uri+"?"+request.getQueryString());
                } else {
                    session.setAttribute(LOGIN_PAGE_BEFORE_PAGE_SESSION_KEY, uri);
                }
                response.sendRedirect("/admin/login");
            }
            return false;
        } else {
            return adminMenuService.getAdminRoleCheck(a.getIdx(), uri) || isAjaxRequest(request);
        }
    }

    private Admin getLoggedAdmin(HttpSession session) {
        return (Admin) session.getAttribute(ADMIN_LOGGED_SESSION_KEY);
    }

    private boolean isAjaxRequest(HttpServletRequest request) {
        String requestedWithHeader = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equalsIgnoreCase(requestedWithHeader);
    }
}
