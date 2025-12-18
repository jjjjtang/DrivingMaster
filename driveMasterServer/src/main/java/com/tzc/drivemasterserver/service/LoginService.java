package com.tzc.drivemasterserver.service;

import com.tzc.drivemasterserver.mapper.LogMapper;
import com.tzc.drivemasterserver.mapper.LoginMapper;
import com.tzc.drivemasterserver.model.Login;
import com.tzc.drivemasterserver.util.PasswordEncoderUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LoginService {

   @Resource
   private LoginMapper loginMapper;

   @Resource
   private LogMapper logMapper;

   @Resource
   private PasswordEncoderUtil passwordEncoder;

   /***
    * 登录验证
    */
   public Login login(String username, String password) {
      Login user = loginMapper.selectByUsername(username);
      if (user == null) {
         return null;
      }

      // 验证密码
      if (passwordEncoder.matches(password, user.getPassword())) {
         return user;
      }

      // 兼容旧数据
      String storedPassword = user.getPassword();
      if (passwordEncoder.md5Encode(password).equals(storedPassword) ||
              password.equals(storedPassword)) {
         // 自动升级为新加密方式
         String newPassword = passwordEncoder.encode(password);
         loginMapper.updatePassword(username, newPassword);
         return user;
      }

      return null;
   }

   /***
    * 注册用户
    */
   @Transactional
   public boolean register(Login login) {
      // 检查用户名是否已存在
      if (checkUserExist(login.getUsername())) {
         return false;
      }

      // 检查邮箱是否已存在
      if (login.getEmail() != null && !login.getEmail().isEmpty()
              && getUserByEmail(login.getEmail()) != null) {
         return false;
      }

      // 设置默认值
      if (login.getAdmin() == null || login.getAdmin().isEmpty()) {
         login.setAdmin("false");
      }

      // 加密密码
      String encodedPassword = passwordEncoder.encode(login.getPassword());
      login.setPassword(encodedPassword);

      return loginMapper.insert(login) > 0;
   }

   /***
    * 检查用户名是否存在
    */
   public boolean checkUserExist(String username) {
      return loginMapper.countByUsername(username) > 0;
   }

   /***
    * 根据用户名获取用户信息
    */
   public Login getUserByUsername(String username) {
      return loginMapper.selectByUsername(username);
   }

   /***
    * 根据邮箱获取用户信息
    */
   public Login getUserByEmail(String email) {
      return loginMapper.selectByEmail(email);
   }

   /***
    * 更新用户信息
    */
   @Transactional
   public boolean updateUser(Login login) {
      // 检查用户是否存在
      if (getUserByUsername(login.getUsername()) == null) {
         return false;
      }

      // 如果更新邮箱，检查邮箱是否被其他用户使用
      if (login.getEmail() != null && !login.getEmail().isEmpty()) {
         Login existingUser = getUserByEmail(login.getEmail());
         if (existingUser != null && !existingUser.getUsername().equals(login.getUsername())) {
            return false;
         }
      }

      return loginMapper.update(login) > 0;
   }

   /***
    * 更新密码
    */
   @Transactional
   public boolean updatePassword(String username, String oldPassword, String newPassword) {
      // 验证旧密码
      Login user = loginMapper.selectByUsernameAndPassword(username, oldPassword);
      if (user == null) {
         return false;
      }

      // 加密新密码
      String newEncodedPassword = passwordEncoder.encode(newPassword);
      return loginMapper.updatePassword(username, newEncodedPassword) > 0;
   }

   /***
    * 重置密码（管理员操作）
    */
   @Transactional
   public boolean resetPassword(String username, String newPassword) {
      // 检查用户是否存在
      if (getUserByUsername(username) == null) {
         return false;
      }

      // 加密新密码
      String newEncodedPassword = passwordEncoder.encode(newPassword);
      return loginMapper.updatePassword(username, newEncodedPassword) > 0;
   }

   /***
    * 更新头像
    */
   @Transactional
   public boolean updateAvatar(String username, String avatar) {
      // 检查用户是否存在
      if (getUserByUsername(username) == null) {
         return false;
      }

      return loginMapper.updateAvatar(username, avatar) > 0;
   }

   /***
    * 更新邮箱
    */
   @Transactional
   public boolean updateEmail(String username, String email) {
      // 检查用户是否存在
      if (getUserByUsername(username) == null) {
         return false;
      }

      // 检查邮箱是否已被其他用户使用
      Login existingUser = getUserByEmail(email);
      if (existingUser != null && !existingUser.getUsername().equals(username)) {
         return false;
      }

      return loginMapper.updateEmail(username, email) > 0;
   }

   /***
    * 删除用户
    */
   @Transactional
   public boolean deleteUser(String username) {
      return loginMapper.deleteByUsername(username) > 0;
   }

   /***
    * 批量删除用户
    */
   @Transactional
   public boolean batchDeleteUsers(List<String> usernames) {
      return loginMapper.batchDelete(usernames) > 0;
   }

   /***
    * 按用户名关键字搜索用户
    */
   public List<Login> searchUsersByUsername(String keyword) {
      return loginMapper.selectByUsernameKeyword(keyword);
   }

   /***
    * 按邮箱关键字搜索用户
    */
   public List<Login> searchUsersByEmail(String keyword) {
      return loginMapper.selectByEmailKeyword(keyword);
   }

   /***
    * 获取所有用户
    */
   public List<Login> getAllUsers() {
      return loginMapper.selectAll();
   }

   /***
    * 分页获取用户
    */
   public List<Login> getUsersByPage(int pageNum, int pageSize) {
      int offset = (pageNum - 1) * pageSize;
      return loginMapper.selectByPage(offset, pageSize);
   }

   /***
    * 根据管理员状态查询用户
    */
   public List<Login> getUsersByAdminStatus(String admin) {
      return loginMapper.selectByAdmin(admin);
   }

   /***
    * 按管理员状态分页查询
    */
   public List<Login> getUsersByAdminAndPage(String admin, int pageNum, int pageSize) {
      int offset = (pageNum - 1) * pageSize;
      return loginMapper.selectByAdminAndPage(admin, offset, pageSize);
   }

   /***
    * 获取用户总数
    */
   public int getUserCount() {
      return loginMapper.countAll();
   }

   /***
    * 根据管理员状态统计用户数
    */
   public int getUserCountByAdminStatus(String admin) {
      return loginMapper.countByAdminStatus(admin);
   }

   /***
    * 验证管理员权限
    */
   public boolean isAdmin(String username) {
      Login user = getUserByUsername(username);
      return user != null && "true".equals(user.getAdmin());
   }

   /***
    * 提升用户为管理员
    */
   @Transactional
   public boolean promoteToAdmin(String username) {
      Login user = getUserByUsername(username);
      if (user == null) {
         return false;
      }

      user.setAdmin("true");
      return loginMapper.update(user) > 0;
   }

   /***
    * 降级管理员为普通用户
    */
   @Transactional
   public boolean demoteFromAdmin(String username) {
      Login user = getUserByUsername(username);
      if (user == null) {
         return false;
      }

      user.setAdmin("false");
      return loginMapper.update(user) > 0;
   }
}