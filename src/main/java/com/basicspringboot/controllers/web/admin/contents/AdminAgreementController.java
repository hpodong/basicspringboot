package com.basicspringboot.controllers.web.admin.contents;

import com.basicspringboot.controllers.web.admin._BSAdminController;
import com.basicspringboot.dto.BSQuery;
import com.basicspringboot.enums.APStatus;
import com.basicspringboot.models.board.Agreement;
import com.basicspringboot.services.contents.AgreementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/agreement")
public class AdminAgreementController extends _BSAdminController {

    @Autowired
    private AgreementService service;

    @Override
    public ModelAndView index(ModelAndView mv) {

        final String category_idx = request.getParameter("c");

        final BSQuery bsq = new BSQuery(Agreement.class);
        bsq.setSelect("*");
        bsq.addType("title", "agm_title");
        bsq.addType("desc", "agm_desc");
        bsq.setFromParams(request);

        if(hasParameter(category_idx)) bsq.addWhere("agreement.agmc_idx = "+category_idx);

        bsq.setOrderBy("agm_status", "agmc_sort", "agm_sort", "agm_crdt DESC");
        bsq.addJoin("LEFT JOIN agreement_category ON agreement.agmc_idx = agreement_category.agmc_idx");

        mv.addObject("categories", service.getCategories());
        mv.addObject("data", service.findAllListView(bsq, Agreement::new));

        mv.setViewName("admin/agreement/index");
        return mv;
    }

    @Override
    public ModelAndView view(Long idx, ModelAndView mv) {

        final BSQuery bsq = new BSQuery(Agreement.class);
        bsq.setSelect("*");
        bsq.setIdx(idx);
        bsq.addJoin("LEFT JOIN agreement_category ON agreement.agmc_idx = agreement_category.agmc_idx");

        mv.addObject("data", service.findOne(bsq, Agreement::new));

        mv.setViewName("admin/agreement/view");
        return mv;
    }

    @Override
    public ModelAndView insert(ModelAndView mv) {
        mv.addObject("categories", service.getCategories());
        mv.setViewName("admin/agreement/insert");
        return mv;
    }

    @Override
    public ModelAndView update(Long idx, ModelAndView mv) {
        final BSQuery bsq = new BSQuery(Agreement.class);
        bsq.setIdx(idx);

        mv.addObject("categories", service.getCategories());
        mv.addObject("data", service.findOne(bsq, Agreement::new));
        mv.setViewName("admin/agreement/update");
        return mv;
    }

    @Override
    public ModelAndView insertProcess(ModelAndView mv, RedirectAttributes ra) {
        final Agreement data = new Agreement(request);
        if(service.insert(data)) {
            mv.setViewName(redirect("/admin/agreement"));
            ra.addFlashAttribute("msg", "약관동의가 등록되었습니다.");
        } else {
            mv.setViewName(redirect("/admin/agreement/insert"));
            ra.addFlashAttribute("err", "약관동의가 등록되지 않았습니다.");
        }
        return mv;
    }

    @Override
    public ModelAndView updateProcess(ModelAndView mv, RedirectAttributes ra) {
        final Agreement data = new Agreement(request);
        if(service.update(data)) {
            mv.setViewName(redirect("/admin/agreement"));
            ra.addFlashAttribute("msg", "약관동의가 수정되었습니다.");
        } else {
            mv.setViewName(redirect("/admin/agreement/update"));
            ra.addFlashAttribute("err", "약관동의가 수정되지 않았습니다.");
        }
        return mv;
    }

    @Override
    public ModelAndView deleteProcess(ModelAndView mv, RedirectAttributes ra) {
        final String[] idx = request.getParameterValues("idx");
        final int result = service.deleteFromIdx(Agreement.class, idx);
        if(result > 0) {
            ra.addFlashAttribute("msg", result+"개의 약관동의가 삭제되었습니다.");
        } else {
            ra.addFlashAttribute("err", "약관동의가 삭제되지 않았습니다.");
        }
        mv.setViewName(redirect("/admin/agreement"));
        return mv;
    }

    @ResponseBody
    @PostMapping("/update/status")
    public boolean changeStatus(@RequestParam Long idx, @RequestParam APStatus status) {
        final Agreement notice = new Agreement();
        notice.setIdx(idx);
        notice.setStatus(status);
        return service.update(notice);
    }
}
