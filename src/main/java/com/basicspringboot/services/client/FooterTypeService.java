package com.basicspringboot.services.client;

import com.basicspringboot.models.others.FooterType;
import com.basicspringboot.services._BSService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FooterTypeService extends _BSService<FooterType> {

    public boolean insert(FooterType data) {
        return super.insert(FooterType.class, setter -> setter.putAll(data.toInput()));
    }

    public boolean update(FooterType data) {
        final String where = "WHERE fot_idx = "+data.getIdx();
        return super.update(FooterType.class, where, setter -> setter.putAll(data.toInput()));
    }
}