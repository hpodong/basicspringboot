package com.basicspringboot.filters.controllers.web.admin.member;

import com.basicspringboot.filters.controllers.web.admin._BSAdminController;
import com.basicspringboot.filters.dto.BSQuery;
import com.basicspringboot.filters.models.member.Member;
import com.basicspringboot.filters.services.member.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/delete/member")
public class AdminDeleteMemberController extends _BSAdminController {

    @Autowired
    private MemberService service;

    @Override
    public String getPrefixPath() {
        return "";
    }

    @Override
    public ModelAndView index(ModelAndView mv) {
        final BSQuery bsq = new BSQuery(Member.class, request);

        bsq.addWhere("m_status = 'leaved'");

        mv.addObject("data", service.findAllListView(bsq, Member::new));
        mv.setViewName("admin/member/delete");
        return mv;
    }

    @Override
    public ModelAndView deleteProcess(ModelAndView mv, RedirectAttributes ra) {
        final String[] idx = request.getParameterValues("idx");
        final int result = service.deleteFromIdx(Member.class, idx);
        if(result > 0) {
            ra.addFlashAttribute("msg", result + "명의 회원이 삭제되었습니다.");
        } else {
            ra.addFlashAttribute("err", "회원이 삭제되지 않았습니다.");
        }
        mv.setViewName(redirect("/admin/delete/member"));
        return mv;
    }
}
