package com.info.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.info.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
