package com.travplan.controllers.web.admin.visit;

import com.travplan.controllers.web.admin._BSAdminController;
import com.travplan.dto.BSQuery;
import com.travplan.models.logs.InflowLog;
import com.travplan.services.visit.InflowLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin/inflow")
public class AdminInflowController extends _BSAdminController {

    @Autowired
    private InflowLogService service;

    @Override
    public String getPrefixPath() {
        return "inflow";
    }

    @Override
    public ModelAndView index(ModelAndView mv) {

        final BSQuery bsq = new BSQuery(InflowLog.class, request);
        bsq.setSelect("*");
        bsq.addType("keyword", "ifl_keyword");
        bsq.addType("ip", "ifl_ip");
        bsq.addType("path", "ifl_path");
        bsq.setFromParams(request);

        mv.addObject("data", service.findAllListView(bsq, InflowLog::new));

        mv.setViewName("admin/inflow/index");
        return mv;
    }
}
