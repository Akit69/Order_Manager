package com.info.controller;


import com.info.entity.DTO.UserLoginDTO;
import com.info.entity.DTO.UserUpdateDTO;
import com.info.entity.Result;
import com.info.entity.UserContext;
import com.info.entity.VO.UserLoginVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.info.service.UserService;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping ("/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO dto) {
        return userService.login(dto);
    }

    @PostMapping ("/register")
    public Result<String> register(@RequestBody UserLoginDTO dto) {
        return userService.register(dto);
    }

    @GetMapping("/me")
    public Result<UserLoginVO> me() {
        return userService.me(UserContext.getUserId());
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody UserUpdateDTO dto) {
        return userService.update(dto);
    }



}
