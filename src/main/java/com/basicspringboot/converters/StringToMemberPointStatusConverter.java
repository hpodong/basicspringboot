package com.basicspringboot.converters;

import com.basicspringboot.enums.MemberPointStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StringToMemberPointStatusConverter implements Converter<String, MemberPointStatus> {

    @Override
    public MemberPointStatus convert(String source) {
        return MemberPointStatus.fromValue(source);
    }
}
