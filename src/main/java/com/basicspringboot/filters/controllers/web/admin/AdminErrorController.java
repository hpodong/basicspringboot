package com.basicspringboot.filters.controllers.web.admin;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/error")
public class AdminErrorController {

    @GetMapping("/access-denied")
    public String error403() {
        return "admin/error/403";
    }
}
