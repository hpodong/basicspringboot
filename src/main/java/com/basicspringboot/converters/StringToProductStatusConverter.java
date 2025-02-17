package com.basicspringboot.converters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import com.basicspringboot.enums.ProductStatus;

@Component
@Slf4j
public class StringToProductStatusConverter implements Converter<String, ProductStatus> {

    @Override
    public ProductStatus convert(String source) {
        return ProductStatus.fromValue(source);
    }
}
