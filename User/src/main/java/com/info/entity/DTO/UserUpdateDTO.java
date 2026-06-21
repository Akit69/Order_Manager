package com.info.entity.DTO;

import lombok.Data;

@Data
public class UserUpdateDTO {
    private String email;
    private String phone;
    private String realName;
    private String gender;
}
