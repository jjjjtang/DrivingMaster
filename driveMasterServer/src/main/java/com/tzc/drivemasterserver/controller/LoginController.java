package com.tzc.drivemasterserver.controller;

import com.tzc.drivemasterserver.model.Login;
import com.tzc.drivemasterserver.service.LoginService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/login")
@CrossOrigin(origins = "*", maxAge = 3600)
public class LoginController {

    @Resource
    private LoginService loginService;

    /**
     * 用户登录
     */
    @PostMapping("" +
            "" +
            "")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Login login, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        // 参数验证
        if (login.getUsername() == null || login.getUsername().trim().isEmpty()) {
            response.put("success", false);
            response.put("code", 400);
            response.put("message", "用户名不能为空");
            return ResponseEntity.badRequest().body(response);
        }

        if (login.getPassword() == null || login.getPassword().trim().isEmpty()) {
            response.put("success", false);
            response.put("code", 400);
            response.put("message", "密码不能为空");
            return ResponseEntity.badRequest().body(response);
        }

        // 执行登录验证
        Login user = loginService.login(login.getUsername(), login.getPassword());
        if (user != null) {
            // 存储用户信息到session
            session.setAttribute("currentUser", user);
            session.setAttribute("username", user.getUsername());

            // 移除密码信息再返回
            user.setPassword(null);

            response.put("success", true);
            response.put("code", 200);
            response.put("message", "登录成功");
            response.put("data", user);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("code", 401);
            response.put("message", "用户名或密码错误");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Login login) {
        Map<String, Object> response = new HashMap<>();

        // 参数验证
        if (login.getUsername() == null || login.getUsername().trim().isEmpty()) {
            response.put("success", false);
            response.put("code", 400);
            response.put("message", "用户名不能为空");
            return ResponseEntity.badRequest().body(response);
        }

        if (login.getPassword() == null || login.getPassword().trim().isEmpty()) {
            response.put("success", false);
            response.put("code", 400);
            response.put("message", "密码不能为空");
            return ResponseEntity.badRequest().body(response);
        }

        // 检查用户名是否已存在
        if (loginService.checkUserExist(login.getUsername())) {
            response.put("success", false);
            response.put("code", 409);
            response.put("message", "用户名已存在");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        // 检查邮箱是否已存在（如果提供了邮箱）
        if (login.getEmail() != null && !login.getEmail().trim().isEmpty()) {
            Login existingUser = loginService.getUserByEmail(login.getEmail());
            if (existingUser != null) {
                response.put("success", false);
                response.put("code", 409);
                response.put("message", "邮箱已被注册");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
        }

        // 执行注册
        boolean result = loginService.register(login);
        if (result) {
            // 移除密码信息再返回
            login.setPassword(null);

            response.put("success", true);
            response.put("code", 201);
            response.put("message", "注册成功");
            response.put("data", login);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            response.put("success", false);
            response.put("code", 500);
            response.put("message", "注册失败，请稍后重试");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 用户注销
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        session.invalidate();

        response.put("success", true);
        response.put("code", 200);
        response.put("message", "注销成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/current-user")
    public ResponseEntity<Map<String, Object>> getCurrentUser(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        Login user = (Login) session.getAttribute("currentUser");
        if (user != null) {
            // 移除密码信息再返回
            user.setPassword(null);

            response.put("success", true);
            response.put("code", 200);
            response.put("data", user);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("code", 401);
            response.put("message", "用户未登录");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    /**
     * 检查用户名是否可用
     */
    @GetMapping("/check-username/{username}")
    public ResponseEntity<Map<String, Object>> checkUsername(@PathVariable String username) {
        Map<String, Object> response = new HashMap<>();

        boolean exists = loginService.checkUserExist(username);

        response.put("success", true);
        response.put("code", 200);
        response.put("available", !exists);
        response.put("message", exists ? "用户名已存在" : "用户名可用");
        return ResponseEntity.ok(response);
    }

    /**
     * 更新用户信息（需要登录）
     */
    @PostMapping("/update-profile")
    public ResponseEntity<Map<String, Object>> updateProfile(@RequestBody Login login, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        // 验证登录状态
        Login currentUser = (Login) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("code", 401);
            response.put("message", "请先登录");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // 只能更新自己的信息
        login.setUsername(currentUser.getUsername());

        // 如果更新邮箱，检查邮箱是否被其他用户使用
        if (login.getEmail() != null && !login.getEmail().trim().isEmpty()) {
            Login existingUser = loginService.getUserByEmail(login.getEmail());
            if (existingUser != null && !existingUser.getUsername().equals(currentUser.getUsername())) {
                response.put("success", false);
                response.put("code", 409);
                response.put("message", "邮箱已被其他用户使用");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
        }

        // 执行更新
        boolean result = loginService.updateUser(login);
        if (result) {
            // 更新session中的用户信息
            Login updatedUser = loginService.getUserByUsername(currentUser.getUsername());
            updatedUser.setPassword(null);
            session.setAttribute("currentUser", updatedUser);

            response.put("success", true);
            response.put("code", 200);
            response.put("message", "资料更新成功");
            response.put("data", updatedUser);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("code", 500);
            response.put("message", "资料更新失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 修改密码（需要登录）
     */
    @PostMapping("/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(@RequestBody Map<String, String> request,
                                                              HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        // 验证登录状态
        Login currentUser = (Login) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("code", 401);
            response.put("message", "请先登录");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");

        // 参数验证
        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            response.put("success", false);
            response.put("code", 400);
            response.put("message", "原密码不能为空");
            return ResponseEntity.badRequest().body(response);
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            response.put("success", false);
            response.put("code", 400);
            response.put("message", "新密码不能为空");
            return ResponseEntity.badRequest().body(response);
        }

        // 执行密码修改
        boolean result = loginService.updatePassword(currentUser.getUsername(), oldPassword, newPassword);
        if (result) {
            response.put("success", true);
            response.put("code", 200);
            response.put("message", "密码修改成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("code", 400);
            response.put("message", "原密码错误");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 更新头像（需要登录）
     */
    @PostMapping("/update-avatar")
    public ResponseEntity<Map<String, Object>> updateAvatar(@RequestBody Map<String, String> request,
                                                            HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        // 验证登录状态
        Login currentUser = (Login) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("code", 401);
            response.put("message", "请先登录");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String avatar = request.get("avatar");

        // 参数验证
        if (avatar == null || avatar.trim().isEmpty()) {
            response.put("success", false);
            response.put("code", 400);
            response.put("message", "头像URL不能为空");
            return ResponseEntity.badRequest().body(response);
        }

        // 执行更新
        boolean result = loginService.updateAvatar(currentUser.getUsername(), avatar);
        if (result) {
            // 更新session中的用户信息
            Login updatedUser = loginService.getUserByUsername(currentUser.getUsername());
            updatedUser.setPassword(null);
            session.setAttribute("currentUser", updatedUser);

            response.put("success", true);
            response.put("code", 200);
            response.put("message", "头像更新成功");
            response.put("data", updatedUser);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("code", 500);
            response.put("message", "头像更新失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 根据用户名获取用户公开信息
     */
    @GetMapping("/user/{username}")
    public ResponseEntity<Map<String, Object>> getUserInfo(@PathVariable String username) {
        Map<String, Object> response = new HashMap<>();

        Login user = loginService.getUserByUsername(username);
        if (user != null) {
            // 移除敏感信息
            user.setPassword(null);

            response.put("success", true);
            response.put("code", 200);
            response.put("data", user);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("code", 404);
            response.put("message", "用户不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * 获取所有用户（仅管理员）
     */
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getAllUsers(HttpSession session,
                                                           @RequestParam(defaultValue = "1") int pageNum,
                                                           @RequestParam(defaultValue = "20") int pageSize) {
        Map<String, Object> response = new HashMap<>();

        // 验证管理员权限
        Login currentUser = (Login) session.getAttribute("currentUser");
        if (currentUser == null || !"true".equals(currentUser.getAdmin())) {
            response.put("success", false);
            response.put("code", 403);
            response.put("message", "需要管理员权限");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        // 参数验证
        if (pageNum < 1) pageNum = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 20;

        List<Login> users = loginService.getUsersByPage(pageNum, pageSize);
        int total = loginService.getUserCount();

        // 移除密码信息
        for (Login user : users) {
            user.setPassword(null);
        }

        response.put("success", true);
        response.put("code", 200);
        response.put("data", users);
        response.put("pageNum", pageNum);
        response.put("pageSize", pageSize);
        response.put("total", total);
        response.put("totalPages", (int) Math.ceil((double) total / pageSize));

        return ResponseEntity.ok(response);
    }

    /**
     * 删除用户（仅管理员）
     */
    @DeleteMapping("/user/{username}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable String username, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        // 验证管理员权限
        Login currentUser = (Login) session.getAttribute("currentUser");
        if (currentUser == null || !"true".equals(currentUser.getAdmin())) {
            response.put("success", false);
            response.put("code", 403);
            response.put("message", "需要管理员权限");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        // 不能删除自己
        if (currentUser.getUsername().equals(username)) {
            response.put("success", false);
            response.put("code", 400);
            response.put("message", "不能删除当前登录的用户");
            return ResponseEntity.badRequest().body(response);
        }

        // 检查用户是否存在
        Login userToDelete = loginService.getUserByUsername(username);
        if (userToDelete == null) {
            response.put("success", false);
            response.put("code", 404);
            response.put("message", "用户不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        // 执行删除
        boolean result = loginService.deleteUser(username);
        if (result) {
            response.put("success", true);
            response.put("code", 200);
            response.put("message", "用户删除成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("code", 500);
            response.put("message", "用户删除失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 提升用户为管理员（仅管理员）
     */
    @PostMapping("/promote/{username}")
    public ResponseEntity<Map<String, Object>> promoteToAdmin(@PathVariable String username, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        // 验证管理员权限
        Login currentUser = (Login) session.getAttribute("currentUser");
        if (currentUser == null || !"true".equals(currentUser.getAdmin())) {
            response.put("success", false);
            response.put("code", 403);
            response.put("message", "需要管理员权限");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        // 检查用户是否存在
        Login user = loginService.getUserByUsername(username);
        if (user == null) {
            response.put("success", false);
            response.put("code", 404);
            response.put("message", "用户不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        // 如果用户已经是管理员
        if ("true".equals(user.getAdmin())) {
            response.put("success", false);
            response.put("code", 400);
            response.put("message", "用户已经是管理员");
            return ResponseEntity.badRequest().body(response);
        }

        // 执行提升
        boolean result = loginService.promoteToAdmin(username);
        if (result) {
            response.put("success", true);
            response.put("code", 200);
            response.put("message", "用户已提升为管理员");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("code", 500);
            response.put("message", "操作失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 搜索用户（仅管理员）
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchUsers(@RequestParam(required = false) String username,
                                                           @RequestParam(required = false) String email,
                                                           HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        // 验证管理员权限
        Login currentUser = (Login) session.getAttribute("currentUser");
        if (currentUser == null || !"true".equals(currentUser.getAdmin())) {
            response.put("success", false);
            response.put("code", 403);
            response.put("message", "需要管理员权限");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        List<Login> users;
        if (username != null && !username.trim().isEmpty()) {
            users = loginService.searchUsersByUsername(username);
        } else if (email != null && !email.trim().isEmpty()) {
            users = loginService.searchUsersByEmail(email);
        } else {
            response.put("success", false);
            response.put("code", 400);
            response.put("message", "请提供用户名或邮箱作为搜索条件");
            return ResponseEntity.badRequest().body(response);
        }

        // 移除密码信息
        for (Login user : users) {
            user.setPassword(null);
        }

        response.put("success", true);
        response.put("code", 200);
        response.put("data", users);
        return ResponseEntity.ok(response);
    }

    /**
     * 验证用户会话
     */
    @GetMapping("/validate-session")
    public ResponseEntity<Map<String, Object>> validateSession(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        Login user = (Login) session.getAttribute("currentUser");
        if (user != null) {
            // 移除密码信息
            user.setPassword(null);

            response.put("success", true);
            response.put("code", 200);
            response.put("authenticated", true);
            response.put("data", user);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("code", 401);
            response.put("authenticated", false);
            response.put("message", "会话已过期或用户未登录");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    /**
     * 全局异常处理（简化版）
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("code", 500);
        response.put("message", "服务器内部错误: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}