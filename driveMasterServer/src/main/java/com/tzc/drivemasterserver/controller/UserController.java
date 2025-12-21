package com.tzc.drivemasterserver.controller;

import com.tzc.drivemasterserver.model.User;
import com.tzc.drivemasterserver.model.dao.user.UserLoginRequest;
import com.tzc.drivemasterserver.model.dao.user.UserRegisterRequest;
import com.tzc.drivemasterserver.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /** 注册 */
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody UserRegisterRequest userRegisterRequest) {
        userService.register(userRegisterRequest);
        return Map.of("msg", "注册成功");
    }

    /** 登录 */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody UserLoginRequest userLoginRequest) {
        String token = userService.login(userLoginRequest.getUsername(), userLoginRequest.getPassword());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("msg", "登录成功");
        //判断是否为管理员
        User user = userService.getUserByToken(token);
        result.put("role", "1".equals(user.getAdmin()));
        return result;
    }

    /** 根据 token 获取用户信息 */
    @GetMapping("/info")
    public User info(@RequestHeader("Authorization") String token) {
        return userService.getUserByToken(token);
    }
}