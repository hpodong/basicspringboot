package com.basicspringboot.converters;

import com.basicspringboot.enums.OrderCancelStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StringToOrderProductStatusConverter implements Converter<String, OrderCancelStatus> {

    @Override
    public OrderCancelStatus convert(String source) {
        return OrderCancelStatus.fromValue(source);
    }
}
