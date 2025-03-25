package com.travplan.controllers.api.app;

import com.travplan.dto.BSQuery;
import com.travplan.dto.ResponseDTO;
import com.travplan.models.board.Agreement;
import com.travplan.models.others.AppVersion;
import com.travplan.services.contents.AgreementService;
import com.travplan.services.contents.AppVersionService;
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

@RequestMapping("/api/app/version")
@RestController
public class AppVersionController extends _BSAPIController<AppVersion, AppVersionService>{

    @Autowired
    private AgreementService agreementService;

    protected AppVersionController(AppVersionService service) {
        super(AppVersion.class, service);
    }

    @PostMapping("/view/{idx}")
    public ResponseEntity<ResponseDTO> findByIdx(@PathVariable Long idx) {
        return super.findByIdx(idx, AppVersion::new);
    }

    @Transactional
    @PostMapping("/check")
    public ResponseEntity<ResponseDTO> check(
            @RequestParam(required = false) Timestamp agreement_updated_at
    ) {
        final AppVersion data = new AppVersion(request);
        return returnResponse(setter -> {
            AppVersion latestVersion = service.findLatestVersion(data.getOs());
            AppVersion requestVersion = service.findRequestVersion(data.getOs(), data.getBuild_number());

            if(latestVersion == null && service.insert(data)) {
                latestVersion = service.findLatestVersion(data.getOs());
                requestVersion = latestVersion;
            }

            if(requestVersion == null && service.insert(data)) requestVersion = service.findRequestVersion(data.getOs(), data.getBuild_number());

            assert requestVersion != null;
            assert latestVersion != null;

            if(requestVersion.isHigh(latestVersion) || requestVersion.isSame(latestVersion)) {
                setter.setStatusCode(HttpStatus.OK);
                setter.setMessage("최신 버전입니다.");
            } else if(latestVersion.isHigh(requestVersion)) {
                switch (latestVersion.getStatus()) {
                    case "pass":
                        setter.setStatusCode(HttpStatus.OK);
                        setter.setMessage("최신 버전입니다.");
                        setter.addMetadata("agreements", findAgreements(agreement_updated_at));
                        break;
                    case "update":
                        setter.setStatusCode(HttpStatus.UNAUTHORIZED);
                        setter.setMessage("업데이트가 필요합니다.");
                        break;
                    case "push":
                        setter.setStatusCode(HttpStatus.BAD_REQUEST);
                        setter.setMessage("새로운 업데이트가 있습니다.");
                        break;
                    case "inspection":
                        setter.setStatusCode(HttpStatus.UNAUTHORIZED);
                        setter.setMessage("점검중입니다.");
                        break;
                }
            } else {
                setter.setStatusCode(HttpStatus.BAD_REQUEST);
                setter.setMessage("알 수 없는 오류가 발생했습니다.");
            }
        });
    }

    private List<Agreement> findAgreements(Timestamp updatedAt) {
        final List<Object> objs = new ArrayList<>();
        final BSQuery<Agreement> agreementBSQ = new BSQuery<>(Agreement.class);
        if(updatedAt != null) {
            agreementBSQ.addWhere("agm_updt > ?");
            objs.add(updatedAt);
        }
        agreementBSQ.addArgs(objs.toArray());
        return agreementService.findAll(agreementBSQ, Agreement::new);
    }
}
