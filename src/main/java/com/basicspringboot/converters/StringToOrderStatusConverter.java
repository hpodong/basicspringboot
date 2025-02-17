package com.basicspringboot.converters;

import com.basicspringboot.enums.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StringToOrderStatusConverter implements Converter<String, OrderStatus> {

    @Override
    public OrderStatus convert(String source) {
        return OrderStatus.fromValue(source);
    }
}
