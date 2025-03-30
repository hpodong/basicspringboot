package com.basicspringboot.filters.services.manage;

import com.basicspringboot.filters.models.logs.AdminConnectLog;
import com.basicspringboot.filters.services._BSService;
import org.springframework.stereotype.Service;

@Service
public class AdminConnectLogService extends _BSService<AdminConnectLog> {

    public boolean insert(AdminConnectLog data) {
        return super.insert(AdminConnectLog.class, setter -> {
            setter.putAll(data.toInput());
        });
    }
}
