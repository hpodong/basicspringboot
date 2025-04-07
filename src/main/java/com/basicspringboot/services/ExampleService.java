package com.basicspringboot.services;

import com.basicspringboot.interfaces.InsertSetter;
import com.basicspringboot.models.Example;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@Getter
public class ExampleService extends _BSService<Example> {

}
