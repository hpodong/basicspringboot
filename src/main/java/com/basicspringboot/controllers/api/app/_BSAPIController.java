package com.basicspringboot.controllers.api.app;

import com.basicspringboot.dto.ResponseDTO;
import com.basicspringboot.enums.JWTType;
import com.basicspringboot.exceptions.NotFoundTokenException;
import com.basicspringboot.interfaces.BSAPIControllerI;
import com.basicspringboot.interfaces.ResponseJwtSetter;
import com.basicspringboot.interfaces.ResponseSetter;
import com.basicspringboot.models.others.FileModel;
import com.basicspringboot.services.others.FileService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public abstract class _BSAPIController implements BSAPIControllerI {
    protected final JWTType jwtType = JWTType.MEMBER;
    protected Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected HttpSession session;
    @Autowired
    protected HttpServletRequest request;
    @Autowired
    protected HttpServletResponse response;
    @Value("${server.url}")
    protected String SERVER_URL;

    @Autowired
    private FileService fileService;

    protected ResponseEntity<ResponseDTO> returnResponse(ResponseSetter setter) {
        final ResponseDTO res = new ResponseDTO();
        setter.setResponse(res);
        return ResponseEntity.status(res.getStatusCode()).body(res);
    }

    protected ResponseEntity<ResponseDTO> returnResponse(JWTType type, boolean needToken, ResponseJwtSetter setter) {
        final ResponseDTO res = new ResponseDTO();
        Long memberIdx = (Long) request.getAttribute("member_idx");
        if(needToken && memberIdx == null) {
            throw new NotFoundTokenException();
        } else {
            setter.setResponse(res, memberIdx);
        }

        return ResponseEntity.status(res.getStatusCode()).body(res);
    }

    protected FileModel getFile(String folder, String parameter) {
        try {
            final Part part = request.getPart(parameter);
            if(part == null || part.getSize() <= 0) return null;
            return new FileModel(folder, request.getPart(parameter));
        } catch (IOException | ServletException e) {
            return null;
        }
    }

    protected List<FileModel> getFiles(String folder, String parameter) {
        final List<FileModel> files = new ArrayList<>();
        try {
            for(Part part : request.getParts()) {
                if(part != null) files.add(new FileModel(folder, part));
            }
        } catch (IOException | ServletException e) {
            throw new RuntimeException(e);
        }
        return files;
    }

    protected Long insertFile(FileModel file) {
        if(file == null) return null;
        return fileService.insert(file);
    }
}
