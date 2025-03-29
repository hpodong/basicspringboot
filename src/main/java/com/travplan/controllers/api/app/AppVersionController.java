package com.travplan.controllers.api.app;

import com.travplan.dto.BSQuery;
import com.travplan.dto.ResponseDTO;
import com.travplan.enums.AppVersionStatus;
import com.travplan.interfaces.ResponseSetter;
import com.travplan.models.board.Agreement;
import com.travplan.models.others.AppVersion;
import com.travplan.services.contents.AgreementService;
import com.travplan.services.contents.AppVersionService;
import com.travplan.services.member.MemberPushLogCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequestMapping("/api/app/version")
@RestController
public class AppVersionController extends _BSAPIController<AppVersion, AppVersionService>{

    @Autowired
    private AgreementService agreementService;
    @Autowired
    private MemberPushLogCategoryService pushLogCategoryService;

    protected AppVersionController(AppVersionService service) {
        super(AppVersion.class, service);
    }

    @PostMapping("/view/{idx}")
    public ResponseEntity<ResponseDTO> findByIdx(@PathVariable Long idx) {
        return super.findByIdx(idx, AppVersion::new);
    }

    @PostMapping("/check")
    public ResponseEntity<ResponseDTO> check(
            @RequestParam(required = false) Timestamp agreement_updated_at,
            @RequestParam(required = false) Timestamp member_push_log_category_updated_at
    ) {
        final AppVersion data = new AppVersion(request);
        return returnResponse(setter -> {
            AppVersion latestVersion = service.findLatestVersion(data.getOs());
            AppVersion requestVersion = service.findRequestVersion(data.getOs(), data.getBuild_number());

            if(latestVersion == null && service.insert(data)) {
                latestVersion = service.findLatestVersion(data.getOs());
                requestVersion = latestVersion;
                log.info("LAST : {}", latestVersion);
                log.info("REQ : {}", requestVersion);
            } else if(requestVersion == null && service.insert(data)) {
                requestVersion = service.findRequestVersion(data.getOs(), data.getBuild_number());
            }

            assert requestVersion != null;
            assert latestVersion != null;

            switch (requestVersion.getStatus()) {
                case AppVersionStatus.PASS:
                    setter.setStatusCode(HttpStatus.OK);
                    setter.setMessage("최신 버전입니다.");
                    setter.addMetadata("agreements", agreementService.getInitData(agreement_updated_at));
                    setter.addMetadata("push_log_categories", pushLogCategoryService.getInitData(member_push_log_category_updated_at));
                    break;
                case AppVersionStatus.UPDATE:
                    setter.setStatusCode(HttpStatus.UNAUTHORIZED);
                    setter.setMessage("업데이트가 필요합니다.");
                    break;
                case AppVersionStatus.PUSH:
                    setter.setStatusCode(HttpStatus.BAD_REQUEST);
                    setter.setMessage("새로운 업데이트가 있습니다.");
                    break;
                case AppVersionStatus.INSPECTION:
                    setter.setStatusCode(HttpStatus.UNAUTHORIZED);
                    setter.setMessage("점검중입니다.");
                    break;
            }
        });
    }
}
