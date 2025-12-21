package com.tzc.drivemasterserver.service;

import com.tzc.drivemasterserver.mapper.UserMapper;
import com.tzc.drivemasterserver.model.User;
import com.tzc.drivemasterserver.model.dao.user.UserRegisterRequest;
import com.tzc.drivemasterserver.util.JwtUtil;
import com.tzc.drivemasterserver.util.PasswordUtil;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /** 注册 */
    public void register(UserRegisterRequest dto) {
        if (userMapper.selectByUsername(dto.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(PasswordUtil.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setAdmin("0");
        user.setAverage_score(0.0);
        user.setTotal_num(0L);

        userMapper.insert(user);
    }

    /** 登录 */
    public String login(String username, String password) {
        User dbUser = userMapper.selectByUsername(username);
        if (dbUser == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!PasswordUtil.matches(password, dbUser.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        return JwtUtil.generateToken(
                dbUser.getUserId(),
                dbUser.getUsername(),
                dbUser.getPassword() // 注意：这里是加密后的
        );
    }

    /** 根据 token 获取用户 */
    public User getUserByToken(String token) {
        Long userId = JwtUtil.getUserId(token);
        return userMapper.selectById(userId);
    }

    /***
     * 查询判断用户是否为管理员
     */
    public boolean isAdmin(Long userId) {
        User user = userMapper.selectById(userId);
        return user != null && "1".equals(user.getAdmin());
    }
}