package com.basicspringboot.filters.services.visit;

import com.basicspringboot.filters.models.logs.InflowLog;
import com.basicspringboot.filters.services._BSService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Getter
public class InflowLogService extends _BSService<InflowLog> {

    public boolean insert(InflowLog data) {
        return super.insert(InflowLog.class, setter -> setter.putAll(data.toInput()));
    }
}
