package com.info.service;

import com.info.entity.DTO.UserLoginDTO;
import com.info.entity.DTO.UserUpdateDTO;
import com.info.entity.Result;
import com.info.entity.VO.UserLoginVO;

public interface UserService {

    Result<UserLoginVO> login(UserLoginDTO userLoginDTO);

    Result<String> register(UserLoginDTO userLoginDTO);

    Result<UserLoginVO> me(Integer userId);

    Result<String> update(UserUpdateDTO dto);

}
