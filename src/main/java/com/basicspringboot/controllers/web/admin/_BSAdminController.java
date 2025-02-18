package com.basicspringboot.controllers.web.admin;

import com.basicspringboot.exceptions.FileSizeException;
import com.basicspringboot.interfaces.BSAdminControllerI;
import com.basicspringboot.models._BSModel;
import com.basicspringboot.models.admin.Admin;
import com.basicspringboot.models.others.FileModel;
import com.basicspringboot.services.others.FileService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Controller
public abstract class _BSAdminController implements BSAdminControllerI {
    protected Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected FileService fileService;
    @Autowired
    protected HttpSession session;
    @Autowired
    protected HttpServletRequest request;
    @Autowired
    protected HttpServletResponse response;

    protected String redirect(String url) {
        return "redirect:" + url;
    }
    protected String beforePage() {
        return request.getHeader("referer");
    }

    protected boolean isNotEmpty(String value) {
        return value != null && !value.isBlank();
    }

    protected FileModel getFile(String folder, String parameter, Integer maxSize) {
        try {
            final Part part = request.getPart(parameter);
            if(part == null || part.getSize() <= 0) return null;
            else if(maxSize != null && part.getSize() > maxSize) throw new FileSizeException(maxSize);
            return new FileModel(folder, request.getPart(parameter));
        } catch (IOException | ServletException e) {
            return null;
        }
    }

    protected FileModel getFile(String folder, String parameter) {
        return getFile(folder, parameter, null);
    }

    protected List<FileModel> getFiles(String folder, String parameter) {
        return getFiles(folder, parameter, null);
    }

    protected List<FileModel> getFiles(String folder, String parameter, Integer maxSize) {
        final List<FileModel> files = new ArrayList<>();
        try {
            final Collection<Part> parts = request.getParts();
            if(parts == null) return null;
            for(Part part : parts) {
                if(maxSize != null && part.getSize() > maxSize) throw new FileSizeException(maxSize);
                else if(part != null && part.getSize() > 0 && part.getName().equals(parameter)) files.add(new FileModel(folder, part));
            }
        } catch (IOException | ServletException e) {
            throw new RuntimeException(e);
        }
        return files;
    }

    protected Long insertFile(FileModel file) {
        if(file == null) return null;
        return fileService.insert(file);
    }

    @Transactional
    protected void insertFiles(List<FileModel> files) {
        if(files != null) {
            files.forEach(file -> {
                fileService.insert(file);
            });
        }
    }

    protected boolean deleteFile(FileModel file) {
        return fileService.deleteFromIdx(FileModel.class, true, file.getIdx()) > 0;
    }

    protected boolean deleteFileFromIdx(Object... idx) {
        return fileService.deleteFromIdx(FileModel.class, true, idx) > 0;
    }

    protected boolean hasParameter(String parameter) {
        return parameter != null && !parameter.isBlank();
    }

    protected String removeTag(String value) {
        return value.replaceAll("<[^>]*>", "");
    }

    protected Admin getLoggedAdmin() {
        return (Admin) session.getAttribute("admin");
    }

    protected Long getLoggedAdminIdx() {
        final Admin loggedAdmin = getLoggedAdmin();
        if(loggedAdmin == null) return null;
        else return loggedAdmin.getIdx();
    }

    @Override
    public ModelAndView insert(ModelAndView mv) {
        return null;
    }

    @Override
    public ModelAndView update(Long idx, ModelAndView mv) {
        return null;
    }

    @Override
    public ModelAndView view(Long idx, ModelAndView mv) {
        return null;
    }

    @Override
    public ModelAndView insertProcess(ModelAndView mv, RedirectAttributes ra) throws IOException {
        return null;
    }

    @Override
    public ModelAndView updateProcess(ModelAndView mv, RedirectAttributes ra) throws IOException, ServletException {
        return null;
    }

    @Override
    public ModelAndView deleteProcess(ModelAndView mv, RedirectAttributes ra) {
        return null;
    }
}
