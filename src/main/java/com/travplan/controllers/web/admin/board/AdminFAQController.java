package com.travplan.controllers.web.admin.board;
import com.travplan.controllers.web.admin._BSAdminController;
import com.travplan.dto.BSQuery;
import com.travplan.enums.APStatus;
import com.travplan.models.board.FAQ;
import com.travplan.models.site.MainVisual;
import com.travplan.services.board.FAQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/faq")
public class AdminFAQController extends _BSAdminController {

    @Autowired
    private FAQService service;

    @Override
    public String getPrefixPath() {
        return "faq";
    }

    @Override
    @Transactional
    public ModelAndView index(ModelAndView mv) {
        final String category_idx = request.getParameter("c");
        final BSQuery bsq = new BSQuery(FAQ.class, request);

        bsq.setSelect("*");
        bsq.addType("question", "faq_question");
        bsq.addType("asked", "faq_asked");
        bsq.setOrderBy("faq_status", "faqc_sort", "faq_sort", "faq_crdt");
        bsq.addJoin("LEFT JOIN faq_category ON faq.faqc_idx = faq_category.faqc_idx");
        if(category_idx != null && !category_idx.isBlank()) {
            final String addWhere = "faq_category.faqc_idx = "+ category_idx;
            bsq.addWhere(addWhere);
        }

        mv.addObject("data", service.findAllListView(bsq, FAQ::new));
        mv.addObject("categories", service.getCategories());
        mv.setViewName("admin/faq/index");
        return mv;
    }

    @Override
    public ModelAndView view(Long idx, ModelAndView mv) {
        mv.addObject("data", findDataFromIdx(idx));
        mv.setViewName("admin/faq/view");
        return mv;
    }

    @Override
    public ModelAndView insert(ModelAndView mv) {
        mv.addObject("categories", service.getCategories());

        mv.setViewName("admin/faq/insert");
        return mv;
    }

    @Override
    public ModelAndView update(Long idx, ModelAndView mv) {

        mv.addObject("data", findDataFromIdx(idx));
        mv.addObject("categories", service.getCategories());

        mv.setViewName("admin/faq/update");
        return mv;
    }

    @Override
    public ModelAndView insertProcess(ModelAndView mv, RedirectAttributes ra) {
        final FAQ faq = new FAQ(request);
        if(service.insert(faq)) {
            mv.setViewName(redirect("/admin/faq"));
            ra.addFlashAttribute("msg", "FAQ가 등록되었습니다.");
        } else {
            mv.setViewName(redirect("/admin/faq/insert"));
            ra.addFlashAttribute("err", "FAQ가 등록되지 않았습니다.");
        }
        return mv;
    }

    @Override
    @Transactional
    public ModelAndView updateProcess(ModelAndView mv, RedirectAttributes ra) {
        final FAQ faq = new FAQ(request);
        if(service.update(faq)) {
            mv.setViewName(redirect("/admin/faq"));
            ra.addFlashAttribute("msg", "FAQ가 수정되었습니다.");
        } else {
            mv.setViewName(redirect("/admin/faq/update"));
            ra.addFlashAttribute("err", "FAQ가 수정되지 않았습니다.");
        }
        return mv;
    }

    @Override
    public ModelAndView deleteProcess(ModelAndView mv, RedirectAttributes ra) {
        final String[] idx = request.getParameterValues("idx");
        final int result = service.deleteFromIdx(FAQ.class, idx);
        if(result > 0) {
            ra.addFlashAttribute("msg", result+"개의 FAQ가 삭제되었습니다.");
        } else {
            ra.addFlashAttribute("err", "FAQ가 삭제되지 않았습니다.");
        }
        mv.setViewName(redirect("/admin/faq"));
        return mv;
    }

    @ResponseBody
    @PostMapping("/update/status")
    public boolean changeStatus(@RequestParam Long idx, @RequestParam APStatus status) {
        final FAQ data = new FAQ();
        data.setIdx(idx);
        data.setStatus(status);
        return service.update(data);
    }

    private FAQ findDataFromIdx(Long idx) {
        final BSQuery bsq = new BSQuery(FAQ.class);
        final String tn = bsq.getTable();

        bsq.setSelect("*");
        bsq.addJoin("LEFT JOIN faq_category ON "+tn+".faqc_idx = faq_category.faqc_idx");
        bsq.setIdx(idx);

        return service.findOne(bsq, FAQ::new);
    }
}