package com.basicspringboot.controllers.web.admin.visit;

import com.basicspringboot.controllers.web.admin._BSAdminController;
import com.basicspringboot.dto.BSQuery;
import com.basicspringboot.models.others.FileModel;
import com.basicspringboot.models.site.SEO;
import com.basicspringboot.models.site.SEOImage;
import com.basicspringboot.services.visit.SEOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
@RequestMapping("/admin/seo")
public class AdminSEOController extends _BSAdminController {

    private static final String UPLOAD_DIR = "/seo";
    private static final int PC_THUMBNAIL_WIDTH = 100;
    private static final int PC_THUMBNAIL_HEIGHT = 50;
    private static final int M_THUMBNAIL_WIDTH = 50;
    private static final int M_THUMBNAIL_HEIGHT = 50;

    @Autowired
    private SEOService service;

    @Override
    public String getPrefixPath() {
        return "admin/seo";
    }

    @Override
    public ModelAndView index(ModelAndView mv) {
        final BSQuery bsq = new BSQuery(SEO.class, request);
        bsq.addSelect("pf.f_url pc_image, mf.f_url m_image");
        bsq.addJoin("LEFT JOIN search_engine_optimization_image pci ON pci.seo_idx = search_engine_optimization.seo_idx AND pci.seoi_type = 'PC'");
        bsq.addJoin("LEFT JOIN file pf ON pci.f_idx = pf.f_idx");
        bsq.addJoin("LEFT JOIN search_engine_optimization_image mi ON mi.seo_idx = search_engine_optimization.seo_idx AND mi.seoi_type = 'MO'");
        bsq.addJoin("LEFT JOIN file mf ON mi.f_idx = mf.f_idx");

        mv.addObject("data", service.findAllListView(bsq, SEO::new));
        return super.index(mv);
    }

    @Override
    @Transactional
    public ModelAndView view(Long idx, ModelAndView mv) {
        final BSQuery bsq = new BSQuery(SEO.class);
        bsq.setIdx(idx);
        final SEO data = service.findOne(bsq, SEO::new);

        if(data == null) {
            return super.index(mv);
        } else {
            mv.addObject("data", data);
            mv.addObject("pc_image", service.findImageFromType(data.getIdx(), "PC"));
            mv.addObject("m_image", service.findImageFromType(data.getIdx(), "MO"));
            return super.view(idx, mv);
        }
    }

    @Override
    public ModelAndView insert(ModelAndView mv) {
        return super.insert(mv);
    }

    @Override
    @Transactional
    public ModelAndView update(Long idx, ModelAndView mv) {
        final BSQuery bsq = new BSQuery(SEO.class);
        bsq.setIdx(idx);
        final SEO data = service.findOne(bsq, SEO::new);

        if(data == null) {
            return super.index(mv);
        } else {
            mv.addObject("data", data);
            mv.addObject("pc_image", service.findImageFromType(data.getIdx(), "PC"));
            mv.addObject("m_image", service.findImageFromType(data.getIdx(), "MO"));
            return super.update(idx, mv);
        }
    }

    @Transactional
    @Override
    public ModelAndView insertProcess(ModelAndView mv, RedirectAttributes ra)  {
        final SEO data = new SEO(request);

        final FileModel pc_file = getFile(UPLOAD_DIR+"/PC", "pc_image");
        final FileModel mo_file = getFile(UPLOAD_DIR+"/MO", "mo_image");

        final Long idx = service.insertReturnKey(data);

        if(idx != null) {
            if(pc_file != null && pc_file.create()) {
                final Long file_idx = insertFile(pc_file);
                if(file_idx != null) {
                    pc_file.createThumbnail(PC_THUMBNAIL_WIDTH, PC_THUMBNAIL_HEIGHT);
                    final SEOImage seoImage = new SEOImage(idx, "PC", file_idx);
                    service.insertImage(seoImage);
                }
            }
            if(mo_file != null && mo_file.create()) {
                final Long file_idx = insertFile(mo_file);
                if(file_idx != null) {
                    mo_file.createThumbnail(M_THUMBNAIL_WIDTH, M_THUMBNAIL_HEIGHT);
                    final SEOImage seoImage = new SEOImage(idx, "MO", file_idx);
                    service.insertImage(seoImage);
                }
            }
            ra.addFlashAttribute("msg", "SEO가 등록되었습니다.");
            return insertProcess(mv, ra);
        } else {
            ra.addFlashAttribute("err", "SEO가 등록되지 않았습니다.");
            return insert(mv);
        }
    }

    @Override
    @Transactional
    public ModelAndView updateProcess(ModelAndView mv, RedirectAttributes ra) {
        final SEO data = new SEO(request);

        final Long idx = data.getIdx();

        final FileModel pc_file = getFile(UPLOAD_DIR+"/PC", "pc_image");
        final FileModel mo_file = getFile(UPLOAD_DIR+"/MO", "mo_image");

        if(pc_file != null && pc_file.create()) {
            final Long file_idx = insertFile(pc_file);
            if(file_idx != null) {
                final SEOImage seoImage = new SEOImage(idx, "PC", file_idx);
                try {
                    if(service.insertImage(seoImage)) pc_file.createThumbnail(PC_THUMBNAIL_WIDTH, PC_THUMBNAIL_HEIGHT);
                } catch (DuplicateKeyException e) {
                    final SEOImage currentImage = service.findImageFromType(idx, "PC");
                    if(currentImage != null) {
                        final FileModel file = currentImage.getFile();
                        file.remove(PC_THUMBNAIL_WIDTH, PC_THUMBNAIL_HEIGHT);
                        if(service.updateImage(seoImage)) pc_file.createThumbnail(PC_THUMBNAIL_WIDTH, PC_THUMBNAIL_HEIGHT);
                    }
                }
            }
        }

        if(mo_file != null && mo_file.create()) {
            final Long file_idx = insertFile(mo_file);
            if(file_idx != null) {
                final SEOImage seoImage = new SEOImage(idx, "MO", insertFile(mo_file));
                try {
                    if(service.insertImage(seoImage)) mo_file.createThumbnail(M_THUMBNAIL_WIDTH, M_THUMBNAIL_HEIGHT);
                } catch (DuplicateKeyException e) {
                    final SEOImage currentImage = service.findImageFromType(idx, "MO");
                    if(currentImage != null) {
                        final FileModel file = currentImage.getFile();
                        file.remove(M_THUMBNAIL_WIDTH, M_THUMBNAIL_HEIGHT);
                        if(service.updateImage(seoImage)) mo_file.createThumbnail(M_THUMBNAIL_WIDTH, M_THUMBNAIL_HEIGHT);
                    }
                }
            }
        }
        if(!service.update(data)) {
            ra.addFlashAttribute("msg", "SEO가 수정되지 않았습니다.");
            return super.update(idx, mv);
        } else {
            ra.addFlashAttribute("msg", "SEO가 수정되었습니다.");
            return super.updateProcess(mv, ra);
        }
    }

    @Override
    public ModelAndView deleteProcess(ModelAndView mv, RedirectAttributes ra) {
        final String[] idx = request.getParameterValues("idx");
        final int result = service.deleteFromIdx(idx);
        if(result > 0) {
            ra.addFlashAttribute("msg", result+"개의 SEO가 삭제되었습니다.");
        } else {
            ra.addFlashAttribute("err", "SEO가 삭제되지 않았습니다.");
        }
        return super.deleteProcess(mv, ra);
    }
}
