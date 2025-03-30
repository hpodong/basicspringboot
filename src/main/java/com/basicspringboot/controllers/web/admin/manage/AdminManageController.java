package com.basicspringboot.controllers.web.admin.manage;

import com.basicspringboot.controllers.web.admin._BSAdminController;
import com.basicspringboot.dto.BSQuery;
import com.basicspringboot.enums.AdminStatus;
import com.basicspringboot.models.admin.Admin;
import com.basicspringboot.services.manage.AdminRoleService;
import com.basicspringboot.services.manage.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/admin/manage")
public class AdminManageController extends _BSAdminController {

    @Autowired
    private AdminService service;
    @Autowired
    private AdminRoleService adminRoleService;
    @Autowired
    public BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public String getPrefixPath() {
        return "manage";
    }

    @Override
    public ModelAndView index(ModelAndView mv) {

        final String status = request.getParameter("st");
        final String ar_idx = request.getParameter("ar");

        final BSQuery bsq = new BSQuery(Admin.class);
        bsq.addSelect("ar_name");
        bsq.addJoin("JOIN admin_role ON admin.ar_idx = admin_role.ar_idx AND admin_role.ar_can_show = 1");
        bsq.addType("id", "admin.a_id");
        bsq.addType("name", "admin.a_name");
        bsq.addType("cell", "admin.a_cell");
        bsq.addType("email", "admin.a_email");
        bsq.setFromParams(request);

        if(isNotEmpty(status)) bsq.addWhere("a_status = '"+status+"'");
        if(isNotEmpty(ar_idx)) bsq.addWhere("admin.ar_idx = "+ar_idx);

        bsq.setOrderBy("CASE WHEN a_status = 'waiting' THEN 1 WHEN a_status = 'active' THEN 2 ELSE 3 END");
        bsq.addOrderBy("ar_sort", "a_crdt DESC");
        mv.setViewName("admin/manage/index");
        mv.addObject("roles", adminRoleService.getRoles());

        mv.addObject("data", service.findAllListView(bsq, Admin::new));
        return mv;
    }

    @Override
    public ModelAndView view(Long idx, ModelAndView mv) {
        final BSQuery bq = new BSQuery(Admin.class);
        bq.setSelect("admin.*, ar_name");
        bq.addJoin("LEFT JOIN admin_role ON admin.ar_idx = admin_role.ar_idx");
        bq.setIdx(idx);

        final Admin data = service.findOne(bq, Admin::new);
        mv.addObject("data", data);
        mv.setViewName("admin/manage/view");
        return mv;
    }
    @Override
    public ModelAndView insert(ModelAndView mv) {
        mv.addObject("roles", adminRoleService.getShownRoles());
        mv.addObject("statuses", AdminStatus.values());
        mv.setViewName("admin/manage/insert");

        return mv;
    }

    @Override
    public ModelAndView update(Long idx, ModelAndView mv) {
        final BSQuery bq = new BSQuery(Admin.class);
        bq.setIdx(idx);

        mv.addObject("roles", adminRoleService.getShownRoles());

        final Admin data = service.findOne(bq, Admin::new);
        mv.addObject("data", data.toSetData());
        mv.addObject("statuses", AdminStatus.values());
        mv.setViewName("admin/manage/update");
        return mv;
    }

    @Override
    public ModelAndView insertProcess(ModelAndView mv, RedirectAttributes ra) {
        final Admin admin = new Admin(request);
        if(admin.getPassword() != null) admin.setPassword(bCryptPasswordEncoder.encode(admin.getPassword()));

        final boolean result = service.insert(admin);
        if(result) {
            ra.addFlashAttribute("msg", "관리자가 입력되었습니다.");
            mv.setViewName(redirect("/admin/manage"));
        } else {
            ra.addFlashAttribute("err", "관리자가 입력되지 않았습니다.");
            mv.setViewName(redirect("/admin/manage/insert"));
        }
        return mv;
    }

    @Override
    public ModelAndView updateProcess(ModelAndView mv, RedirectAttributes ra) {
        final Admin admin = new Admin(request);
        if(admin.getPassword() != null && !admin.getPassword().isBlank()) admin.setPassword(bCryptPasswordEncoder.encode(admin.getPassword()));
        else admin.setPassword(null);
        if(service.update(admin)) {
            ra.addFlashAttribute("msg", "관리자가 수정되었습니다.");
            mv.setViewName(redirect("/admin/manage"));
        } else {
            ra.addFlashAttribute("err", "관리자가 수정되지 않았습니다.");
            mv.setViewName(redirect("/admin/manage/update?"+admin.getIdx()));
        }
        return mv;
    }

    @Override
    public ModelAndView deleteProcess(ModelAndView mv, RedirectAttributes ra) {
        final String[] idx = request.getParameterValues("idx");
        final int result = service.deleteFromIdx(Admin.class, idx);
        if(result > 0) {
            ra.addFlashAttribute("msg", result+"개의 관리지가 삭제되었습니다.");
        } else {
            ra.addFlashAttribute("err", "관리지가 삭제되지 않았습니다.");
        }
        mv.setViewName(redirect("/admin/manage"));
        return mv;
    }

    @ResponseBody
    @PostMapping("/id-check")
    public boolean idCheck(@RequestParam String id, @RequestParam(required = false) String exclude) {
        final BSQuery bsq = new BSQuery(Admin.class);
        bsq.setWhere("a_id = '"+id+"'", "a_id != '"+exclude+"'");
        return service.findOne(bsq, Admin::new) == null;
    }
}
