package com.info.entity.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserLoginVO {
    private Integer id;
    private String username;
    private String realName;
    private String token;
    private long expireTime;
}
