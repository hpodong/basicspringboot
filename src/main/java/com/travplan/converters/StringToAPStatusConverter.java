package com.travplan.converters;

import com.travplan.enums.APStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StringToAPStatusConverter implements Converter<String, APStatus> {

    @Override
    public APStatus convert(String source) {
        return APStatus.fromValue(source);
    }
}
