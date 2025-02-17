package com.basicspringboot.converters;

import com.basicspringboot.enums.APStatus;
import com.basicspringboot.enums.OrderTradingStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StringToOrderTradingStatusConverter implements Converter<String, OrderTradingStatus> {

    @Override
    public OrderTradingStatus convert(String source) {
        return OrderTradingStatus.fromValue(source);
    }
}
