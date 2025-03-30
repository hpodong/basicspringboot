package com.basicspringboot.utils;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;

@NoArgsConstructor
public class Upload {
    @Value("${upload.path}")
    private String UPLOAD_PATH;
    public String path(String folder, String name) {
        return UPLOAD_PATH + File.separator + folder + File.separator + name;
    }
    public String url(String folder, String name) {
        return UPLOAD_PATH + File.separator + folder + File.separator + name;
    }
}
