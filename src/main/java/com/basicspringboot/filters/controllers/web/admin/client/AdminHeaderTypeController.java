package com.basicspringboot.filters.controllers.web.admin.client;

import com.basicspringboot.filters.controllers.web.admin._BSAdminController;
import com.basicspringboot.filters.dto.BSQuery;
import com.basicspringboot.filters.models.others.HeaderType;
import com.basicspringboot.filters.services.client.HeaderTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/client/header")
public class AdminHeaderTypeController extends _BSAdminController {

    @Autowired
    private HeaderTypeService service;

    @Override
    public String getPrefixPath() {
        return "client_page/header";
    }

    @Override
    public ModelAndView index(ModelAndView mv) {

        final BSQuery bsq = new BSQuery(HeaderType.class, request);
        bsq.setSelect("*");
        bsq.addType("title", "ht_name");
        bsq.setFromParams(request);

        mv.addObject("data", service.findAllListView(bsq, HeaderType::new));

        mv.setViewName("admin/client_page/header/index");
        return mv;
    }

    @Override
    public ModelAndView view(Long idx, ModelAndView mv) {
        final BSQuery bsq = new BSQuery(HeaderType.class);
        bsq.setIdx(idx);
        mv.addObject("data", service.findOne(bsq, HeaderType::new));
        mv.setViewName("admin/client_page/header/view");
        return mv;
    }

    @Override
    public ModelAndView insert(ModelAndView mv) {
        mv.setViewName("admin/client_page/header/insert");
        return mv;
    }

    @Override
    public ModelAndView update(Long idx, ModelAndView mv) {
        final BSQuery bsq = new BSQuery(HeaderType.class);
        bsq.setIdx(idx);

        mv.addObject("data", service.findOne(bsq, HeaderType::new).toSetData());
        mv.setViewName("admin/client_page/header/update");
        return mv;
    }

    @Override
    public ModelAndView insertProcess(ModelAndView mv, RedirectAttributes ra) {
        final HeaderType data = new HeaderType(request);
        if(service.insert(data)) {
            mv.setViewName(redirect("/admin/client/header"));
            ra.addFlashAttribute("msg", "헤더 타입이 등록되었습니다.");
        } else {
            mv.setViewName(redirect("/admin/client/header/insert"));
            ra.addFlashAttribute("err", "헤더 타입이 등록되지 않았습니다.");
        }
        return mv;
    }

    @Override
    public ModelAndView updateProcess(ModelAndView mv, RedirectAttributes ra) {
        final HeaderType data = new HeaderType(request);
        if(service.update(data)) {
            mv.setViewName(redirect("/admin/client/header"));
            ra.addFlashAttribute("msg", "헤더 타입이 수정되었습니다.");
        } else {
            mv.setViewName(redirect("/admin/client/header/update"));
            ra.addFlashAttribute("err", "헤더 타입이 수정되지 않았습니다.");
        }
        return mv;
    }

    @Override
    public ModelAndView deleteProcess(ModelAndView mv, RedirectAttributes ra) {
        final String[] idx = request.getParameterValues("idx");
        final int result = service.deleteFromIdx(HeaderType.class, idx);
        if(result > 0) {
            ra.addFlashAttribute("msg", result+"개의 헤더 타입이 삭제되었습니다.");
        } else {
            ra.addFlashAttribute("err", "헤더 타입이 삭제되지 않았습니다.");
        }
        mv.setViewName(redirect("/admin/client/header"));
        return mv;
    }
}
