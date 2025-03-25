package com.travplan.components;

import com.travplan.services.contents.PopupService;
import com.travplan.services.application.ConsultService;
import com.travplan.services.board.FAQService;
import com.travplan.services.contents.AgreementService;
import com.travplan.services.contents.EventBannerService;
import com.travplan.services.contents.MainVisualService;
import com.travplan.services.manage.AdminMenuService;
import com.travplan.services.manage.AdminRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BasicComponent implements ApplicationRunner {

    private final AdminRoleService adminRoleService;
    private final AgreementService agreementService;
    private final AdminMenuService adminMenuService;
    private final FAQService faqService;
    private final ConsultService consultService;
    private final MainVisualService mainVisualService;
    private final EventBannerService eventBannerService;
    private final PopupService popupService;

    @Override
    public void run(ApplicationArguments args) {
        adminRoleService.refresh();
        adminMenuService.refreshMenus();
        faqService.refreshCategories();
        agreementService.refreshCategories();
        consultService.refreshCategories();
        mainVisualService.refresh();
        eventBannerService.refresh();
        popupService.refresh();
    }
}
