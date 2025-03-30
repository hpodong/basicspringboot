package com.basicspringboot.filters.services.others;

import com.basicspringboot.filters.dto.BSQuery;
import com.basicspringboot.filters.models.others.FcmToken;
import com.basicspringboot.filters.services._BSService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Getter
public class FcmTokenService extends _BSService<FcmToken> {

    public boolean insert(FcmToken data) {
        return super.insert(FcmToken.class, setter -> setter.putAll(data.toInput()));
    }

    @Transactional
    public boolean login(Long member_idx, String fcmToken, String deviceId) {
        final BSQuery bsq = new BSQuery(FcmToken.class);
        bsq.setWhere("ft_device_id = ?");
        bsq.addArgs(deviceId);
        FcmToken data = findOne(bsq, FcmToken::new);
        if(data == null) data = new FcmToken();
        data.setDevice_id(deviceId);
        data.setMember_idx(member_idx);
        data.setToken(fcmToken);

        if(data.getIdx() != null) {
            return update(data);
        } else {
            return insert(data);
        }
    }

    public void logout(String deviceId) {
        final String sql = "UPDATE fcm_token SET m_idx = NULL WHERE ft_device_id = ?";
        master.update(sql, deviceId);
    }

    public boolean update(FcmToken data) {
        return update(data, false);
    }

    public boolean update(FcmToken data, boolean nullable) {
        final String where = "WHERE ft_idx = "+data.getIdx();
        return super.update(FcmToken.class, where, setter -> setter.putAll(data.toInput(nullable)));
    }
}
