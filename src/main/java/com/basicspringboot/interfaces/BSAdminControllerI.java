package com.basicspringboot.interfaces;

import jakarta.servlet.ServletException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

public interface BSAdminControllerI {

    @GetMapping("")
    ModelAndView index(ModelAndView mv);
    @GetMapping("/view")
    ModelAndView view(@RequestParam Long idx, ModelAndView mv);
    @GetMapping("/insert")
    ModelAndView insert(ModelAndView mv);
    @GetMapping("/update")
    ModelAndView update(@RequestParam Long idx, ModelAndView mv);
    @PostMapping("/insert")
    ModelAndView insertProcess(ModelAndView mv, RedirectAttributes ra) throws IOException;
    @PostMapping("/update")
    ModelAndView updateProcess(ModelAndView mv, RedirectAttributes ra) throws IOException, ServletException;
    @PostMapping("/delete")
    ModelAndView deleteProcess(ModelAndView mv, RedirectAttributes ra);
}
