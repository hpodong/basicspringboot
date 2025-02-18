package com.basicspringboot.controllers.web.admin.manage;

import com.basicspringboot.controllers.web.admin._BSAdminController;
import com.basicspringboot.dto.BSQuery;
import com.basicspringboot.enums.APStatus;
import com.basicspringboot.models.admin.AdminMenu;
import com.basicspringboot.services.manage.AdminMenuService;
import com.basicspringboot.services.manage.AdminRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/menu")
public class AdminMenuController extends _BSAdminController {

    @Autowired
    private AdminMenuService service;

    @Autowired
    private AdminRoleService adminRoleService;

    @Override
    public ModelAndView index(ModelAndView mv) {

        final BSQuery bsq = new BSQuery(AdminMenu.class);
        bsq.addType("title", "am_title");
        bsq.setOrderBy("am_sort", "am_crdt DESC");

        bsq.setFromParams(request);

        mv.setViewName("admin/menu/index");

        mv.addObject("data", service.findAllListView(bsq, AdminMenu::new));
        /*mv.addObject("data", service.getMenus());*/
        return mv;
    }

    @Override
    public ModelAndView view(Long idx, ModelAndView mv) {
        final BSQuery bq = new BSQuery(AdminMenu.class);
        bq.setIdx(idx);


        mv.addObject("data", findByIdx(idx));
        mv.setViewName("admin/menu/view");
        return mv;
    }
    @Override
    public ModelAndView insert(ModelAndView mv) {
        mv.setViewName("admin/menu/insert");
        mv.addObject("menus", service.getAllMenus());
        return mv;
    }

    @Override
    public ModelAndView update(Long idx, ModelAndView mv) {
        final BSQuery bq = new BSQuery(AdminMenu.class);
        bq.setIdx(idx);

        mv.addObject("menus", service.getAllMenus());
        mv.addObject("data", findByIdx(idx).toSetData());
        mv.setViewName("admin/menu/update");
        return mv;
    }

    @Override
    public ModelAndView insertProcess(ModelAndView mv, RedirectAttributes ra) {
        final AdminMenu data = new AdminMenu(request);

        final Long result = service.insert(data);
        if(result != null) {
            service.refreshMenus();
            adminRoleService.insertItems(1121, result.toString());
            ra.addFlashAttribute("msg", "관리자 메뉴가 입력되었습니다.");
            mv.setViewName(redirect("/admin/menu"));
        } else {
            ra.addFlashAttribute("err", "관리자 메뉴가 입력되지 않았습니다.");
            mv.setViewName(redirect("/admin/menu/insert"));
        }
        return mv;
    }

    @Override
    public ModelAndView updateProcess(ModelAndView mv, RedirectAttributes ra) {
        final AdminMenu data = new AdminMenu(request);

        final boolean result = service.update(data, true);
        if(result) {
            service.refreshMenus();
            ra.addFlashAttribute("msg", "관리자 메뉴가 수정되었습니다.");
            mv.setViewName(redirect("/admin/menu"));
        } else {
            ra.addFlashAttribute("err", "관리자 메뉴가 수정되지 않았습니다.");
            mv.setViewName(beforePage());
        }
        return mv;
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
        mv.setViewName(redirect("/admin/menu"));
        return mv;
    }

    @ResponseBody
    @PostMapping("/update/status")
    public boolean updateStatus(@RequestParam Long idx, @RequestParam(value = "st") APStatus status) {
        final AdminMenu data = new AdminMenu();
        data.setIdx(idx);
        data.setStatus(status);
        return service.update(data, false);
    }

    private AdminMenu findByIdx(Long idx) {
        final BSQuery bsq = new BSQuery(AdminMenu.class);
        bsq.setSelect("admin_menu.*", "am.am_title parent_name");
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
