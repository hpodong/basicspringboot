package com.basicspringboot.converters;

import com.basicspringboot.enums.ProductReviewStatus;
import com.basicspringboot.enums.ProductStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StringToProductReviewStatusConverter implements Converter<String, ProductReviewStatus> {

    @Override
    public ProductReviewStatus convert(String source) {
        return ProductReviewStatus.fromValue(source);
    }
}
