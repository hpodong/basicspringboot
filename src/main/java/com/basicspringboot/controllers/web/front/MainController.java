package com.basicspringboot.controllers.web.front;

import com.basicspringboot.models.logs.InflowLog;
import com.basicspringboot.models.logs.PageLog;
import com.basicspringboot.services.visit.InflowLogService;
import com.basicspringboot.services.visit.PageLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.net.URI;

@RequestMapping("/")
@Slf4j
@Controller
public class MainController extends BSFrontController{

    @Value("${server.address}")
    private String SERVER_HOST;

    @Autowired
    private InflowLogService inflowLogService;

    @Autowired
    private PageLogService pageLogService;

    @Override
    public String getPrefixPath() {
        return "front";
    }

    @Override
    public ModelAndView index(ModelAndView mv) {
        return super.index(mv);
    }

    @GetMapping("/login")
    public ModelAndView login(ModelAndView mv) {
        mv.setViewName(getPrefixPath("login"));
        setTitle("황장우");
        setDescription("황장우페이지");
        setKeyword("상공");
        return mv;
    }

    @PostMapping("/api/add/page-log")
    @ResponseBody
    public boolean addPageLog(@RequestParam String url) {
        final PageLog pageLog = new PageLog(request, url);
        if(!SERVER_HOST.equals(getRefererHost())) {
            inflowLogService.insert(new InflowLog(request));
        }
        return pageLogService.addLog(pageLog);
    }

    private String getRefererHost() {
        final String url = request.getHeader("Referer");
        final URI uri = URI.create(url);
        return uri.getHost();
    }
}
