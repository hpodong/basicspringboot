package com.travplan.services;

import com.travplan.models._Example;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Getter
public class _ExampleService extends _BSService<_Example>{

    public boolean insert(_Example data) {
        return super.insert(_Example.class, setter -> setter.putAll(data.toInput()));
    }

    public boolean update(_Example data) {
        return update(data, false);
    }

    public boolean update(_Example data, boolean nullable) {
        final String where = "WHERE ex_idx = "+data.getIdx();
        return super.update(_Example.class, where, setter -> setter.putAll(data.toInput(nullable)));
    }
}
