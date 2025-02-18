package com.basicspringboot.controllers.web.admin;
import com.basicspringboot.dto.BSQuery;
import com.basicspringboot.models.admin.Admin;
import com.basicspringboot.models.admin.AdminPushLog;
import com.basicspringboot.models.logs.AdminConnectLog;
import com.basicspringboot.models.others.FileModel;
import com.basicspringboot.services.manage.AdminConnectLogService;
import com.basicspringboot.services.manage.AdminPushLogService;
import com.basicspringboot.services.manage.AdminService;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController extends _BSAdminController {

    private static final String EDITOR_UPLOAD_DIR = "/editor";
    private static final String ADMIN_LOGGED_SESSION_KEY = "admin";

    private final AdminService service;
    private final AdminConnectLogService adminConnectLogService;
    private final AdminPushLogService adminPushLogService;
    public final BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/login")
    public ModelAndView loginView(ModelAndView mv, @CookieValue(value = "save_id", required = false) String save_id) {
        mv.addObject("save_id", save_id);
        mv.addObject("url", session.getAttribute("url"));
        mv.setViewName("admin/login");
        return mv;
    }

    @Transactional
    @PostMapping("/login")
    public String login(RedirectAttributes ra, @RequestParam String id, @RequestParam String password, @RequestParam String url, @RequestParam(required = false) Boolean save_id) {
        final BSQuery bq = new BSQuery(Admin.class);
        bq.addWhere("a_id = '"+id+"'");

        final Cookie cookie = new Cookie("save_id", id);

        if(save_id == null) cookie.setMaxAge(0);

        response.addCookie(cookie);

        final Admin admin = service.findOne(bq, Admin::new);
        if(admin != null) {
            if(bCryptPasswordEncoder.matches(password, admin.getPassword())) {
                session.setAttribute(ADMIN_LOGGED_SESSION_KEY, admin);
                final AdminConnectLog adminConnectLog = new AdminConnectLog();
                adminConnectLog.setAdmin_idx(admin.getIdx());
                adminConnectLog.setRemote_ip(request.getRemoteAddr());
                adminConnectLogService.insert(adminConnectLog);
                if(!url.isBlank()) return redirect(url);
                return redirect("/admin");
            } else {
                ra.addFlashAttribute("err", "비밀번호가 일치하지 않습니다.");
                return redirect("/admin/login");
            }
        } else {
            ra.addFlashAttribute("url", url);
            ra.addFlashAttribute("err", "존재하지 않는 관리자입니다.");
            return redirect("/admin/login");
        }
    }

    @ResponseBody
    @PostMapping("/logout")
    public boolean logout() {
        session.setAttribute(ADMIN_LOGGED_SESSION_KEY, null);
        return true;
    }

    @GetMapping("")
    public ModelAndView index(ModelAndView mv) {
        final Admin admin = getLoggedAdmin();
        if(admin == null) {
            mv.setViewName(redirect("/admin"));
        } else {
            mv.setViewName(redirect("/admin/dashboard"));
        }
        return mv;
    }

    @GetMapping("/dashboard")
    public ModelAndView dashboard(ModelAndView mv) {
        mv.setViewName("admin/index");
        return mv;
    }

    @ResponseBody
    @PostMapping("/editor/upload")
    public String uploadEditorFile() {
        final FileModel file = getFile(EDITOR_UPLOAD_DIR, "file");
        if(file.create()) {
            return file.getUrl();
        } else {
            return null;
        }
    }

    /*
    @ResponseBody
    @PostMapping("/recent/push-logs")
    public List<AdminPushLog> getRecentPushLogs(@RequestParam String type) {
        final BSQuery bsq = new BSQuery(AdminPushLog.class, request);
        if(hasParameter(type)) bsq.addWhere("apl_type = '"+type+"'");
        bsq.setOrderBy("apl_is_read", "apl_crdt DESC");
        return adminPushLogService.findAll(bsq, AdminPushLog::new);
    }
    */
    @ResponseBody
    @PostMapping("/recent/push-logs")
    public Map<String, Object> getRecentPushLogs(@RequestParam String type) {
        final BSQuery bsq = new BSQuery(AdminPushLog.class, request);
        bsq.addWhere("DATE(apl_crdt) BETWEEN DATE_SUB(CURDATE(), INTERVAL 30 DAY) AND CURDATE()");
        if (hasParameter(type)) bsq.addWhere("apl_type = '"+type+"'");
        bsq.setOrderBy("apl_is_read", "apl_crdt DESC");

        // 로그 결과 조회(리스트)
        List<AdminPushLog> logs = adminPushLogService.findAll(bsq, AdminPushLog::new);

        // 카운트 조회
        Map<String, Long> pushLogsCount = adminPushLogService.getPushLogsCount();

        // 결과를 하나의 Map으로 합침
        Map<String, Object> response = new HashMap<>();
        response.put("list", logs);
        response.put("count", pushLogsCount);

        return response;
    }

    @ResponseBody
    @PostMapping("/recent/push-logs/read")
    public boolean readPushLogs(@RequestParam Long idx) {
        final AdminPushLog data = new AdminPushLog();
        data.setIdx(idx);
        data.setIs_read(true);
        return adminPushLogService.update(data);
    }
}
