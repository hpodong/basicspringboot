package com.basicspringboot.services.member;

import com.basicspringboot.interfaces.InsertSetter;
import com.basicspringboot.models.Example;
import com.basicspringboot.models.member.TestModel;
import com.basicspringboot.models.others.FileModel;
import com.basicspringboot.services._BSService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@Getter
public class TestService extends _BSService<TestModel> {

}
