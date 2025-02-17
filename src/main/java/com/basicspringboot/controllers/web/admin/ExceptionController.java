package com.basicspringboot.controllers.web.admin;

import com.basicspringboot.exceptions.FrontException;
import com.basicspringboot.exceptions.NoDataException;
import com.basicspringboot.exceptions.RequiredLoginException;
import com.basicspringboot.exceptions.MemberCompareException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class ExceptionController {
    @ExceptionHandler(NoHandlerFoundException.class)
    public String notFoundPage(NoHandlerFoundException e, Model m) {
        m.addAttribute("exception", e);
        m.addAttribute("url", e.getHeaders().get("referer"));
        return "/error/404";
    }

    @ExceptionHandler(FrontException.class)
    public String frontException(FrontException e, Model m, RedirectAttributes ra, HttpServletRequest request) {
        ra.addFlashAttribute("err", e.getMessage());
        log.error("FrontException: {}", e.getMessage());
        return redirectPage(getBeforePage(request));
    }

    @ExceptionHandler(NoDataException.class)
    public String emptyResultDataAccessException(NoDataException e, Model m, RedirectAttributes ra, HttpServletRequest request) {
        ra.addFlashAttribute("err", "삭제되었거나 없는 데이터입니다.");
        log.error("NoDataException: {}", e.getMessage());
        return "redirect:"+getBeforePage(request);
    }

    @ExceptionHandler(RequiredLoginException.class)
    public Object expiredLoggedSessionException(RequiredLoginException e, RedirectAttributes ra, HttpServletResponse response, HttpServletRequest request) {
        if (isAjaxRequest(request)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("msg", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        } else {
            ra.addFlashAttribute("msg", e.getMessage());
            return "redirect:/member/login";
        }
    }

    @ExceptionHandler(MemberCompareException.class)
    public String memberCompareException(MemberCompareException e, RedirectAttributes ra, HttpServletRequest request) {
        ra.addFlashAttribute("msg", "권한이 없습니다.");
        return redirectPage(getBeforePage(request));
    }

    /*@ExceptionHandler(BSValidationException.class)
    public String bsLengthException(BSValidationException e, HttpServletRequest request, RedirectAttributes ra) {
        final String referer = request.getHeader("referer");
        ra.addFlashAttribute("err", e.getMessage());
        log.error("BSLengthException: {}", e.getMessage());
        return "redirect:"+referer;
    }*/

    @ExceptionHandler(RuntimeException.class)
    public String runTimeErrorPage(RuntimeException e, HttpServletRequest request) {
        e.printStackTrace();
        request.setAttribute("err", e.getMessage());
        return "/error/500";
    }

    private String getBeforePage(HttpServletRequest request) {
        return request.getHeader("referer");
    }

    private String redirectPage(String page) {
        return "redirect:"+page;
    }

    private boolean isAjaxRequest(HttpServletRequest request) {
        String requestedWithHeader = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equalsIgnoreCase(requestedWithHeader);
    }
}
