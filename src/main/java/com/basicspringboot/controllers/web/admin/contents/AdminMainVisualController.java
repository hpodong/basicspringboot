package com.basicspringboot.controllers.web.admin.contents;

import com.basicspringboot.controllers.web.admin._BSAdminController;
import com.basicspringboot.dto.BSQuery;
import com.basicspringboot.models.others.FileModel;
import com.basicspringboot.models.site.MainVisual;
import com.basicspringboot.services.contents.MainVisualService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin/mainvisual")
public class AdminMainVisualController extends _BSAdminController {

    private final String UPLOAD_DIR = "/mainvisual";
    private final int MAX_SIZE = 1024*1024*10;
    private final int THUMBNAIL_WIDTH = 200;
    private final int THUMBNAIL_HEIGHT = 100;

    @Autowired
    private MainVisualService service;

    @Override
    public ModelAndView index(ModelAndView mv) {

        final BSQuery bsq = new BSQuery(MainVisual.class);

        bsq.addType("title", "mv_title");

        bsq.setFromParams(request);

        bsq.setSelect("*");
        bsq.addJoin("LEFT JOIN file ON file.f_idx = main_visual.f_idx");

        bsq.setOrderBy("mv_sort", "mv_crdt DESC", "mv_idx DESC");

        mv.addObject("data", service.findAllListView(bsq, MainVisual::new));

        mv.setViewName("admin/mainvisual/index");

        return mv;
    }

    @Override
    public ModelAndView insert(ModelAndView mv) {
        mv.setViewName("admin/mainvisual/insert");
        return mv;
    }

    @Override
    @Transactional
    public ModelAndView update(Long idx, ModelAndView mv) {
        final BSQuery bsq = new BSQuery(MainVisual.class);
        bsq.setIdx(idx);

        mv.addObject("data", findDataFromIdx(idx));
        mv.setViewName("admin/mainvisual/update");
        return mv;
    }

    @Override
    @Transactional
    public ModelAndView insertProcess(ModelAndView mv, RedirectAttributes ra) throws IOException {
        final MainVisual data = new MainVisual(request);
        final FileModel file = getFile(UPLOAD_DIR, "image_file", MAX_SIZE);

        final Long file_idx = insertFile(file);

        data.setFile_idx(file_idx);

        if(service.insert(data)) {
            if(file.create()) file.createThumbnail(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
            ra.addFlashAttribute("msg", "메인비주얼이 입력되었습니다.");
            mv.setViewName(redirect("/admin/mainvisual"));
        } else {
            ra.addFlashAttribute("err", "메인비주얼이 입력되지 않았습니다.");
            mv.setViewName(redirect("/admin/mainvisual/insert"));
        }
        return mv;
    }

    @Override
    @Transactional
    public ModelAndView updateProcess(ModelAndView mv, RedirectAttributes ra) throws IOException {
        final MainVisual data = new MainVisual(request);
        final FileModel file = getFile(UPLOAD_DIR, "image_file", MAX_SIZE);
        MainVisual beforeData = null;

        if(file != null) {
            final Long file_idx = insertFile(file);
            data.setFile_idx(file_idx);
            beforeData = findDataFromIdx(data.getIdx());
        }


        if(service.update(data)) {
            if(file != null && file.create()) {
                file.createThumbnail(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
                if(beforeData != null && beforeData.getFile() != null) beforeData.getFile().remove(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
            }
            service.refresh();
            ra.addFlashAttribute("msg", "메인비주얼이 수정되었습니다.");
            mv.setViewName(redirect("/admin/mainvisual"));
        } else {
            ra.addFlashAttribute("err", "메인비주얼이 수정되지 않았습니다.");
            mv.setViewName(redirect("/admin/mainvisual/update?idx=" + data.getIdx()));
        }
        return mv;
    }

    @Override
    @Transactional
    public ModelAndView deleteProcess(ModelAndView mv, RedirectAttributes ra) {
        final String[] idx = request.getParameterValues("idx");

        final BSQuery bsq = new BSQuery(MainVisual.class);
        bsq.setSelect("*");
        bsq.addJoin("LEFT JOIN file ON file.f_idx = main_visual.f_idx");
        bsq.addWhere("mv_idx IN("+String.join(",", idx)+")");
        bsq.setLimit(null);

        final List<MainVisual> list = service.findAll(bsq, MainVisual::new);
        list.forEach(visual -> {
            final FileModel file = visual.getFile();
            if(file != null && file.hasFile()) {
                file.remove(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
            }
        });

        final int result = service.deleteFromIdx(MainVisual.class, idx);
        if(result > 0) {
            service.refresh();
            ra.addFlashAttribute("msg", result+"개의 메인비주얼이 삭제되었습니다.");
        } else {
            ra.addFlashAttribute("err", "메인비주얼이 삭제되지 않았습니다.");
        }
        mv.setViewName(redirect("/admin/mainvisual"));
        return mv;
    }

    @ResponseBody
    @PostMapping("/update/sort")
    public boolean updateSortFromIdx(@RequestBody List<MainVisual> visuals) {
        final int result = service.updateSort(visuals);
        return result > 0;
    }

    private MainVisual findDataFromIdx(Long idx) {
        final BSQuery bsq = new BSQuery(MainVisual.class);

        bsq.setSelect("*");
        bsq.addJoin("LEFT JOIN file ON file.f_idx = main_visual.f_idx");
        bsq.setIdx(idx);

        return service.findOne(bsq, MainVisual::new);
    }
}
