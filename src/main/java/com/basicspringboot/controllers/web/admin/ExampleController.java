package com.basicspringboot.controllers.web.admin;

import com.basicspringboot.dto.BSQuery;
import com.basicspringboot.models._Example;
import com.basicspringboot.services._ExampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/example")
public class ExampleController extends _BSAdminController {

    @Autowired
    private _ExampleService service;

    @Override
    public String getPrefixPath() {
        return "_example";
    }

    @Override
    public ModelAndView index(ModelAndView mv) {

        final BSQuery bsq = new BSQuery(_Example.class, request);
        bsq.setSelect("*");
        bsq.addType("title", "yv_title");
        bsq.setFromParams(request);

        mv.addObject("data", service.findAllListView(bsq, _Example::new));

        mv.setViewName("admin/_example/index");
        return mv;
    }

    @Override
    public ModelAndView view(Long idx, ModelAndView mv) {
        final BSQuery bsq = new BSQuery(_Example.class);
        bsq.setIdx(idx);
        mv.addObject("data", service.findOne(bsq, _Example::new));
        mv.setViewName("admin/order/cancel/view");
        return mv;
    }

    @Override
    public ModelAndView insert(ModelAndView mv) {
        mv.setViewName("admin/_example/insert");
        return mv;
    }

    @Override
    public ModelAndView update(Long idx, ModelAndView mv) {
        final BSQuery bsq = new BSQuery(_Example.class);
        bsq.setIdx(idx);

        mv.addObject("data", service.findOne(bsq, _Example::new));
        mv.setViewName("admin/_example/update");
        return mv;
    }

    @Override
    public ModelAndView insertProcess(ModelAndView mv, RedirectAttributes ra) {
        final _Example data = new _Example(request);
        if(service.insert(data)) {
            mv.setViewName(redirect("/admin/_example"));
            ra.addFlashAttribute("msg", "유튜브 영상이 등록되었습니다.");
        } else {
            mv.setViewName(redirect("/admin/_example/insert"));
            ra.addFlashAttribute("err", "유튜브 영상이 등록되지 않았습니다.");
        }
        return mv;
    }

    @Override
    public ModelAndView updateProcess(ModelAndView mv, RedirectAttributes ra) {
        final _Example data = new _Example(request);
        if(service.update(data)) {
            mv.setViewName(redirect("/admin/_example"));
            ra.addFlashAttribute("msg", "유튜브 영상이 수정되었습니다.");
        } else {
            mv.setViewName(redirect("/admin/_example/update"));
            ra.addFlashAttribute("err", "유튜브 영상이 수정되지 않았습니다.");
        }
        return mv;
    }

    @Override
    public ModelAndView deleteProcess(ModelAndView mv, RedirectAttributes ra) {
        final String[] idx = request.getParameterValues("idx");
        final int result = service.deleteFromIdx(_Example.class, idx);
        if(result > 0) {
            ra.addFlashAttribute("msg", result+"개의 유튜브 영상이 삭제되었습니다.");
        } else {
            ra.addFlashAttribute("err", "유튜브 영상이 삭제되지 않았습니다.");
        }
        mv.setViewName(redirect("/admin/_example"));
        return mv;
    }
}
