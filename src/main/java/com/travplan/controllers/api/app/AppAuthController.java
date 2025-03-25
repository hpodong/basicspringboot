package com.travplan.controllers.api.app;

import com.travplan.dto.ResponseDTO;
import com.travplan.dto.SignupDTO;
import com.travplan.exceptions.APIException;
import com.travplan.exceptions.InsertException;
import com.travplan.models.member.Member;
import com.travplan.models.member.MemberSocial;
import com.travplan.services.api.AligoService;
import com.travplan.services.member.MemberService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RequestMapping("/api/app/auth")
@Slf4j
@RestController
public class AppAuthController extends _BSAPIController<Member, MemberService>{

    private final AligoService aligoService;

    protected AppAuthController(AligoService aligoService, MemberService service) {
        super(Member.class, service);
        this.aligoService = aligoService;
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(String email, String password) throws APIException {
        return returnResponse(setter -> {
            setter.setData(service.login(email, password));
            setter.setStatusCode(HttpStatus.OK);
        });
    }

    @PostMapping("/oauth/login")
    public ResponseEntity<ResponseDTO> login(@RequestBody MemberSocial social) {
        return returnResponse(setter -> {
            setter.setData(service.login(social));
            setter.setStatusCode(HttpStatus.OK);
        });
    }

    @Transactional
    @PostMapping("/signup")
    public ResponseEntity<ResponseDTO> signup(@RequestBody SignupDTO req) throws InsertException {
        return returnResponse(setter -> {
            setter.setStatusCode(HttpStatus.CREATED);
            setter.setData(service.signup(req));
        });
    }

    @PostMapping("/send/auth-number")
    public ResponseEntity<ResponseDTO> sendAuthMessage(String cell) throws IOException {
        final Response response = aligoService.sendAuthNumber(cell);
        return returnResponse(setter -> {
            if(response.isSuccessful()) {
                setter.setStatusCode(HttpStatus.OK);
                setter.setMessage("인증번호가 전송되었습니다");
            } else {
                throw new APIException("전화번호를 다시 입력해주세요");
            }
        });
    }

    @PostMapping("/verify/auth-number")
    public ResponseEntity<ResponseDTO> verifyAuthMessage(String cell, String authNumber) {
        return returnResponse(setter -> {
            aligoService.verifyAuthNumber(cell, authNumber);
            setter.setStatusCode(HttpStatus.OK);
            setter.setMessage("인증이 완료되었습니다.");
        });
    }

    @PostMapping("/reissue-tk")
    public ResponseEntity<ResponseDTO> reissueToken(String refreshToken) {
        return returnResponse(setter -> {
            setter.setStatusCode(HttpStatus.OK);
            final Map<String, Object> data = service.reissueToken(refreshToken);
            setter.setData(data);
        });
    }
}
