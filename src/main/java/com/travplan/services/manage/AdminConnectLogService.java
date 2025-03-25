package com.travplan.services.manage;

import com.travplan.models.logs.AdminConnectLog;
import com.travplan.services._BSService;
import org.springframework.stereotype.Service;

@Service
public class AdminConnectLogService extends _BSService<AdminConnectLog> {

    public boolean insert(AdminConnectLog data) {
        return super.insert(AdminConnectLog.class, setter -> {
            setter.putAll(data.toInput());
        });
    }
}
