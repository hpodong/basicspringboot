package com.basicspringboot.controllers.web.admin;
import com.basicspringboot.dto.BSQuery;
import com.basicspringboot.models.admin.AdminPushLog;
import com.basicspringboot.models.others.FileModel;
import com.basicspringboot.services.manage.AdminPushLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController extends _BSAdminController {

    private static final String EDITOR_UPLOAD_DIR = "/editor";

    private final AdminPushLogService adminPushLogService;

    @GetMapping("/login")
    public ModelAndView loginView(ModelAndView mv, @CookieValue(value = "save_id", required = false) String save_id) {
        mv.addObject("save_id", save_id);
        mv.addObject("url", session.getAttribute("url"));
        mv.setViewName("admin/login");
        return mv;
    }

    @Override
    public String getPrefixPath() {
        return "admin";
    }

    @Override
    public ModelAndView index(ModelAndView mv) {
        mv.setViewName(redirect("/admin/dashboard"));
        return mv;
    }

    @GetMapping("/dashboard")
    public ModelAndView dashboard(ModelAndView mv) {
        return super.index(mv);
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
