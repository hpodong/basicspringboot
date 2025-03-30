package com.basicspringboot.controllers.web.admin;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

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
