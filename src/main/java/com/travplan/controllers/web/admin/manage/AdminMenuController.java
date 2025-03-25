package com.travplan.controllers.web.admin.manage;

import com.travplan.controllers.web.admin._BSAdminController;
import com.travplan.dto.BSQuery;
import com.travplan.enums.APStatus;
import com.travplan.models.admin.AdminMenu;
import com.travplan.models.others.FileModel;
import com.travplan.services.manage.AdminMenuService;
import com.travplan.services.manage.AdminRoleService;
import com.travplan.services.others.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/menu")
public class AdminMenuController extends _BSAdminController {

    private static final String UPLOAD_DIR = "/admin/menu";
    private static final int MAX_SIZE = 1024 * 1024;

    private final AdminMenuService service;
    private final AdminRoleService adminRoleService;
    private final FileService fileService;

    @Override
    public String getPrefixPath() {
        return "admin/menu";
    }

    @Override
    public ModelAndView index(ModelAndView mv) {

        final String tp = request.getParameter("tp");

        final BSQuery bsq = new BSQuery(AdminMenu.class);

        bsq.addType("title", "am_title");
        bsq.addType("desc", "am_desc");
        bsq.addType("link", "am_link");

        bsq.setFromParams(request);

        bsq.setOrderBy("am_type", "am_sort", "am_crdt DESC");

        if(hasParameter(tp)) {
            bsq.addWhere("am_type = ?");
            bsq.addArgs(tp);
        }

        mv.addObject("statuses", APStatus.values());
        mv.addObject("data", service.findAllListView(bsq, AdminMenu::new));
        return super.index(mv);
    }

    @Override
    public ModelAndView view(Long idx, ModelAndView mv) {
        final BSQuery bq = new BSQuery(AdminMenu.class);
        bq.setIdx(idx);


        mv.addObject("data", findByIdx(idx));
        return super.view(idx, mv);
    }
    @Override
    public ModelAndView insert(ModelAndView mv) {
        mv.addObject("menus", service.getAllMenus());
        return super.insert(mv);
    }

    @Override
    public ModelAndView update(Long idx, ModelAndView mv) {
        final BSQuery bq = new BSQuery(AdminMenu.class);
        bq.setIdx(idx);

        final AdminMenu data = findByIdx(idx);

        mv.addObject("menus", service.getAllMenus());
        mv.addObject("data", data);
        mv.addObject("set_data", data.toSetData());
        return super.update(idx, mv);
    }

    @Override
    public ModelAndView insertProcess(ModelAndView mv, RedirectAttributes ra) {
        final AdminMenu data = new AdminMenu(request);
        final FileModel file = getFile(UPLOAD_DIR, "icon_file", MAX_SIZE);

        if(file != null) data.setFile_idx(insertFile(file));

        final Long result = service.insertReturnKey(data);
        if(result != null) {
            if(file != null) file.create();
            service.refreshMenus();
            adminRoleService.insertItems(1121, result.toString());
            ra.addFlashAttribute("msg", "관리자 메뉴가 입력되었습니다.");
            return super.insertProcess(mv, ra);
        } else {
            ra.addFlashAttribute("err", "관리자 메뉴가 입력되지 않았습니다.");
            return super.insert(mv);
        }
    }

    @Transactional
    @Override
    public ModelAndView updateProcess(ModelAndView mv, RedirectAttributes ra) {
        final AdminMenu data = new AdminMenu(request);
        final FileModel file = getFile(UPLOAD_DIR, "icon_file", MAX_SIZE);

        final String delete_file_idx = request.getParameter("delete_file_idx");
        final boolean is_delete_icon = delete_file_idx != null;
        final FileModel before_icon = fileService.findByIdx(data.getFile_idx());

        if(is_delete_icon) data.setFile_idx(null);

        if(file != null) data.setFile_idx(insertFile(file));

        final boolean result = service.update(data, true);
        if(result) {
            if((file != null && file.create() && before_icon != null) || is_delete_icon) {
                if(before_icon != null) before_icon.remove();
            }
            service.refreshMenus();
            ra.addFlashAttribute("msg", "관리자 메뉴가 수정되었습니다.");
            return super.updateProcess(mv, ra);
        } else {
            ra.addFlashAttribute("err", "관리자 메뉴가 수정되지 않았습니다.");
            return super.update(data.getIdx(), mv);
        }
    }

    @Override
    public ModelAndView deleteProcess(ModelAndView mv, RedirectAttributes ra) {
        final String[] idx = request.getParameterValues("idx");
        final int result = service.deleteFromIdx(AdminMenu.class, idx);
        if(result > 0) {
            service.refreshMenus();
            ra.addFlashAttribute("msg", result+"개의 메뉴가 삭제되었습니다.");
        } else {
            ra.addFlashAttribute("err", "메뉴가 삭제되지 않았습니다.");
        }
        return super.deleteProcess(mv, ra);
    }

    private AdminMenu findByIdx(Long idx) {
        final BSQuery bsq = new BSQuery(AdminMenu.class);
        bsq.setSelect("admin_menu.*", "am.am_title parent_name", "file.*");
        bsq.addJoin("LEFT JOIN file ON file.f_idx = admin_menu.f_idx");
        bsq.addJoin("LEFT JOIN admin_menu am ON am.am_idx = admin_menu.am_fk");
        bsq.setIdx(idx);

        final AdminMenu data = service.findOne(bsq, AdminMenu::new);

        final BSQuery children_bsq = new BSQuery(AdminMenu.class);
        children_bsq.setWhere("am_dldt IS NULL", "am_fk = "+idx);
        children_bsq.setLimit(null);
        children_bsq.setOrderBy("am_sort", "am_crdt");

        data.setChildren(service.findAll(children_bsq, AdminMenu::new));
        return data;
    }
}
