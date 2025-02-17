package com.basicspringboot.controllers.web.admin.contents;

import com.basicspringboot.controllers.web.admin._BSAdminController;
import com.basicspringboot.dto.BSQuery;
import com.basicspringboot.models.others.AppVersion;
import com.basicspringboot.services.contents.AppVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/appver")
public class AdminAppVersionController extends _BSAdminController {

    @Autowired
    private AppVersionService service;

    @Override
    public ModelAndView index(ModelAndView mv) {

        final String os = request.getParameter("os");

        final BSQuery bsq = new BSQuery(AppVersion.class);
        bsq.addType("desc", "av_desc");
        bsq.setFromParams(request);
        if(os != null && !os.isBlank()) bsq.addWhere("av_os = '"+os+"'");
        mv.addObject("data", service.findAllListView(bsq, AppVersion::new));
        mv.setViewName("admin/appver/index");
        return mv;
    }

    @Override
    public ModelAndView view(Long idx, ModelAndView mv) {
        return null;
    }

    @Override
    public ModelAndView insert(ModelAndView mv) {
        return null;
    }

    @Override
    public ModelAndView update(Long idx, ModelAndView mv) {
        final BSQuery bsq = new BSQuery(AppVersion.class);
        bsq.setIdx(idx);

        mv.addObject("data", service.findOne(bsq, AppVersion::new));
        mv.setViewName("admin/appver/update");
        return mv;
    }

    @Override
    public ModelAndView insertProcess(ModelAndView mv, RedirectAttributes ra) {
        return null;
    }

    @Override
    public ModelAndView updateProcess(ModelAndView mv, RedirectAttributes ra) {
        final AppVersion data = new AppVersion(request);
        if(service.update(data)) {
            ra.addFlashAttribute("msg", "앱버전이 수정되었습니다.");
            mv.setViewName(redirect("/admin/appver"));
        } else {
            ra.addFlashAttribute("err", "앱버전이 수정되지 않았습니다.");
            mv.setViewName(redirect("/admin/appver/update"));
        }
        return mv;
    }

    @Override
    public ModelAndView deleteProcess(ModelAndView mv, RedirectAttributes ra) {
        return null;
    }
}
