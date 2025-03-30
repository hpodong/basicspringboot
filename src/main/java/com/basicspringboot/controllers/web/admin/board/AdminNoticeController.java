package com.basicspringboot.controllers.web.admin.board;

import com.basicspringboot.controllers.web.admin._BSAdminController;
import com.basicspringboot.dto.BSQuery;
import com.basicspringboot.exceptions.FileSizeException;
import com.basicspringboot.models.board.Notice;
import com.basicspringboot.models.others.FileModel;
import com.basicspringboot.services.board.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/notice")
public class AdminNoticeController extends _BSAdminController {

    private final String UPLOAD_DIR = "/notice";
    private final int MAX_SIZE = 1024*1024*10;

    @Autowired
    private NoticeService service;

    @Override
    public String getPrefixPath() {
        return "notice";
    }

    @Override
    public ModelAndView index(ModelAndView mv) {

        final BSQuery bsq = new BSQuery(Notice.class);

        bsq.addType("title", "n_title");
        bsq.addType("desc", "n_desc");
        bsq.setFromParams(request);

        bsq.setOrderBy("n_status", "n_is_top DESC", "n_crdt DESC");

        mv.addObject("data", service.findAllListView(bsq, Notice::new));

        mv.setViewName("admin/notice/index");
        return mv;
    }

    @Override
    @Transactional
    public ModelAndView view(Long idx, ModelAndView mv) {

        mv.addObject("data", findDataFromIdx(idx));
        mv.addObject("files", service.getFilesFromDataIdx(idx));

        mv.setViewName("admin/notice/view");
        return mv;
    }

    @Override
    public ModelAndView insert(ModelAndView mv) {
        mv.setViewName("admin/notice/insert");
        return mv;
    }

    @Override
    public ModelAndView update(Long idx, ModelAndView mv) {
        final BSQuery bsq = new BSQuery(Notice.class);

        bsq.setIdx(idx);

        mv.addObject("data", findDataFromIdx(idx));
        mv.addObject("files", service.getFilesFromDataIdx(idx));
        mv.setViewName("admin/notice/update");
        return mv;
    }

    @Override
    @Transactional
    public ModelAndView insertProcess(ModelAndView mv, RedirectAttributes ra) {
        try {
            final Notice data = new Notice(request);
            final List<FileModel> files = getFiles(UPLOAD_DIR, "input_files", MAX_SIZE);

            data.setHas_file(!files.isEmpty());
            final Long idx = service.insertReturnKey(data);

            if(idx != null) {
                service.insertFiles(files, idx);
                mv.setViewName(redirect("/admin/notice"));
                ra.addFlashAttribute("msg", "공지사항이 등록되었습니다.");
            } else {
                mv.setViewName(redirect(beforePage()));
                ra.addFlashAttribute("err", "공지사항이 등록되지 않았습니다.");
            }
        } catch (FileSizeException e) {
            mv.setViewName(redirect(beforePage()));
            ra.addFlashAttribute("err", e.getMessage());
        }
        return mv;
    }

    @Override
    @Transactional
    public ModelAndView updateProcess(ModelAndView mv, RedirectAttributes ra) {
        try {
            final String[] delete_file_idx = request.getParameterValues("delete_file_idx");
            int delete_file_length = delete_file_idx == null ? 0 : delete_file_idx.length;
            final Notice data = new Notice(request);
            final List<FileModel> files = getFiles(UPLOAD_DIR, "input_files", MAX_SIZE);

            final Map<String, Object> fileData = service.getFileMaxSortFromDataIdx(data.getIdx());

            final Integer maxSort = (Integer) fileData.get("max");
            final int count = (Integer) fileData.get("count");

            data.setHas_file(!files.isEmpty() || delete_file_length < count);
            data.setIs_top(data.getIs_top() != null);

            if(service.update(data)) {
                if(delete_file_idx != null) deleteFileFromIdx((Object[]) delete_file_idx);
                service.insertFiles(files, data.getIdx(), maxSort == null ? null : maxSort + 1);
                mv.setViewName(redirect("/admin/notice"));
                ra.addFlashAttribute("msg", "공지사항이 수정되었습니다.");
            } else {
                mv.setViewName(redirect(beforePage()));
                ra.addFlashAttribute("err", "공지사항이 수정되지 않았습니다.");
            }
        } catch (FileSizeException e) {
            mv.setViewName(redirect(beforePage()));
            ra.addFlashAttribute("err", e.getMessage());
        }
        return mv;
    }

    @Override
    public ModelAndView deleteProcess(ModelAndView mv, RedirectAttributes ra) {
        final String[] idx = request.getParameterValues("idx");
        final int result = service.deleteFromIdx(Notice.class, idx);
        if(result > 0) {
            ra.addFlashAttribute("msg", result+"개의 공지사항이 삭제되었습니다.");
        } else {
            ra.addFlashAttribute("err", "공지사항이 삭제되지 않았습니다.");
        }
        mv.setViewName(redirect("/admin/notice"));
        return mv;
    }

    private Notice findDataFromIdx(Long idx) {
        final BSQuery bsq = new BSQuery(Notice.class);
        bsq.setSelect("*");
        bsq.setIdx(idx);
        return service.findOne(bsq, Notice::new);
    }
}
