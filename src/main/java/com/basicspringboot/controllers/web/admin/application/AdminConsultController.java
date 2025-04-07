package com.basicspringboot.controllers.web.admin.application;

import com.basicspringboot.controllers.web.admin.BSAdminController;
import com.basicspringboot.dto.BSQuery;
import com.basicspringboot.exceptions.FileSizeException;
import com.basicspringboot.models.consults.Consult;
import com.basicspringboot.models.others.FileModel;
import com.basicspringboot.services.application.ConsultService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin/consult")
@RequiredArgsConstructor
public class AdminConsultController extends BSAdminController {

    private static final String UPLOAD_DIR = "/consult/answer";
    private static final int MAX_SIZE = 1024*1024*10;

    private final ConsultService service;

    @Override
    public String getPrefixPath() {
        return "consult";
    }

    @Override
    public ModelAndView index(ModelAndView mv) {
        final String category = request.getParameter("ct");
        final BSQuery bsq = new BSQuery(Consult.class);
        bsq.addSelect("csc_name");
        if(category != null && !category.isBlank()) bsq.addWhere("consult.csc_idx = "+category);

        bsq.addType("name", "cs_name");
        bsq.addType("cell", "cs_cell");
        bsq.addType("desc", "cs_desc");

        bsq.setFromParams(request);
        bsq.addJoin("LEFT JOIN consult_category ON consult_category.csc_idx = consult.csc_idx");

        bsq.setOrderBy("IF(cs_status = 'waiting', 0, 1)", "cs_crdt ASC", "cs_idx");

        mv.addObject("data", service.findAllListView(bsq, Consult::new));
        mv.addObject("categories", service.getCategories());
        mv.setViewName("admin/consult/index");
        return mv;
    }

    @Override
    @Transactional
    public ModelAndView view(Long idx, ModelAndView mv) {
        mv.addObject("data", getDataFromIdx(idx));
        mv.addObject("files", service.getFiles(idx));
        mv.setViewName("admin/consult/view");
        return mv;
    }

    @Override
    public ModelAndView insert(ModelAndView mv) {
        mv.addObject("categories", service.getCategories());
        mv.setViewName(redirect("/admin"));
        return mv;
    }

    @Override
    public ModelAndView update(Long idx, ModelAndView mv) {
        mv.addObject("categories", service.getCategories());
        mv.addObject("data", getDataFromIdx(idx));
        mv.setViewName(redirect("/admin"));
        return mv;
    }

    @Override
    public ModelAndView insertProcess(ModelAndView mv, RedirectAttributes ra) {
        return mv;
    }

    @Override
    @Transactional
    public ModelAndView updateProcess(ModelAndView mv, RedirectAttributes ra) {
        final Consult data = new Consult(request);

        if(removeTag(data.getAnswer()).isBlank()) data.setStatus("waiting");
        else {
            data.setStatus("completed");
            data.setAnswered_at(Timestamp.valueOf(LocalDateTime.now()));
        }

        try {
            final FileModel file = getFile(UPLOAD_DIR, "input_file", MAX_SIZE);
            final Consult beforeData = getDataFromIdx(data.getIdx());

            if(file != null) {
                final Long file_idx = insertFile(file);
                data.setAnswer_file_idx(file_idx);
                if(file_idx != null && beforeData.getAnswer_file_idx() != null) {
                    beforeData.getAnswer_file().remove();
                    deleteFile(beforeData.getAnswer_file());
                }
            }

            if(service.update(data)) {
                if(file != null) file.create();
                ra.addFlashAttribute("msg", "답변이 입력되었습니다.");
                mv.setViewName(redirect("/admin/consult"));
            } else {
                ra.addFlashAttribute("err", "답변이 입력되지 않았습니다.");
                mv.setViewName(redirect(beforePage()));
            }
        } catch (FileSizeException e) {
            ra.addFlashAttribute("err", e.getMessage());
            mv.setViewName(redirect(beforePage()));
        }
        return mv;
    }

    @Override
    public ModelAndView deleteProcess(ModelAndView mv, RedirectAttributes ra) {
        final String[] idx = request.getParameterValues("idx");
        final int result = service.deleteFromIdx(Consult.class, idx);
        if(result > 0) {
            ra.addFlashAttribute("msg", result+"개의 상담이 삭제되었습니다.");
        } else {
            ra.addFlashAttribute("err", "상담이 삭제되지 않았습니다.");
        }
        mv.setViewName(redirect("/admin/consult"));
        return mv;
    }

    private Consult getDataFromIdx(Long idx) {
        final BSQuery bsq = new BSQuery(Consult.class);
        bsq.addSelect("csc_name");
        bsq.addSelect("file.*");
        bsq.setIdx(idx);
        bsq.addJoin("LEFT JOIN consult_category ON consult_category.csc_idx = consult.csc_idx");
        bsq.addJoin("LEFT JOIN file ON file.f_idx = consult.f_idx");

        return service.findOne(bsq, Consult::new);
    }
}
