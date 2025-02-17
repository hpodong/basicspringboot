package com.basicspringboot.services.manage;

import com.basicspringboot.models.admin.Admin;
import com.basicspringboot.services._BSService;
import org.springframework.stereotype.Service;

@Service
public class AdminService extends _BSService<Admin> {

    public boolean insert(Admin admin) {
        return super.insert(Admin.class, setter -> setter.putAll(admin.toInput()));
    }

    public boolean update(Admin admin) {
        final String where = "WHERE a_idx = "+admin.getIdx();
        return super.update(Admin.class, where, setter -> setter.putAll(admin.toInput()));
    }
}