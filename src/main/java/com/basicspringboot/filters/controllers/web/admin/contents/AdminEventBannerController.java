package com.basicspringboot.filters.controllers.web.admin.contents;

import com.basicspringboot.filters.controllers.web.admin._BSAdminController;
import com.basicspringboot.filters.dto.BSQuery;
import com.basicspringboot.filters.models.others.FileModel;
import com.basicspringboot.filters.models.site.EventBanner;
import com.basicspringboot.filters.services.contents.EventBannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/event-banner")
public class AdminEventBannerController extends _BSAdminController {

    private final String UPLOAD_DIR = "/event_banner";
    private final int MAX_SIZE = 1024*1024*10;
    private final int THUMBNAIL_WIDTH = 200;
    private final int THUMBNAIL_HEIGHT = 100;

    @Autowired
    private EventBannerService service;

    @Override
    public String getPrefixPath() {
        return "/event-banner";
    }

    @Override
    public ModelAndView index(ModelAndView mv) {

        final BSQuery bsq = new BSQuery(EventBanner.class);

        bsq.addType("title", "eb_title");

        bsq.setFromParams(request);

        bsq.setSelect("*");
        bsq.addJoin("LEFT JOIN file ON file.f_idx = "+bsq.getTable()+".f_idx");

        bsq.setOrderBy("eb_sort", "eb_crdt DESC", "eb_idx DESC");

        mv.addObject("data", service.findAllListView(bsq, EventBanner::new));

        mv.setViewName("admin/event-banner/index");

        return mv;
    }

    @Override
    public ModelAndView view(Long idx, ModelAndView mv) {
        return null;
    }

    @Override
    public ModelAndView insert(ModelAndView mv) {
        mv.setViewName("admin/event-banner/insert");
        return mv;
    }

    @Override
    @Transactional
    public ModelAndView update(Long idx, ModelAndView mv) {
        final BSQuery bsq = new BSQuery(EventBanner.class);
        bsq.setIdx(idx);

        mv.addObject("data", findDataFromIdx(idx));
        mv.setViewName("admin/event-banner/update");
        return mv;
    }

    @Override
    @Transactional
    public ModelAndView insertProcess(ModelAndView mv, RedirectAttributes ra) {
        final EventBanner data = new EventBanner(request);
        final FileModel file = getFile(UPLOAD_DIR, "image_file", MAX_SIZE);

        final Long file_idx = insertFile(file);

        data.setFile_idx(file_idx);

        if(service.insert(data)) {
            if(file.create()) file.createThumbnail(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
            ra.addFlashAttribute("msg", "이벤트배너가 입력되었습니다.");
            mv.setViewName(redirect("/admin/event-banner"));
        } else {
            ra.addFlashAttribute("err", "이벤트배너가 입력되지 않았습니다.");
            mv.setViewName(redirect("/admin/event-banner/insert"));
        }
        return mv;
    }

    @Override
    @Transactional
    public ModelAndView updateProcess(ModelAndView mv, RedirectAttributes ra) {
        final EventBanner data = new EventBanner(request);
        final FileModel file = getFile(UPLOAD_DIR, "image_file", MAX_SIZE);
        EventBanner beforeData = null;

        if(file != null) {
            final Long file_idx = insertFile(file);
            data.setFile_idx(file_idx);
            beforeData = findDataFromIdx(data.getIdx());
        }


        if(service.update(data)) {
            if(file != null && file.create()) {
                file.createThumbnail(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
                if(beforeData != null && beforeData.getFile() != null) {
                    beforeData.getFile().remove(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
                }
            }
            service.refresh();
            ra.addFlashAttribute("msg", "이벤트배너가 수정되었습니다.");
            mv.setViewName(redirect("/admin/event-banner"));
        } else {
            ra.addFlashAttribute("err", "이벤트배너가 수정되지 않았습니다.");
            mv.setViewName(redirect("/admin/event-banner/update?idx=" + data.getIdx()));
        }
        return mv;
    }

    @Override
    @Transactional
    public ModelAndView deleteProcess(ModelAndView mv, RedirectAttributes ra) {
        final String[] idx = request.getParameterValues("idx");

        final BSQuery bsq = new BSQuery(EventBanner.class);
        bsq.setSelect("*");
        bsq.addJoin("LEFT JOIN file ON file.f_idx = event_banner.f_idx");
        bsq.addWhere("eb_idx IN("+String.join(",", idx)+")");
        bsq.setLimit(null);

        final List<EventBanner> list = service.findAll(bsq, EventBanner::new);
        list.forEach(visual -> {
            final FileModel file = visual.getFile();
            if(file != null && file.hasFile()) {
                file.remove(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
            }
        });

        final int result = service.deleteFromIdx(EventBanner.class, idx);
        if(result > 0) {
            service.refresh();
            ra.addFlashAttribute("msg", result+"개의 이벤트배너가 삭제되었습니다.");
        } else {
            ra.addFlashAttribute("err", "이벤트배너가 삭제되지 않았습니다.");
        }
        mv.setViewName(redirect("/admin/event-banner"));
        return mv;
    }

    @ResponseBody
    @PostMapping("/update/sort")
    public boolean updateSortFromIdx(@RequestBody List<EventBanner> visuals) {
        final int result = service.updateSort(visuals);
        return result > 0;
    }

    private EventBanner findDataFromIdx(Long idx) {
        final BSQuery bsq = new BSQuery(EventBanner.class);
        bsq.setSelect("*");
        bsq.addJoin("LEFT JOIN file ON file.f_idx = event_banner.f_idx");
        bsq.setIdx(idx);

        return service.findOne(bsq, EventBanner::new);
    }
}
