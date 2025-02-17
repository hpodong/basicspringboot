package com.basicspringboot.interfaces;

import com.basicspringboot.dto.ResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public interface BSAPIControllerI {

    @PostMapping("/view/{idx}")
    ResponseEntity<ResponseDTO> view(@PathVariable Long idx);

    @PostMapping("/insert")
    ResponseEntity<ResponseDTO> insert();

    @PostMapping("/update/{idx}")
    ResponseEntity<ResponseDTO> update(@PathVariable Long idx);

    @PostMapping("/delete/{idx}")
    ResponseEntity<ResponseDTO> delete(@PathVariable Long idx);
}
