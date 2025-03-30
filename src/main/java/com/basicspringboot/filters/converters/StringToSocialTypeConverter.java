package com.basicspringboot.filters.converters;

import com.basicspringboot.filters.enums.SocialType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StringToSocialTypeConverter implements Converter<String, SocialType> {

    @Override
    public SocialType convert(String source) {
        return SocialType.fromValue(source);
    }
}
