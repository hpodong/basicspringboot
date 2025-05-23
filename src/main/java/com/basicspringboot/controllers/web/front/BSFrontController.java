package com.basicspringboot.controllers.web.front;

import com.basicspringboot.exceptions.DeleteException;
import com.basicspringboot.exceptions.FileSizeException;
import com.basicspringboot.exceptions.InsertException;
import com.basicspringboot.exceptions.UpdateException;
import com.basicspringboot.interfaces.BSAdminControllerI;
import com.basicspringboot.interfaces.DataSetter;
import com.basicspringboot.models.member.Member;
import com.basicspringboot.models.others.ClientPage;
import com.basicspringboot.models.others.FileModel;
import com.basicspringboot.models.site.SEO;
import com.basicspringboot.services.others.FileService;
import com.basicspringboot.utils.Utils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.*;

@Slf4j
@Controller
public abstract class BSFrontController implements BSAdminControllerI {
    static final String PAGE_DATA_ATTRIBUTE_KEY = "page_data";
    static final String SEO_DATA_ATTRIBUTE_KEY = "seo_date";

    @Autowired
    protected FileService fileService;
    @Autowired
    protected HttpSession session;
    @Autowired
    protected HttpServletRequest request;
    @Autowired
    protected HttpServletResponse response;

    @Override
    public String getPrefixPath(String filename) {
        return getPrefixPath() + "/" + filename;
    }

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
        return fileService.insertReturnKey(file);
    }

    @Transactional
    protected List<FileModel> insertFiles(List<FileModel> files) {
        return fileService.insertMany(files);
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

    @Override
    public ModelAndView index(ModelAndView mv) {
        mv.setViewName(getPrefixPath("index"));
        return mv;
    }

    @Override
    public ModelAndView insert(ModelAndView mv) {
        mv.setViewName(getPrefixPath("insert"));
        return mv;
    }

    @Override
    public ModelAndView update(Long idx, ModelAndView mv) {
        mv.setViewName(getPrefixPath("update"));
        return mv;
    }

    @Override
    public ModelAndView view(Long idx, ModelAndView mv) {
        mv.setViewName(getPrefixPath("view"));
        return mv;
    }

    @Override
    public ModelAndView insertProcess(ModelAndView mv, RedirectAttributes ra) throws InsertException {
        return redirectIndex(mv);
    }

    @Override
    public ModelAndView updateProcess(ModelAndView mv, RedirectAttributes ra) throws UpdateException {
        return redirectIndex(mv);
    }

    @Override
    public ModelAndView deleteProcess(ModelAndView mv, RedirectAttributes ra) throws DeleteException {
        return redirectIndex(mv);
    }

    private ModelAndView redirectIndex(ModelAndView mv) {
        if(this.getClass().isAnnotationPresent(RequestMapping.class)) {
            final RequestMapping mapping = this.getClass().getAnnotation(RequestMapping.class);
            mv.setViewName(redirect(mapping.value()[0]));
        }
        return mv;
    }

    public Member getLoggedMember() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Member) authentication.getPrincipal();
    }

    public Long getLoggedMemberIdx() {
        return getLoggedMember().getIdx();
    }

    private ClientPage getClientPage() {
        return (ClientPage) request.getAttribute(PAGE_DATA_ATTRIBUTE_KEY);
    }
    private SEO getSeo() {
        return (SEO) request.getAttribute(SEO_DATA_ATTRIBUTE_KEY);
    }

    protected void setTitle(String title) {
        final ClientPage page_data = getClientPage();
        final SEO seo = getSeo();

        if(page_data != null) page_data.setTitle(title);
        if(seo != null) seo.setTitle(title);
    }

    protected void setDescription(String description) {
        final SEO seo = getSeo();
        final String newDescription = Utils.stripHtmlTags(description);

        if(seo != null && !newDescription.isBlank()) seo.setDesc(newDescription);
    }

    protected void setKeyword(String keyword) {
        final SEO seo = getSeo();
        if(seo != null) seo.setKeyword(keyword);
    }

    protected void setSubtitle(String subtitle) {
        final ClientPage page_data = getClientPage();
        if(page_data != null) page_data.setSubtitle(subtitle);
    }

    protected void setImageUrl(String url) {
        final SEO seo = getSeo();
        if(seo != null) seo.setImage_url(url);
    }

    protected String redirectPostPage(String url) {
        return redirectPostPage(url, null);
    }

    protected String redirectPostPage(String url, DataSetter setter) {
        final StringBuilder sb = new StringBuilder();
        final Map<String, Object> data = new HashMap<>();
        if(setter != null) setter.setData(data);
        sb.append("<html>");
        sb.append("<body onload='document.forms[\"postForm\"].submit()'>");
        sb.append("<form name='postForm' action='").append(url).append("' method='POST'>");
        for(String key : request.getParameterMap().keySet()) {
            final String[] values = request.getParameterValues(key);
            for(String value : values) {
                sb.append("<input type='hidden' name='").append(key).append("' value='").append(value).append("'/>");
            }
        }
        for(String key : data.keySet()) {
            final Object value = data.get(key);
            sb.append("<input type='hidden' name='").append(key).append("' value='").append(value).append("'/>");
        }
        sb.append("</form>");
        sb.append("</body>");
        sb.append("</html>");

        return sb.toString();
    }

    protected String replacePage(String url) {
        return "<script>" +
                "location.replace('" + url + "')" +
                "</script>";
    }
}
