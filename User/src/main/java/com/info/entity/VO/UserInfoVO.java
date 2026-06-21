package com.info.entity.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserInfoVO {
    private Integer id;
    private String username;
    private String email;
    private String phone;
    private String realName;
    private String gender;
    private String status;
}