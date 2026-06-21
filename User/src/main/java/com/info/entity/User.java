package com.info.entity;


import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("t_user")
public class User {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    private String username;

    private String password;

    private String email;

    private String phone;

    private String real_name;

    private String gender;

    private String status;

    @TableField(fill = FieldFill.INSERT)
    private String create_time;

    @TableField(fill = FieldFill.UPDATE)
    private String update_time;

}
