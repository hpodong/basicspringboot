package com.basicspringboot.controllers.api.app;

import com.basicspringboot.dto.BSQuery;
import com.basicspringboot.dto.ResponseDTO;
import com.basicspringboot.models.board.Agreement;
import com.basicspringboot.models.others.AppVersion;
import com.basicspringboot.services.contents.AgreementService;
import com.basicspringboot.services.contents.AppVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/api/app/version")
@RestController
public class AppVersionController extends _BSAPIController{

    @Autowired
    private AppVersionService service;
    @Autowired
    private AgreementService agreementService;

    @Transactional
    @PostMapping("/check")
    public ResponseEntity<ResponseDTO> check(
            @RequestParam(required = false) Timestamp agreement_updated_at
    ) {
        final AppVersion data = new AppVersion(request);
        return returnResponse(setter -> {
            AppVersion latestVersion = service.findLatestVersion(data.getOs());
            AppVersion requestVersion = service.findRequestVersion(data.getOs(), data.getBuild_number());

            synchronized (this) {
                if(latestVersion == null && service.insert(data)) {
                    latestVersion = service.findLatestVersion(data.getOs());
                    requestVersion = latestVersion;
                }
            }

            synchronized (this) {
                if(requestVersion == null && service.insert(data)) requestVersion = service.findRequestVersion(data.getOs(), data.getBuild_number());
            }

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

    @Override
    public ResponseEntity<ResponseDTO> view(@PathVariable Long idx) {
        return null;
    }

    @Override
    public ResponseEntity<ResponseDTO> insert() {
        return null;
    }

    @Override
    public ResponseEntity<ResponseDTO> update(@PathVariable Long idx) {
        return null;
    }

    @Override
    public ResponseEntity<ResponseDTO> delete(@PathVariable Long idx) {
        return null;
    }

    private List<Agreement> findAgreements(Timestamp updatedAt) {
        final List<Object> objs = new ArrayList<>();
        final BSQuery agreementBSQ = new BSQuery(Agreement.class);
        if(updatedAt != null) {
            agreementBSQ.addWhere("agm_updt > ?");
            objs.add(updatedAt);
        }
        return agreementService.findAll(agreementBSQ, Agreement::new, objs.toArray());
    }
}
