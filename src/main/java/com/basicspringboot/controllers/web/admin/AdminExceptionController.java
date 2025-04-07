package com.basicspringboot.controllers.web.admin;

import com.basicspringboot.exceptions.DeleteException;
import com.basicspringboot.exceptions.InsertException;
import com.basicspringboot.exceptions.UpdateException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
@Slf4j
public class AdminExceptionController {

    private String redirectBeforePage(HttpServletRequest request) {
        return "redirect:"+request.getHeader("referer");
    }

    @ExceptionHandler(InsertException.class)
    public String insertErrorPage(InsertException e, HttpServletRequest request, RedirectAttributes ra) {
        ra.addFlashAttribute("err", e.getMessage());
        return redirectBeforePage(request);
    }

    @ExceptionHandler(UpdateException.class)
    public String updateErrorPage(InsertException e, HttpServletRequest request, RedirectAttributes ra) {
        ra.addFlashAttribute("err", e.getMessage());
        return redirectBeforePage(request);
    }

    @ExceptionHandler(DeleteException.class)
    public String deleteErrorPage(InsertException e, HttpServletRequest request, RedirectAttributes ra) {
        ra.addFlashAttribute("err", e.getMessage());
        return redirectBeforePage(request);
    }


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
