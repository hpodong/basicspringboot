package com.basicspringboot.services.client;

import com.basicspringboot.models.others.HeaderType;
import com.basicspringboot.services._BSService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HeaderTypeService extends _BSService<HeaderType> {

    public boolean insert(HeaderType data) {
        return super.insert(HeaderType.class, setter -> setter.putAll(data.toInput()));
    }

    public boolean update(HeaderType data) {
        final String where = "WHERE ht_idx = "+data.getIdx();
        return super.update(HeaderType.class, where, setter -> setter.putAll(data.toInput()));
    }
}