package com.travplan.controllers.web.admin;

import com.travplan.exceptions.FrontException;
import com.travplan.exceptions.NoDataException;
import com.travplan.exceptions.RequiredLoginException;
import com.travplan.exceptions.MemberCompareException;
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
public class AdminExceptionController {
    @ExceptionHandler(NoHandlerFoundException.class)
    public String notFoundPage(NoHandlerFoundException e, Model m) {
        m.addAttribute("exception", e);
        return "/admin/error/404";
    }

    @ExceptionHandler(RuntimeException.class)
    public String runTimeErrorPage(RuntimeException e, HttpServletRequest request) {
        request.setAttribute("err", e.getMessage());
        return "/admin/error/500";
    }
}
