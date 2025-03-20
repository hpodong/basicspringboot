package com.basicspringboot.services.others;

import com.basicspringboot.dto.BSQuery;
import com.basicspringboot.models.others.FileModel;
import com.basicspringboot.services._BSService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Getter
public class FileService extends _BSService<FileModel> {

    public Long insert(FileModel data) {
        return super.insertReturnKey(FileModel.class, setter -> setter.putAll(data.toInput()));
    }

    @Transactional
    public List<FileModel> insertMany(List<FileModel> files) {
        final String[] cols = new String[]{
                "f_folder",
                "f_name",
                "f_enc_name",
                "f_url",
                "f_size",
                "f_type"
        };
        final List<Object> values = new ArrayList<>();

        for(FileModel file : files) {
            values.add(file.getFolder());
            values.add(file.getName());
            values.add(file.getEnc_name());
            values.add(file.getUrl());
            values.add(file.getSize());
            values.add(file.getType());
        }

        final boolean result = insertMany(FileModel.class, cols, values.toArray()) > 0;
        if(result) {
            final BSQuery bsq = new BSQuery(FileModel.class);
            bsq.setOrderBy("f_idx DESC");
            bsq.setLimit(files.size());
            return findAll(bsq, FileModel::new);
        } else {
            return null;
        }
    }

    public FileModel findByIdx(Long idx) {
        final BSQuery bsq = new BSQuery(FileModel.class);
        bsq.setIdx(idx);
        return findOne(bsq, FileModel::new);
    }
}
