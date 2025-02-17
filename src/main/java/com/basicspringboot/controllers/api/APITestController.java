package com.basicspringboot.controllers.api;

import com.basicspringboot.controllers.api.app._BSAPIController;
import com.basicspringboot.dto.ResponseDTO;
import com.basicspringboot.exceptions.APIException;
import com.basicspringboot.services.api.FirebaseService;
import com.basicspringboot.services.api.KakaoService;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/test")
public class APITestController extends _BSAPIController {

    @Autowired
    private FirebaseService firebaseService;
    @Autowired
    private KakaoService kakaoService;

    @Override
    public ResponseEntity<ResponseDTO> view(Long idx) {
        return null;
    }

    @Override
    public ResponseEntity<ResponseDTO> insert() {
        return null;
    }

    @Override
    public ResponseEntity<ResponseDTO> update(Long idx) {
        return null;
    }

    @Override
    public ResponseEntity<ResponseDTO> delete(Long idx) {
        return null;
    }

    /*@PostMapping("/order")
    @Transactional
    public ResponseEntity<ResponseDTO> order() {
        return returnResponse(setter -> {
            ProductOrder order = new ProductOrder(request);
            final BSQuery bsq = new BSQuery(ProductOrder.class);
            final Long idx = orderService.insertReturnKey(order);
            if(idx == null) throw new InsertException();
            bsq.setIdx(idx);
            order = orderService.findOne(bsq, ProductOrder::new);

            setter.setData(order);
            setter.setStatusCode(HttpStatus.CREATED);
            setter.setMessage("주문이 등록되었습니다.");
        });
    }*/

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
