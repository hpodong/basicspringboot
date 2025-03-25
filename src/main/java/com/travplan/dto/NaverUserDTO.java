package com.travplan.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class NaverUserDTO {
    private String id;
    private String gender;
    private String email;
    private String mobile;
    private String mobile_e164;
    private String name;
    private String birthday;
    private Integer birthyear;
}
