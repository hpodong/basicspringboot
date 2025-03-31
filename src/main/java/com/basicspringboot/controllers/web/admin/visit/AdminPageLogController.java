package com.basicspringboot.controllers.web.admin.visit;

import com.basicspringboot.controllers.web.admin.BSAdminController;
import com.basicspringboot.dto.BSQuery;
import com.basicspringboot.models.others.ClientPageInflowLog;
import com.basicspringboot.services.client.ClientPageInflowLogService;
import com.basicspringboot.services.client.ClientPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/log/page-history")
public class AdminPageLogController extends BSAdminController {

    private final ClientPageService service;
    private final ClientPageInflowLogService clientPageInflowLogService;

    @Override
    public String getPrefixPath() {
        return "log/page";
    }

    @Override
    public ModelAndView index(ModelAndView mv) {
        final BSQuery bsq = getBSQ();

        final long total_count = service.findAllVisitedCount(bsq);
        final long row_count = service.findAllRowCount(bsq);

        mv.addObject("data", clientPageInflowLogService.findAllListView(bsq, row_count, ClientPageInflowLog::new));
        mv.addObject("total_count", total_count);
        mv.addObject("charts", service.findCharts(bsq.getWhere()));
        mv.setViewName("admin/log/page/index");
        return mv;
    }

    @ResponseBody
    @GetMapping("/download/excel")
    public void excelDownload() throws IOException {
        clientPageInflowLogService.downloadExcel(getBSQ());
    }

    private BSQuery getBSQ() {
        final BSQuery bsq = new BSQuery(ClientPageInflowLog.class, request);
        bsq.addSelect("cp_title");
        bsq.addQuerySelect();

        bsq.addJoin("JOIN client_page ON client_page.cp_idx = client_page_inflow_log.cp_idx");

        bsq.setOrderBy("count DESC");
        bsq.addGroupBy("client_page_inflow_log.cp_idx");
        return bsq;
    }
}
