package com.basicspringboot.controllers.web;

import com.basicspringboot.enums.JWTType;
import com.basicspringboot.interfaces.BSAdminControllerI;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;


@Controller
public abstract class _BSControllerController implements BSAdminControllerI {
    protected final JWTType jwtType = JWTType.MEMBER;
    protected Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    protected HttpSession session;
    @Autowired
    protected HttpServletRequest request;
    @Autowired
    protected HttpServletResponse response;

    protected String redirect(String url) {
        return "redirect:" + url;
    }
    protected String beforePage() {
        return request.getHeader("referer");
    }
    protected String redirectBeforePage() {
        return redirect(beforePage());
    }
}
