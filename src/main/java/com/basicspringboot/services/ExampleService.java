package com.basicspringboot.services;

import com.basicspringboot.dto.BSQuery;
import com.basicspringboot.interfaces.InsertSetter;
import com.basicspringboot.models.Example;
import com.basicspringboot.models.others.FileModel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@Getter
public class ExampleService extends _BSService<Example> {

    public void insertFiles(Long example_idx, List<FileModel> files) {
        final int size = files.size();

        insertMany("_example_file", size, new InsertSetter() {
            @Override
            public void columnSetter(List<String> columns) {
                columns.add("f_idx");
                columns.add("_idx");
                columns.add("_sort");
            }

            @Override
            public void valueSetter(int index, List<Object> values) {
                final FileModel file = files.get(index);
                values.add(file.getIdx());
                values.add(example_idx);
                values.add(index);
            }
        });
    }
}
