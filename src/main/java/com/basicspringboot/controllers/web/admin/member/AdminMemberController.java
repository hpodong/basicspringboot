package com.basicspringboot.controllers.web.admin.member;

import com.basicspringboot.controllers.web.admin._BSAdminController;
import com.basicspringboot.dto.BSQuery;
import com.basicspringboot.enums.AdminStatus;
import com.basicspringboot.enums.MemberStatus;
import com.basicspringboot.models.member.Member;
import com.basicspringboot.services.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/admin/member")
@RequiredArgsConstructor
public class AdminMemberController extends _BSAdminController {

    private final MemberService service;
    public final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public ModelAndView index(ModelAndView mv) {

        mv.addObject("data", service.findAllListView(getBSQ(), Member::new));

        mv.setViewName("admin/member/index");

        return mv;
    }

    @Override
    public ModelAndView view(Long idx, ModelAndView mv) {
        mv.addObject("data", getDataFromIdx(idx));
        mv.setViewName("admin/member/view");
        return mv;
    }

    @Override
    public ModelAndView update(Long idx, ModelAndView mv) {
        mv.addObject("data", getDataFromIdx(idx).toSetData());
        mv.addObject("statuses", MemberStatus.values());
        mv.setViewName("admin/member/update");
        return mv;
    }

    @Override
    public ModelAndView insertProcess(ModelAndView mv, RedirectAttributes ra) {
        final Member member = new Member(request);
        if(member.getPassword() != null && !member.getPassword().isBlank()) member.setPassword(bCryptPasswordEncoder.encode(member.getPassword()));
        if(service.insert(member)) {
            ra.addFlashAttribute("msg", "회원이 입력되었습니다.");
            mv.setViewName(redirect("/admin/member"));
        } else {
            ra.addFlashAttribute("err", "회원이 입력되지 않았습니다.");
            mv.setViewName(redirect("/admin/member/insert"));
        }
        return mv;
    }

    @Override
    public ModelAndView updateProcess(ModelAndView mv, RedirectAttributes ra) {
        final Member member = new Member(request);
        if(member.getPassword() != null && !member.getPassword().isBlank()) member.setPassword(bCryptPasswordEncoder.encode(member.getPassword()));
        else member.setPassword(null);
        if(service.update(member)) {
            ra.addFlashAttribute("msg", "회원이 수정되었습니다.");
            mv.setViewName(redirect("/admin/member"));
        } else {
            ra.addFlashAttribute("err", "회원이 수정되지 않았습니다.");
            mv.setViewName(redirect("/admin/member/update?"+member.getIdx()));
        }
        return mv;
    }

    @Override
    public ModelAndView deleteProcess(ModelAndView mv, RedirectAttributes ra) {
        final String[] idx = request.getParameterValues("idx");
        final int result = service.deleteFromIdx(Member.class, idx);
        if(result > 0) {
            ra.addFlashAttribute("msg", result+"개의 회원이 삭제되었습니다.");
        } else {
            ra.addFlashAttribute("err", "회원이 삭제되지 않았습니다.");
        }
        mv.setViewName(redirect("/admin/delete/member"));
        return mv;
    }

    @ResponseBody
    @PostMapping("/id-check")
    public boolean idCheck(@RequestParam String id, @RequestParam(required = false) String exclude) {
        final BSQuery bsq = new BSQuery(Member.class);
        bsq.setWhere("m_id = '"+id+"'", "m_id != '"+exclude+"'");
        return service.findOne(bsq, Member::new) == null;
    }

    @ResponseBody
    @PostMapping("/email-check")
    public boolean emailCheck(@RequestParam String email, @RequestParam(required = false) String exclude) {
        final BSQuery bsq = new BSQuery(Member.class);
        bsq.setWhere("m_email = '"+email+"'", "m_email != '"+exclude+"'");
        return service.findOne(bsq, Member::new) == null;
    }

    @ResponseBody
    @PostMapping("/cell-check")
    public boolean cellCheck(@RequestParam String cell, @RequestParam(required = false) String exclude) {
        final BSQuery bsq = new BSQuery(Member.class);
        bsq.setWhere("m_cell = '"+cell+"'", "m_cell != '"+exclude+"'");
        return service.findOne(bsq, Member::new) == null;
    }

    @ResponseBody
    @PostMapping("/leaved/{idx}")
    public boolean updateStatusLeaved(@PathVariable Long idx) {
        return service.updateStatusLeavedFromMemberIdx(idx);
    }

    private Member getDataFromIdx(Long idx) {
        final BSQuery bsq = new BSQuery(Member.class);
        bsq.addSelect(service.socialsToSql());
        bsq.setIdx(idx);
        return service.findOne(bsq, Member::new);
    }

    @ResponseBody
    @GetMapping("/download/excel")
    public void excelDownload() throws IOException {
        service.downloadExcel(getBSQ());
    }

    private BSQuery getBSQ() {
        final BSQuery bsq = new BSQuery(Member.class);
        bsq.setSelect("member.*");
        bsq.addType("id", "m_id");
        bsq.addType("name", "m_name");
        bsq.addType("cell", "m_cell");
        bsq.setFromParams(request);
        bsq.addWhere("m_dldt IS NULL");
        return bsq;
    }
}
