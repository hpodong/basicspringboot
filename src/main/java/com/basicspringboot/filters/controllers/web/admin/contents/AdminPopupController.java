package com.basicspringboot.filters.controllers.web.admin.contents;

import com.basicspringboot.filters.controllers.web.admin._BSAdminController;
import com.basicspringboot.filters.dto.BSQuery;
import com.basicspringboot.filters.models.others.FileModel;
import com.basicspringboot.filters.models.site.Popup;
import com.basicspringboot.filters.services.contents.PopupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/popup")
public class AdminPopupController extends _BSAdminController {

    private final String UPLOAD_DIR = "/popup";

    private final int MAX_SIZE = 1024*1024*10;

    private final int THUMBNAIL_WIDTH = 100;
    private final int THUMBNAIL_HEIGHT = 100;

    @Autowired
    private PopupService service;

    @Override
    public String getPrefixPath() {
        return "admin/popup";
    }

    @Override
    public ModelAndView index(ModelAndView mv) {

        final BSQuery bsq = new BSQuery(Popup.class);

        bsq.setSelect("*");
        bsq.addJoin("LEFT JOIN file ON file.f_idx = popup.f_idx");
        bsq.addType("title", "pu_title");

        bsq.setFromParams(request);

        final String device = request.getParameter("dv");

        if(hasParameter(device)) bsq.addWhere("pu_device = '"+device+"'");

        mv.addObject("data", service.findAllListView(bsq, Popup::new));

        return super.index(mv);
    }

    @Override
    public ModelAndView insert(ModelAndView mv) {
        return super.insert(mv);
    }

    @Override
    public ModelAndView update(Long idx, ModelAndView mv) {
        mv.addObject("data", findPopupFromIdx(idx));
        return super.update(idx, mv);
    }

    @Override
    public ModelAndView insertProcess(ModelAndView mv, RedirectAttributes ra) {
        final Popup data = new Popup(request);
        final FileModel file = getFile(UPLOAD_DIR, "image_file", MAX_SIZE);

        final Long file_idx = insertFile(file);

        data.setFile_idx(file_idx);

        if(service.insert(data, true)) {
            if(file.create(data.getWidth(), data.getHeight())) file.createThumbnail(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
            ra.addFlashAttribute("msg", "팝업이 입력되었습니다.");
            return super.insertProcess(mv, ra);
        } else {
            ra.addFlashAttribute("err", "팝업이 입력되지 않았습니다.");
            return super.insert(mv);
        }
    }

    @Override
    @Transactional
    public ModelAndView updateProcess(ModelAndView mv, RedirectAttributes ra) {
        final Popup data = new Popup(request);
        final FileModel file = getFile(UPLOAD_DIR, "image_file", MAX_SIZE);
        Popup beforePopup = null;

        if(file != null) {
            final Long file_idx = insertFile(file);
            data.setFile_idx(file_idx);
            beforePopup = findPopupFromIdx(data.getIdx());
        }

        if(service.update(data, true)) {
            if(file != null && file.create(data.getWidth(), data.getWidth())) {
                file.createThumbnail(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
                if(beforePopup != null && beforePopup.getFile() != null) {
                    beforePopup.getFile().remove(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
                }
            }
            ra.addFlashAttribute("msg", "팝업이 수정되었습니다.");
            return super.updateProcess(mv, ra);
        } else {
            ra.addFlashAttribute("err", "팝업이 수정되지 않았습니다.");
            return super.update(data.getIdx(), mv);
        }
    }

    @Override
    @Transactional
    public ModelAndView deleteProcess(ModelAndView mv, RedirectAttributes ra) {
        final String[] idx = request.getParameterValues("idx");
        final BSQuery bsq = new BSQuery(Popup.class);
        bsq.setSelect("*");
        bsq.addJoin("LEFT JOIN file ON file.f_idx = popup.f_idx");
        bsq.addWhere("pu_idx IN("+String.join(",", idx)+")");
        bsq.setLimit(null);
        final List<Popup> popups = service.findAll(bsq, Popup::new);
        popups.forEach(popup -> {
            final FileModel file = popup.getFile();
            if(file != null && file.hasFile()) {
                file.remove(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
            }
        });

        final int result = service.delete(idx);
        if(result > 0) {
            ra.addFlashAttribute("msg", result+"개의 팝업이 삭제되었습니다.");
        } else {
            ra.addFlashAttribute("err", "팝업이 삭제되지 않았습니다.");
        }
        return super.deleteProcess(mv, ra);
    }

    private Popup findPopupFromIdx(Long idx) {
        final BSQuery bsq = new BSQuery(Popup.class);

        bsq.setSelect("*");
        bsq.addJoin("LEFT JOIN file ON file.f_idx = popup.f_idx");
        bsq.setIdx(idx);

        return service.findOne(bsq, Popup::new);
    }
}
