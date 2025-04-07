package com.basicspringboot.interceptors;

import com.basicspringboot.models.member.Member;
import com.basicspringboot.models.others.ClientPage;
import com.basicspringboot.models.site.Popup;
import com.basicspringboot.models.site.SEO;
import com.basicspringboot.services.client.ClientPageService;
import com.basicspringboot.services.contents.PopupService;
import com.basicspringboot.services.member.MemberService;
import com.basicspringboot.services.visit.SEOService;
import jakarta.interceptor.Interceptor;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.*;
import java.util.stream.Collectors;

@Interceptor
@Slf4j
@RequiredArgsConstructor
public class FrontInterceptors implements HandlerInterceptor {
    static private final String LOGGED_MEMBER_SESSION_KEY = "member";
    static private final String PAGE_DATA_KEY = "page_data";
    private static final String IDX_COOKIE_KEY = "COOKIE_SAVED_IDX";

    private final ClientPageService clientPageService;
    private final SEOService seoService;
    private final PopupService popupService;
    private final MemberService memberService;

    private final String APP_USERAGENT;

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
        final ClientPage clientPage = (ClientPage) request.getAttribute(PAGE_DATA_KEY);
        if(clientPage != null) clientPageService.addClientPageInflowLogs(clientPage.getIdx());
        final HttpSession session = request.getSession();
        session.removeAttribute("msg");
    }

    @Override
    @Transactional
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        if(isAjaxRequest(request)) return true;

        final String method = request.getMethod();
        if(method.equals("GET")) {
            setSEO(request);
            popupInterceptor(request);
            autoLogin(request);
        }
        setClientPage(request);
        appInterceptor(request);
        return true;
    }

    private void autoLogin(HttpServletRequest request) {
        final HttpSession session = request.getSession();
        if(session.getAttribute(LOGGED_MEMBER_SESSION_KEY) == null) {
            final Cookie[] cookies = request.getCookies();

            if(cookies != null) {
                for (Cookie cookie : cookies) {
                    if(IDX_COOKIE_KEY.equals(cookie.getName())) {
                        final String idx = cookie.getValue();
                        final Member member = memberService.findByIdx(Long.parseLong(idx));
                        session.setAttribute(LOGGED_MEMBER_SESSION_KEY, member);
                    }
                }
            }
        }
    }

    private void popupInterceptor(HttpServletRequest request) {
        final String uri = request.getRequestURI();

        final Cookie[] cookies = request.getCookies();
        final List<Long> closedPopups = new ArrayList<>();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().startsWith("popup_closed_")) {
                    // 쿠키 이름이 "popup_closed_"로 시작하는 경우
                    String popupIdx = cookie.getName().replace("popup_closed_", "");
                    try {
                        closedPopups.add(Long.parseLong(popupIdx));

                    } catch (NumberFormatException e) {
                        // 예외 처리 (int로 변환할 수 없을 때)
                    }
                }
            }
        }


        final List<Popup> activatedPopup;
        if(uri.equals("/")) activatedPopup = popupService.getMains();
        else activatedPopup = popupService.getPopups();
        // 쿠키에 있는 idx 값들을 제외한 팝업들만 필터링
        List<Popup> filteredPopups = activatedPopup.stream()
                .filter(popup -> !closedPopups.contains(popup.getIdx()))
                .collect(Collectors.toList());

        // 필터링된 팝업 목록을 request에 저장
        request.setAttribute("activated_popups", filteredPopups);
    }

    private void setClientPage(HttpServletRequest request) {
        final String uri = request.getRequestURI();
        final ClientPage cp = clientPageService.getDataFromURI(uri);
        request.setAttribute(PAGE_DATA_KEY, cp);
    }

    private void setSEO(HttpServletRequest request) {
        final String uri = request.getRequestURI();
        final SEO seo = seoService.getDataFromURI(uri);
        if(seo != null) seo.setImageFromRequest(request);
        request.setAttribute("seo_data", seo);
    }

    private void appInterceptor(HttpServletRequest request) {
        final String useragent = request.getHeader("User-Agent");

        if(useragent.contains(APP_USERAGENT)) request.setAttribute("IS_APP", true);
    }

    private boolean isAjaxRequest(HttpServletRequest request) {
        String requestedWithHeader = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equalsIgnoreCase(requestedWithHeader);
    }
}
