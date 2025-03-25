package com.travplan.controllers.web.admin.manage;

import com.travplan.controllers.web.admin._BSAdminController;
import com.travplan.dto.BSQuery;
import com.travplan.models.logs.AdminConnectLog;
import com.travplan.services.manage.AdminConnectLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/log/connect")
public class AdminConnectLogController extends _BSAdminController {

    @Autowired
    private AdminConnectLogService service;

    @Override
    public String getPrefixPath() {
        return "log/connect";
    }

    @Override
    public ModelAndView index(ModelAndView mv) {
        final BSQuery bsq = new BSQuery(AdminConnectLog.class);
        bsq.setSelect("*");
        bsq.addType("id", "a_id");
        bsq.addType("name", "a_name");
        bsq.addType("ip", "acl_remote_ip");
        bsq.setFromParams(request);
        bsq.addJoin("LEFT JOIN admin ON admin.a_idx = admin_connect_log.a_idx");
        bsq.addJoin("LEFT JOIN admin_role ON admin_role.ar_idx = admin.ar_idx");
        mv.addObject("data", service.findAllListView(bsq, AdminConnectLog::new));
        mv.setViewName("admin/log/connect/index");
        return mv;
    }

    @Override
    public ModelAndView view(Long idx, ModelAndView mv) {
        mv.setViewName("admin/log/connect/view");
        return mv;
    }

    @Override
    public ModelAndView insert(ModelAndView mv) {
        mv.setViewName("admin/log/connect/insert");
        return mv;
    }

    @Override
    public ModelAndView update(Long idx, ModelAndView mv) {
        mv.setViewName("admin/log/connect/update");
        return mv;
    }

    @Override
    public ModelAndView insertProcess(ModelAndView mv, RedirectAttributes ra) {
        return mv;
    }

    @Override
    public ModelAndView updateProcess(ModelAndView mv, RedirectAttributes ra) {
        return mv;
    }

    @Override
    public ModelAndView deleteProcess(ModelAndView mv, RedirectAttributes ra) {
        return mv;
    }
}
