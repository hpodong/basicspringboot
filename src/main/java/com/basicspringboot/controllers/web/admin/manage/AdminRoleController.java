package com.basicspringboot.controllers.web.admin.manage;

import com.basicspringboot.controllers.web.admin.BSAdminController;
import com.basicspringboot.dto.BSQuery;
import com.basicspringboot.models.admin.AdminMenu;
import com.basicspringboot.models.admin.AdminRole;
import com.basicspringboot.services.manage.AdminMenuService;
import com.basicspringboot.services.manage.AdminRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/role")
public class AdminRoleController extends BSAdminController {

    @Autowired
    private AdminRoleService service;
    @Autowired
    private AdminMenuService adminMenuService;

    @Override
    public String getPrefixPath() {
        return "role";
    }

    @Override
    public ModelAndView index(ModelAndView mv) {

        final BSQuery bsq = new BSQuery(AdminRole.class, request);

        bsq.setOrderBy("ar_sort", "ar_crdt DESC");

        mv.addObject("data", service.findAllListView(bsq, AdminRole::new));
        mv.setViewName("admin/role/index");
        return mv;
    }

    @Override
    public ModelAndView view(Long idx, ModelAndView mv) {
        final BSQuery bq = new BSQuery(AdminRole.class);
        bq.setIdx(idx);

        final List<AdminMenu> menus = adminMenuService.findRolesFromIdx(idx);
        adminMenuService.factoryMenusWithChildren(menus);

        final AdminRole data = service.findOne(bq, AdminRole::new);
        mv.addObject("data", data);
        mv.addObject("menus", adminMenuService.factoryParentMenus(menus));
        mv.setViewName("admin/role/view");
        return mv;
    }

    @Override
    public ModelAndView insert(ModelAndView mv) {
        mv.addObject("menus", adminMenuService.getMenus());
        mv.setViewName("admin/role/insert");
        return mv;
    }

    @Override
    @Transactional
    public ModelAndView update(Long idx, ModelAndView mv) {
        final BSQuery bq = new BSQuery(AdminRole.class);
        mv.addObject("menus", adminMenuService.getMenus());
        bq.setIdx(idx);

        mv.addObject("items", service.findItemsIdx(idx));

        final AdminRole data = service.findOne(bq, AdminRole::new);
        mv.addObject("data", data.toSetData());
        mv.setViewName("admin/role/update");
        return mv;
    }

    @Transactional
    @Override
    public ModelAndView insertProcess(ModelAndView mv, RedirectAttributes ra) {
        final AdminRole data = new AdminRole(request);

        final Long idx = service.insertReturnKey(data);
        if(idx != null) {
            service.insertItems(idx, request.getParameterValues("item"));
            service.refresh();
            ra.addFlashAttribute("msg", "관리자 권한이 입력되었습니다.");
            mv.setViewName(redirect("/admin/role"));
        } else {
            ra.addFlashAttribute("err", "관리자 권한이 입력되지 않았습니다.");
            mv.setViewName(redirect("/admin/role/insert"));
        }
        return mv;
    }

    @Transactional
    @Override
    public ModelAndView updateProcess(ModelAndView mv, RedirectAttributes ra) {
        final AdminRole data = new AdminRole(request);

        final boolean result = service.update(data);
        if(result) {
            service.deleteItems(data.getIdx());
            service.insertItems(data.getIdx(), request.getParameterValues("item"));
            service.refresh();
            ra.addFlashAttribute("msg", "관리자 권한이 수정되었습니다.");
            mv.setViewName(redirect("/admin/role"));
        } else {
            ra.addFlashAttribute("err", "관리자 권한이 수정되지 않았습니다.");
            mv.setViewName(redirect("/admin/role/update?"+data.getIdx()));
        }
        return mv;
    }

    @Override
    public ModelAndView deleteProcess(ModelAndView mv, RedirectAttributes ra) {
        final String[] idx = request.getParameterValues("idx");
        final int result = service.deleteFromIdx(AdminRole.class, idx);
        if(result > 0) {
            service.refresh();
            ra.addFlashAttribute("msg", result+"개의 권한이 삭제되었습니다.");
        } else {
            ra.addFlashAttribute("err", "권한이 삭제되지 않았습니다.");
        }
        mv.setViewName(redirect("/admin/role"));
        return mv;
    }
}
