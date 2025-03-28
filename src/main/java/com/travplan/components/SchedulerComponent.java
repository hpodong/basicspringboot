package com.travplan.components;

import com.travplan.services.client.ClientPageService;
import com.travplan.services.visit.PageLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SchedulerComponent {

    private final PageLogService pageLogService;
    private final ClientPageService clientPageService;

    @Scheduled(cron = "0 * * * * ?")
    public void everyMinute() {
        pageLogService.insert();
        clientPageService.insertClientPageInflowLogs();
    }
}
