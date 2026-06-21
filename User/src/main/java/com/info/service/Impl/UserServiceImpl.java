package com.info.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.info.entity.*;
import com.info.entity.DTO.UserLoginDTO;
import com.info.entity.DTO.UserUpdateDTO;
import com.info.entity.VO.UserLoginVO;
import com.info.mapper.UserMapper;
import com.info.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public Result<UserLoginVO> login(UserLoginDTO dto) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, dto.getUsername());
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            return Result.error("用户不存在");
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            return Result.error("密码错误");
        }
        //生成token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        UserLoginVO vo = UserLoginVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .realName(user.getReal_name())
                .token(token)
                .expireTime(jwtUtil.getExpireTime(token))
                .build();

        return Result.success("登录成功",vo);
    }


    @Override
    public Result<String> register(UserLoginDTO dto) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, dto.getUsername());
        User user = userMapper.selectOne(queryWrapper);
        if (user != null) {
            return Result.error("用户已存在");
        }
        User newUser = new User();
        newUser.setUsername(dto.getUsername());
        newUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        newUser.setStatus("1");
        userMapper.insert(newUser);

        return Result.success("注册成功",null);
    }


    @Override
    public Result<UserLoginVO> me(Integer userId) {
        User user = userMapper.selectById(userId);

        if(user == null){
            return Result.error("用户不存在");
        }

        UserLoginVO vo = UserLoginVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .realName(user.getReal_name())
                .build();
        
        return Result.success("查询成功",vo);
    }

    @Override
    public Result<String> update(UserUpdateDTO dto) {
         Integer userId = UserContext.getUserId();
         User user = userMapper.selectById(userId);
         if(user == null){
             return Result.error("用户不存在");
         }

         if(dto.getEmail() != null){
             user.setEmail(dto.getEmail());
         }
         if(dto.getPhone() != null){
             user.setPhone(dto.getPhone());
         }
         if(dto.getRealName() != null){
             user.setReal_name(dto.getRealName());
         }
         if(dto.getGender() != null){
             user.setGender(dto.getGender());
         }

         userMapper.updateById(user);

         return Result.success("更新成功",null);
    }


}
