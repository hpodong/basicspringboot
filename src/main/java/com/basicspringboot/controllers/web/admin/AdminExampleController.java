package com.basicspringboot.controllers.web.admin;

import com.basicspringboot.dto.BSQuery;
import com.basicspringboot.enums.APStatus;
import com.basicspringboot.exceptions.DeleteException;
import com.basicspringboot.exceptions.InsertException;
import com.basicspringboot.exceptions.UpdateException;
import com.basicspringboot.models.Example;
import com.basicspringboot.models.others.FileModel;
import com.basicspringboot.services.ExampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/example")
public class AdminExampleController extends BSAdminController {

    private static final String UPLOAD_DIR = "/example";
    private static final int MAX_SIZE = 1024*1024*10;

    @Autowired
    private ExampleService service;

    @Override
    public String getPrefixPath() {
        return "admin/_example";
    }

    @Override
    public ModelAndView index(ModelAndView mv) {

        final BSQuery<Example> bsq = new BSQuery<>(Example.class);
        bsq.addType("title", "t_title");
        bsq.addType("description", "t_description");
        bsq.setFromParams(request);

        bsq.addQuerySelect();

        bsq.setSelect("*");
        mv.addObject("statuses", APStatus.values());
        mv.addObject("data", service.findAllListView(bsq, Example::new));
        return super.index(mv);
    }

    @Override
    public ModelAndView view(Long idx, ModelAndView mv) {
        final BSQuery<Example> bsq = new BSQuery<>(Example.class);
        bsq.setIdx(idx);
        mv.addObject("data", service.findOne(bsq, Example::new));
        return super.view(idx, mv);
    }

    @Override
    public ModelAndView insert(ModelAndView mv) {
        mv.addObject("statuses", APStatus.values());
        return super.insert(mv);
    }

    @Override
    public ModelAndView update(Long idx, ModelAndView mv) {
        final BSQuery<Example> bsq = new BSQuery<>(Example.class);
        bsq.setIdx(idx);
        mv.addObject("statuses", APStatus.values());
        mv.addObject("data", service.findOne(bsq, Example::new));
        return super.update(idx, mv);
    }

    @Transactional
    @Override
    public ModelAndView insertProcess(ModelAndView mv, RedirectAttributes ra) {
        final Example data = new Example(request);
        final Long idx = service.insertReturnKey(data);

        //1:1 파일 입력
        /*final FileModel file = getFile(UPLOAD_DIR, "file");
        data.setFileIdx(insertFile(file));*/

        if(idx != null) {
            //1:다 파일 입력
            /*final List<FileModel> files = getFiles(UPLOAD_DIR, "files", MAX_SIZE);
            if(files != null && !files.isEmpty()) {
                final List<FileModel> insertedFiles = insertFiles(files);
                createFiles(files);
                service.insertFiles(idx, insertedFiles);
            }*/
            ra.addFlashAttribute("msg", "데이터가 등록되었습니다.");
            return super.insertProcess(mv, ra);
        } else {
            throw new InsertException("데이터가 등록되지 않았습니다.");
        }
    }

    @Override
    public ModelAndView updateProcess(ModelAndView mv, RedirectAttributes ra) {
        final Example data = new Example(request);
        if(service.update(data)) {
            ra.addFlashAttribute("msg", "데이터가 수정되었습니다.");
            return super.updateProcess(mv, ra);
        } else {
            throw new UpdateException("데이터가 수정되지 않았습니다.");
        }
    }

    @Override
    public ModelAndView deleteProcess(ModelAndView mv, RedirectAttributes ra) {
        final String[] idx = request.getParameterValues("idx");
        final int result = service.deleteFromIdx(Example.class, idx);
        if(result > 0) {
            ra.addFlashAttribute("msg", result+"개의 데이터가 삭제되었습니다.");
            return super.deleteProcess(mv, ra);
        } else {
            throw new DeleteException("데이터가 삭제되지 않았습니다.");
        }
    }
}
