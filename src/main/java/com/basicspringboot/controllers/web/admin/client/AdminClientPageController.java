package com.basicspringboot.controllers.web.admin.client;

import com.basicspringboot.controllers.web.admin._BSAdminController;
import com.basicspringboot.dto.BSQuery;
import com.basicspringboot.enums.APStatus;
import com.basicspringboot.models.others.ClientPage;
import com.basicspringboot.services.client.ClientPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/client_page")
public class AdminClientPageController extends _BSAdminController {

    @Autowired
    private ClientPageService service;

    @Override
    public ModelAndView index(ModelAndView mv) {

        final BSQuery bsq = new BSQuery(ClientPage.class);
        final String tn = bsq.getTable();

        bsq.addType("title", "cp_title");
        bsq.addType("link", "cp_link");

        bsq.addJoin("LEFT JOIN header_type ON header_type.ht_idx = "+tn+".ht_idx");
        bsq.addJoin("LEFT JOIN footer_type ON footer_type.fot_idx = "+tn+".fot_idx");

        bsq.setOrderBy("cp_crdt DESC", "cp_idx DESC");

        bsq.setFromParams(request);

        mv.setViewName("admin/client_page/index");

        mv.addObject("data", service.findAllListView(bsq, ClientPage::new));
        return mv;
    }

    @Override
    public ModelAndView view(Long idx, ModelAndView mv) {
        mv.addObject("data", findByIdx(idx));
        mv.setViewName("admin/client_page/view");
        return mv;
    }
    @Override
    public ModelAndView insert(ModelAndView mv) {
        mv.addObject("header_types",service.getHeaderTypes());
        mv.addObject("footer_types",service.getFooterTypes());
        mv.setViewName("admin/client_page/insert");
        return mv;
    }

    @Override
    public ModelAndView update(Long idx, ModelAndView mv) {
        final ClientPage data = findByIdx(idx);
        mv.addObject("header_types",service.getHeaderTypes());
        mv.addObject("footer_types",service.getFooterTypes());
        mv.addObject("data", data.toSetData());
        log.info("DATA : {}", data.toSetData());
        mv.setViewName("admin/client_page/update");
        return mv;
    }

    @Override
    public ModelAndView insertProcess(ModelAndView mv, RedirectAttributes ra) {
        final ClientPage data = new ClientPage(request);

        final Long result = service.insert(data);
        if(result != null) {
            ra.addFlashAttribute("msg", "회원 페이지가 입력되었습니다.");
            mv.setViewName(redirect("/admin/client_page"));
        } else {
            ra.addFlashAttribute("err", "회원 페이지가 입력되지 않았습니다.");
            mv.setViewName(redirect("/admin/client_page/insert"));
        }
        return mv;
    }

    @Override
    public ModelAndView updateProcess(ModelAndView mv, RedirectAttributes ra) {
        final ClientPage data = new ClientPage(request);

        final boolean result = service.update(data);
        if(result) {
            ra.addFlashAttribute("msg", "회원 페이지가 수정되었습니다.");
            mv.setViewName(redirect("/admin/client_page"));
        } else {
            ra.addFlashAttribute("err", "회원 페이지가 수정되지 않았습니다.");
            mv.setViewName(beforePage());
        }
        return mv;
    }

    @Override
    public ModelAndView deleteProcess(ModelAndView mv, RedirectAttributes ra) {
        final String[] idx = request.getParameterValues("idx");
        final int result = service.deleteFromIdx(ClientPage.class, idx);
        if(result > 0) {
            ra.addFlashAttribute("msg", result+"개의 회원 페이지가 삭제되었습니다.");
        } else {
            ra.addFlashAttribute("err", "회원 페이지가 삭제되지 않았습니다.");
        }
        mv.setViewName(redirect("/admin/client_page"));
        return mv;
    }

    @ResponseBody
    @PostMapping("/update/status")
    public boolean updateStatus(@RequestParam Long idx, @RequestParam(value = "st") APStatus status) {
        final ClientPage data = new ClientPage();
        data.setIdx(idx);
        data.setStatus(status);
        return service.update(data);
    }

    private ClientPage findByIdx(Long idx) {
        final BSQuery bsq = new BSQuery(ClientPage.class);
        final String tn = bsq.getTable();

        bsq.setSelect("*");

        bsq.addJoin("LEFT JOIN header_type ON header_type.ht_idx = "+tn+".ht_idx");
        bsq.addJoin("LEFT JOIN footer_type ON footer_type.fot_idx = "+tn+".fot_idx");
        bsq.setIdx(idx);
        return service.findOne(bsq, ClientPage::new);
    }
}
