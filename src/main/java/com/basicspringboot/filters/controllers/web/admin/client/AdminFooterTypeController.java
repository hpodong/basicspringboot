package com.basicspringboot.filters.controllers.web.admin.client;

import com.basicspringboot.filters.controllers.web.admin._BSAdminController;
import com.basicspringboot.filters.dto.BSQuery;
import com.basicspringboot.filters.models.others.FooterType;
import com.basicspringboot.filters.services.client.FooterTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/client/footer")
public class AdminFooterTypeController extends _BSAdminController {

    @Autowired
    private FooterTypeService service;

    @Override
    public String getPrefixPath() {
        return "client_page/footer";
    }

    @Override
    public ModelAndView index(ModelAndView mv) {

        final BSQuery bsq = new BSQuery(FooterType.class, request);
        bsq.setSelect("*");
        bsq.addType("title", "fot_name");
        bsq.setFromParams(request);

        mv.addObject("data", service.findAllListView(bsq, FooterType::new));

        mv.setViewName("admin/client_page/footer/index");
        return mv;
    }

    @Override
    public ModelAndView view(Long idx, ModelAndView mv) {
        final BSQuery bsq = new BSQuery(FooterType.class);
        bsq.setIdx(idx);
        mv.addObject("data", service.findOne(bsq, FooterType::new));
        mv.setViewName("admin/client_page/footer/view");
        return mv;
    }

    @Override
    public ModelAndView insert(ModelAndView mv) {
        mv.setViewName("admin/client_page/footer/insert");
        return mv;
    }

    @Override
    public ModelAndView update(Long idx, ModelAndView mv) {
        final BSQuery bsq = new BSQuery(FooterType.class);
        bsq.setIdx(idx);

        mv.addObject("data", service.findOne(bsq, FooterType::new).toSetData());
        mv.setViewName("admin/client_page/footer/update");
        return mv;
    }

    @Override
    public ModelAndView insertProcess(ModelAndView mv, RedirectAttributes ra) {
        final FooterType data = new FooterType(request);
        if(service.insert(data)) {
            mv.setViewName(redirect("/admin/client/footer"));
            ra.addFlashAttribute("msg", "푸터 타입이 등록되었습니다.");
        } else {
            mv.setViewName(redirect("/admin/client/footer/insert"));
            ra.addFlashAttribute("err", "푸터 타입이 등록되지 않았습니다.");
        }
        return mv;
    }

    @Override
    public ModelAndView updateProcess(ModelAndView mv, RedirectAttributes ra) {
        final FooterType data = new FooterType(request);
        if(service.update(data)) {
            mv.setViewName(redirect("/admin/client/footer"));
            ra.addFlashAttribute("msg", "푸터 타입이 수정되었습니다.");
        } else {
            mv.setViewName(redirect("/admin/client/footer/update"));
            ra.addFlashAttribute("err", "푸터 타입이 수정되지 않았습니다.");
        }
        return mv;
    }

    @Override
    public ModelAndView deleteProcess(ModelAndView mv, RedirectAttributes ra) {
        final String[] idx = request.getParameterValues("idx");
        final int result = service.deleteFromIdx(FooterType.class, idx);
        if(result > 0) {
            ra.addFlashAttribute("msg", result+"개의 푸터 타입이 삭제되었습니다.");
        } else {
            ra.addFlashAttribute("err", "푸터 타입이 삭제되지 않았습니다.");
        }
        mv.setViewName(redirect("/admin/client/footer"));
        return mv;
    }
}
