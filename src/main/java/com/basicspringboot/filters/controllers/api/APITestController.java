package com.basicspringboot.filters.controllers.api;/*
package com.travplan.controllers.api;

import com.travplan.controllers.api.app._BSAPIController;
import com.travplan.dto.ResponseDTO;
import com.travplan.exceptions.APIException;
import com.travplan.models._Example;
import com.travplan.services.api.FirebaseService;
import com.travplan.services.api.KakaoService;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/test")
public class APITestController extends _BSAPIController<_Example> {

    @Autowired
    private FirebaseService firebaseService;
    @Autowired
    private KakaoService kakaoService;

    protected APITestController(Class<_Example> baseClass) {
        super(baseClass);
    }

    @PostMapping("/push_notification")
    public ResponseEntity<ResponseDTO> pushNotification() {
        return returnResponse(setter -> {
            try {
                final Response response = firebaseService.pushNotification("en2gTpPhRlWQEzD9im1O9N:APA91bFkJT5z6BHIfbCt7sKyQLiY0dSsGb5FJX8uzLQTE6WEYSEvhAtpyt2V-NtR6Ry6z5X1PNUR8qWYxq9TfZDDoPLScHMX06LyO6n1vYhInFDNhRmBq0WtUAkQIUbXQt-4D0J_22uM", "test", "test");
                if(response.isSuccessful()) {
                    setter.setStatusCode(HttpStatus.OK);
                    setter.setMessage("푸시 알림을 성공적으로 발송했습니다.");
                } else {
                    throw new APIException("푸시 알림 발송에 실패했습니다.");
                }
            } catch (IOException e) {
                throw new APIException("푸시 알림 발송에 실패했습니다.");
            }
        });
    }

    @PostMapping("/kakao/aligo")
    public ResponseEntity<ResponseDTO> aligo() {
        return returnResponse(setter -> {
            final Response response = kakaoService.sendCertificateNumber("010-8474-4471");
            if(response.isSuccessful()) {
                setter.setStatusCode(HttpStatus.OK);
                setter.setMessage("알리고 전송에 성공했습니다.");
            } else {
                throw new APIException("알리고 전송에 실패했습니다.");
            }
        });
    }

}
*/
